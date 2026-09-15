package jogo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import jogo.model.Circulo;
import jogo.model.Entidade;
import jogo.model.Quadrado;

import java.util.ArrayList;
import java.util.List;

/** Jogo JavaFX: quadrados e círculos caem com gravidade static compartilhada. */
public class Main extends Application {
    private final List<Entidade> entidades = new ArrayList<>();
    private final List<Shape> views = new ArrayList<>();
    private final Pane root = new Pane();

    @Override
    public void start(Stage stage) {
        entidades.add(new Quadrado(100, 100, 30));
        entidades.add(new Circulo(300, 100, 20));
        entidades.add(new Quadrado(500, 100, 40));

        for (Entidade e : entidades) {
            views.add(criarView(e));
        }

        Scene scene = new Scene(root, 800, 600);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                for (Entidade en : entidades) {
                    en.setVy(-12);
                }
            }
        });

        root.setOnMouseClicked(e -> {
            Entidade nova = new Quadrado(e.getX(), e.getY(), 30);
            entidades.add(nova);
            views.add(criarView(nova));
            System.out.println("Total: " + Entidade.getTotalEntidades());
        });

        stage.setScene(scene);
        stage.setTitle("javafx-game — POO Avançada");
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < entidades.size(); i++) {
                    Entidade en = entidades.get(i);
                    Shape view = views.get(i);

                    en.atualizar();

                    if (en.getY() + en.getTamanho() > 550) {
                        en.setY(550 - en.getTamanho());
                        en.setVy(0);
                    }

                    if (view instanceof Rectangle r) {
                        r.setX(en.getX());
                        r.setY(en.getY());
                    } else if (view instanceof Circle c) {
                        c.setCenterX(en.getX());
                        c.setCenterY(en.getY());
                    }
                }
            }
        }.start();
    }

    private Shape criarView(Entidade e) {
        Shape view;
        if (e instanceof Quadrado) {
            view = new Rectangle(e.getX(), e.getY(), e.getTamanho(), e.getTamanho());
        } else {
            view = new Circle(e.getX(), e.getY(), e.getTamanho() / 2);
        }
        view.setFill(Color.hsb(Math.random() * 360, 0.7, 0.9));
        root.getChildren().add(view);
        return view;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
