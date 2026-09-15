package biblioteca;

import biblioteca.dao.Conexao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Minha Estante: gerenciador pessoal de livros e filmes (Projeto Final). */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Conexao.inicializar();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/estante.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 760, 460));
        stage.setTitle("Minha Estante");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
