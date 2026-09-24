import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.BattleResults;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class RodarBatalhas {
    public static void main(String[] args) throws Exception {
        // Sem argumentos: os 4 oponentes fixos de sempre (comportamento original,
        // usado pelos Topicos 15/16/17 do tutorial). Com um argumento: uma unica
        // batalha contra a classe passada em args[0], mesmo formato de saida.
        String[] oponentes = (args.length > 0)
                ? new String[]{args[0]}
                : new String[]{"sample.Corners", "sample.Crazy", "sample.Walls", "sample.RamFire"};

        RobocodeEngine engine = new RobocodeEngine(new File("/home/ensismoebius/robocode"));
        engine.setVisible(false);

        RobotSpecification[] todos = engine.getLocalRepository();
        RobotSpecification meuRobo = null;
        for (RobotSpecification s : todos) {
            if (s.getClassName().equals("meurobo.MeuRobo")) meuRobo = s;
        }
        if (meuRobo == null) throw new RuntimeException("MeuRobo nao encontrado no repositorio!");

        for (String oponenteClasse : oponentes) {
            RobotSpecification oponente = null;
            for (RobotSpecification s : todos) {
                if (s.getClassName().equals(oponenteClasse)) oponente = s;
            }
            if (oponente == null) {
                System.out.println("OPONENTE NAO ENCONTRADO: " + oponenteClasse);
                continue;
            }

            RobotSpecification[] participantes = {meuRobo, oponente};
            BattlefieldSpecification campo = new BattlefieldSpecification(800, 600);
            BattleSpecification battleSpec = new BattleSpecification(10, campo, participantes);

            final CountDownLatch latch = new CountDownLatch(1);
            final StringBuilder resultado = new StringBuilder();

            engine.addBattleListener(new BattleAdaptor() {
                @Override
                public void onBattleCompleted(BattleCompletedEvent e) {
                    resultado.append("=== MeuRobo vs ").append(oponenteClasse).append(" (10 rounds) ===\n");
                    for (BattleResults r : e.getIndexedResults()) {
                        resultado.append(String.format(
                                "%-25s score=%6d survival=%6d bulletDmg=%6d ramDmg=%6d 1st=%2d 2nd=%2d 3rd=%2d%n",
                                r.getTeamLeaderName(), r.getScore(), r.getSurvival(),
                                r.getBulletDamage(), r.getRamDamage(),
                                r.getFirsts(), r.getSeconds(), r.getThirds()));
                    }
                    latch.countDown();
                }

                @Override
                public void onBattleError(BattleErrorEvent e) {
                    resultado.append("ERRO: ").append(e.getError()).append("\n");
                    latch.countDown();
                }
            });

            engine.runBattle(battleSpec, true);
            latch.await();
            System.out.println(resultado);
        }

        engine.close();
        System.exit(0);
    }
}
