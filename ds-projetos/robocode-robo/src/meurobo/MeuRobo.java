package meurobo;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MeuRobo — radar lock + mira preditiva (Linear/Circular Targeting) + movimento
 * evasivo por Wave Surfing simplificado (com fallback de circling fixo) +
 * gerenciamento de energia, construído incrementalmente ao longo das aulas de
 * Robocode do tutorial DS.
 */
public class MeuRobo extends AdvancedRobot {

    // Número de "fatias" do GuessFactor. Ímpar: o bin central é o acerto direto.
    private static final int NUM_BINS = 31;

    // --- Mira preditiva: acompanha a variação de heading do inimigo entre scans. ---
    private double headingInimigoAnterior = Double.NaN; // NaN = ainda não temos um scan anterior
    private double variacaoHeadingInimigo = 0;

    // --- Wave surfing: detecção de disparo por queda de energia + ondas ativas. ---
    private double energiaAnteriorInimigo = 100.0;
    private final List<Onda> ondasAtivas = new ArrayList<>();
    private final double[] estatisticasPerigo = new double[NUM_BINS];

    @Override
    public void run() {
        setColors(Color.blue, Color.black, Color.cyan);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setTurnRadarRight(360);

        while (true) {
            // Ondas continuam avançando mesmo em ticks sem novo scan, então isso
            // precisa rodar a cada volta do loop, não só dentro de onScannedRobot.
            atualizarOndas();
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        try {
            double anguloAbsoluto = getHeading() + e.getBearing();
            double inimigoXAtual = getX() + e.getDistance() * Math.sin(Math.toRadians(anguloAbsoluto));
            double inimigoYAtual = getY() + e.getDistance() * Math.cos(Math.toRadians(anguloAbsoluto));

            // Wave surfing: detecta disparo do inimigo pela queda de energia,
            // antes de qualquer outra coisa (precisamos do estado "antes" do scan).
            double quedaEnergia = energiaAnteriorInimigo - e.getEnergy();
            if (quedaEnergia > 0.0 && quedaEnergia <= 3.0) {
                registrarNovaOnda(inimigoXAtual, inimigoYAtual, quedaEnergia);
            }
            energiaAnteriorInimigo = e.getEnergy();

            // Radar lock: gira o dobro do necessário para "grudar" no inimigo.
            setTurnRadarRight(normalizeAngle(anguloAbsoluto - getRadarHeading()) * 2);

            // Acompanha a variação de heading do inimigo entre scans (Circular Targeting).
            double headingInimigoAtual = e.getHeading();
            if (!Double.isNaN(headingInimigoAnterior)) {
                variacaoHeadingInimigo = normalizeAngle(headingInimigoAtual - headingInimigoAnterior);
            }
            headingInimigoAnterior = headingInimigoAtual;

            // Potência de tiro adaptada à energia restante e à distância.
            double potencia = calcularPotencia(e);

            // Mira preditiva (Linear/Circular Targeting), substituindo a mira direta.
            mirar(inimigoXAtual, inimigoYAtual, e.getVelocity(), headingInimigoAtual, potencia);
            setFire(potencia);

            // Movimento evasivo: Wave Surfing (cai para circling fixo sem onda ativa).
            mover(inimigoXAtual, inimigoYAtual, anguloAbsoluto);

            execute();
        } catch (Exception ex) {
            out.println("Erro em onScannedRobot: " + ex.getMessage());
        }
    }

    private double calcularPotencia(ScannedRobotEvent e) {
        if (getEnergy() < 20) {
            return 1;
        }
        return e.getDistance() < 200 ? 3 : 2;
    }

    // ============================================================
    // Mira preditiva (Linear Targeting / Circular Targeting)
    // ============================================================

    /**
     * Calcula o ângulo de mira usando a previsão circular (Passo 3) e gira o
     * canhão até ele. Defesa: se a potência produzir velocidade de bala inválida
     * (<= 0 ou NaN), cai de volta para mira direta na posição atual do inimigo.
     */
    private void mirar(double inimigoX, double inimigoY, double velocidadeInimigo,
                        double headingInimigoGraus, double potencia) {
        double velocidadeBala = 20 - 3 * potencia;
        double anguloMiraGraus;

        if (velocidadeBala > 0 && !Double.isNaN(velocidadeBala)) {
            double[] previsto = preverPosicaoCircular(inimigoX, inimigoY, velocidadeInimigo,
                    headingInimigoGraus, variacaoHeadingInimigo, getX(), getY(), velocidadeBala);
            anguloMiraGraus = Math.toDegrees(Math.atan2(previsto[0] - getX(), previsto[1] - getY()));
        } else {
            anguloMiraGraus = Math.toDegrees(Math.atan2(inimigoX - getX(), inimigoY - getY()));
        }

        setTurnGunRight(normalizeAngle(anguloMiraGraus - getGunHeading()));
    }

    /**
     * Linear Targeting por refinamento iterativo: prevê onde o inimigo estará
     * assumindo que ele mantém heading e velocidade constantes. Degrau pedagógico
     * do Passo 2 — o robô final usa preverPosicaoCircular, que cobre este caso
     * como caso particular (variação de heading == 0). Mantido aqui, sem uso em
     * produção, só para fins didáticos (compila e é referenciado no tutorial).
     */
    private double[] preverPosicaoLinear(double inimigoX, double inimigoY,
                                          double velocidadeInimigo, double headingInimigoGraus,
                                          double minhaX, double minhaY, double velocidadeBala) {
        double headingRad = Math.toRadians(headingInimigoGraus);
        double vx = velocidadeInimigo * Math.sin(headingRad);
        double vy = velocidadeInimigo * Math.cos(headingRad);

        double previstoX = inimigoX;
        double previstoY = inimigoY;
        double tempo = 0;

        for (int i = 0; i < 10; i++) {
            double dx = previstoX - minhaX;
            double dy = previstoY - minhaY;
            tempo = Math.sqrt(dx * dx + dy * dy) / velocidadeBala;
            previstoX = clamp(inimigoX + vx * tempo, 18, getBattleFieldWidth() - 18);
            previstoY = clamp(inimigoY + vy * tempo, 18, getBattleFieldHeight() - 18);
        }

        return new double[]{previstoX, previstoY};
    }

    /**
     * Circular Targeting por simulação passo a passo: avança a posição simulada
     * do inimigo tick a tick, aplicando a variação de heading observada entre os
     * dois últimos scans, até que uma bala disparada agora alcançaria essa
     * posição simulada. Generaliza o Linear Targeting (quando variacaoHeadingGraus
     * é 0, a trajetória simulada vira uma reta).
     */
    private double[] preverPosicaoCircular(double inimigoX, double inimigoY,
                                            double velocidadeInimigo, double headingInimigoGraus,
                                            double variacaoHeadingGraus,
                                            double minhaX, double minhaY, double velocidadeBala) {
        double simX = inimigoX, simY = inimigoY;
        double simHeadingGraus = headingInimigoGraus;
        int maxPassos = 500; // margem generosa; ver Passo 3 do tutorial

        for (int passo = 1; passo <= maxPassos; passo++) {
            simHeadingGraus = normalizeAngle(simHeadingGraus + variacaoHeadingGraus);
            double rad = Math.toRadians(simHeadingGraus);
            simX = clamp(simX + velocidadeInimigo * Math.sin(rad), 18, getBattleFieldWidth() - 18);
            simY = clamp(simY + velocidadeInimigo * Math.cos(rad), 18, getBattleFieldHeight() - 18);

            double dx = simX - minhaX, dy = simY - minhaY;
            double distanciaAteSimulado = Math.sqrt(dx * dx + dy * dy);
            if (velocidadeBala * passo >= distanciaAteSimulado) {
                break; // a bala alcançaria a posição simulada neste passo
            }
        }
        return new double[]{simX, simY};
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // ============================================================
    // Wave Surfing simplificado (GuessFactor, inimigo único, sem segmentação)
    // ============================================================

    private static class Onda {
        double origemX, origemY;
        long horaDisparo;
        double velocidadeBala;
        double anguloDiretoGraus; // ângulo absoluto da origem até minha posição no instante do disparo
    }

    private void registrarNovaOnda(double inimigoX, double inimigoY, double quedaEnergia) {
        Onda onda = new Onda();
        onda.origemX = inimigoX;
        onda.origemY = inimigoY;
        onda.horaDisparo = getTime();
        onda.velocidadeBala = 20 - 3 * quedaEnergia;
        onda.anguloDiretoGraus = Math.toDegrees(Math.atan2(getX() - inimigoX, getY() - inimigoY));
        ondasAtivas.add(onda);
    }

    private void atualizarOndas() {
        Iterator<Onda> it = ondasAtivas.iterator();
        while (it.hasNext()) {
            Onda onda = it.next();
            double raioAtual = onda.velocidadeBala * (getTime() - onda.horaDisparo);
            double distanciaAteMim = Math.hypot(getX() - onda.origemX, getY() - onda.origemY);
            if (raioAtual >= distanciaAteMim) {
                registrarResultado(onda);
                it.remove();
            }
        }
    }

    private void registrarResultado(Onda onda) {
        int bin = calcularBin(onda);
        estatisticasPerigo[bin] += 1;
    }

    private int calcularBin(Onda onda) {
        return calcularBinParaPosicao(onda, getX(), getY());
    }

    private int calcularBinParaPosicao(Onda onda, double x, double y) {
        double anguloAtualGraus = Math.toDegrees(Math.atan2(x - onda.origemX, y - onda.origemY));
        double anguloRelativoGraus = normalizeAngle(anguloAtualGraus - onda.anguloDiretoGraus);
        double anguloMaximoEscapeGraus = Math.toDegrees(Math.asin(8.0 / onda.velocidadeBala));
        double guessFactor = clamp(anguloRelativoGraus / anguloMaximoEscapeGraus, -1, 1);
        int bin = (int) Math.round((guessFactor + 1) / 2.0 * (NUM_BINS - 1));
        return Math.max(0, Math.min(NUM_BINS - 1, bin));
    }

    private Onda ondaMaisProxima() {
        Onda maisProxima = null;
        double menorDistancia = Double.MAX_VALUE;
        for (Onda onda : ondasAtivas) {
            double distancia = Math.hypot(getX() - onda.origemX, getY() - onda.origemY);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProxima = onda;
            }
        }
        return maisProxima;
    }

    private double escolherAnguloOrbita(Onda ondaMaisProxima) {
        double anguloAtual = Math.toDegrees(Math.atan2(getX() - ondaMaisProxima.origemX, getY() - ondaMaisProxima.origemY));

        double passoLookahead = 20; // px, um pequeno passo hipotético para avaliar cada sentido
        double xHorario = getX() + passoLookahead * Math.sin(Math.toRadians(anguloAtual + 10));
        double yHorario = getY() + passoLookahead * Math.cos(Math.toRadians(anguloAtual + 10));
        double xAntiHorario = getX() + passoLookahead * Math.sin(Math.toRadians(anguloAtual - 10));
        double yAntiHorario = getY() + passoLookahead * Math.cos(Math.toRadians(anguloAtual - 10));

        int binHorario = calcularBinParaPosicao(ondaMaisProxima, xHorario, yHorario);
        int binAntiHorario = calcularBinParaPosicao(ondaMaisProxima, xAntiHorario, yAntiHorario);

        boolean horarioMaisSeguro = estatisticasPerigo[binHorario] <= estatisticasPerigo[binAntiHorario];
        return anguloAtual + (horarioMaisSeguro ? 90 : -90);
    }

    /**
     * Decide o movimento: orbita a origem da onda mais próxima usando as
     * estatísticas de perigo (Wave Surfing). Sem onda ativa ainda, cai para o
     * comportamento do Tópico 14: circling fixo em torno da posição atual do
     * inimigo.
     */
    private void mover(double inimigoXAtual, double inimigoYAtual, double anguloAbsoluto) {
        double anguloOrbitaGraus;
        Onda maisProxima = ondaMaisProxima();
        if (maisProxima != null) {
            anguloOrbitaGraus = escolherAnguloOrbita(maisProxima);
        } else {
            anguloOrbitaGraus = anguloAbsoluto + 90;
        }
        setTurnRight(normalizeAngle(anguloOrbitaGraus - getHeading()));
        setAhead(80);
    }

    // ============================================================
    // Reações defensivas
    // ============================================================

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        try {
            setTurnRight(normalizeAngle(90 - e.getBearing()));
            setAhead(100);
            execute();
        } catch (Exception ex) {
            out.println("Erro em onHitByBullet: " + ex.getMessage());
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        try {
            setTurnRight(normalizeAngle(90 - e.getBearing()));
            setAhead(-50);
            execute();
        } catch (Exception ex) {
            out.println("Erro em onHitWall: " + ex.getMessage());
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        try {
            if (e.isMyFault()) {
                setTurnRight(90);
                setAhead(-100);
                execute();
            }
        } catch (Exception ex) {
            out.println("Erro em onHitRobot: " + ex.getMessage());
        }
    }

    private double normalizeAngle(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}
