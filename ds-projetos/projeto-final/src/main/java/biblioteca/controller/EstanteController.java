package biblioteca.controller;

import biblioteca.dao.ItemDAO;
import biblioteca.dao.ItemDAOSQLite;
import biblioteca.model.Filme;
import biblioteca.model.GerenciadorDeAcervo;
import biblioteca.model.ItemDeAcervo;
import biblioteca.model.Livro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class EstanteController {

    private final ItemDAO dao = new ItemDAOSQLite();
    private GerenciadorDeAcervo gerenciador;

    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private TextField campoTitulo, campoAno, campoAvaliacao, campoExtra1, campoExtra2;
    @FXML
    private TableView<ItemDeAcervo> tabela;
    @FXML
    private TableColumn<ItemDeAcervo, String> colCategoria, colTitulo;
    @FXML
    private TableColumn<ItemDeAcervo, Integer> colAno, colAvaliacao;
    @FXML
    private Label labelResumo;

    @FXML
    public void initialize() {
        comboCategoria.setItems(FXCollections.observableArrayList("Livro", "Filme"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colAvaliacao.setCellValueFactory(new PropertyValueFactory<>("avaliacao"));

        gerenciador = new GerenciadorDeAcervo(dao.listarTodos());
        atualizarTabela();
    }

    @FXML
    private void aoClicarSalvar() {
        String categoria = comboCategoria.getValue();
        String titulo = campoTitulo.getText();
        int ano = Integer.parseInt(campoAno.getText());
        int avaliacao = Integer.parseInt(campoAvaliacao.getText());

        ItemDeAcervo item = "Livro".equals(categoria)
                ? new Livro(titulo, ano, avaliacao, campoExtra1.getText(), Integer.parseInt(campoExtra2.getText()))
                : new Filme(titulo, ano, avaliacao, campoExtra1.getText(), Integer.parseInt(campoExtra2.getText()));

        gerenciador.adicionar(item);
        dao.salvar(item);
        atualizarTabela();
        limparCampos();
    }

    @FXML
    private void aoClicarExcluir() {
        ItemDeAcervo selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            dao.remover(selecionado.getId());
            gerenciador = new GerenciadorDeAcervo(dao.listarTodos());
            atualizarTabela();
        }
    }

    private void atualizarTabela() {
        ObservableList<ItemDeAcervo> dados = FXCollections.observableArrayList(gerenciador.getItens());
        tabela.setItems(dados);
        labelResumo.setText("Total: " + gerenciador.getItens().size()
                + " | Média de avaliação: " + String.format("%.1f", gerenciador.mediaDeAvaliacao())
                + " | Por categoria: " + gerenciador.contarPorCategoria());
    }

    private void limparCampos() {
        campoTitulo.clear();
        campoAno.clear();
        campoAvaliacao.clear();
        campoExtra1.clear();
        campoExtra2.clear();
    }
}
