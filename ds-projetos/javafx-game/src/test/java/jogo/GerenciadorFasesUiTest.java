package jogo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import jogo.model.Fase;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de UI real com TestFX, rodando headless (Monocle) — não é só o
 * Model isolado, é a tela GerenciadorFases sendo clicada de verdade.
 */
class GerenciadorFasesUiTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/jogo/fase.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 520, 420));
        stage.show();
    }

    @Test
    void salvarNovaFasePelaTelaAdicionaLinhaNaTabela() {
        clickOn("#campoNome").write("Fase Teste TestFX");
        clickOn("#campoDificuldade").write("3");
        clickOn("#campoGravidade").write("0.7");
        clickOn("#btnSalvar");

        TableView<Fase> tabela = lookup("#tabela").queryTableView();
        assertTrue(tabela.getItems().stream().anyMatch(f -> f.getNome().equals("Fase Teste TestFX")));
    }
}
