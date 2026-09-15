package meurobo;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.Color;

/**
 * MeuRobo — radar lock + movimento circular + gerenciamento de energia,
 * construído incrementalmente ao longo das aulas de Robocode do tutorial DS.
 */
public class MeuRobo extends AdvancedRobot {

    @Override
    public void run() {
        setColors(Color.blue, Color.black, Color.cyan);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setTurnRadarRight(360);

        while (true) {
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        try {
            double anguloAbsoluto = getHeading() + e.getBearing();

            // Radar lock: gira o dobro do necessário para "grudar" no inimigo.
            setTurnRadarRight(normalizeAngle(anguloAbsoluto - getRadarHeading()) * 2);

            // Mira.
            setTurnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()));

            // Potência de tiro adaptada à energia restante e à distância.
            double potencia = calcularPotencia(e);
            setFire(potencia);

            // Movimento circular (strafing) perpendicular ao inimigo.
            double anguloPerpendicular = anguloAbsoluto + 90;
            setTurnRight(normalizeAngle(anguloPerpendicular - getHeading()));
            setAhead(80);

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
