package jogo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Ferramenta JavaFX independente: CRUD de fases, view em FXML + FaseController. */
public class GerenciadorFasesApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/jogo/fase.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 520, 420));
        stage.setTitle("Gerenciador de Fases");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
