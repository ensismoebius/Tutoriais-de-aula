package jogo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jogo.dao.FaseDAO;
import jogo.dao.FaseDAOImpl;
import jogo.model.Fase;

/**
 * Controller (MVC): só orquestra — chama o Model/DAO e atualiza a View.
 * Nenhuma regra de negócio de verdade mora aqui.
 */
public class FaseController {

    private final FaseDAO dao = new FaseDAOImpl();

    @FXML
    private TableView<Fase> tabela;
    @FXML
    private TableColumn<Fase, String> colNome;
    @FXML
    private TableColumn<Fase, Integer> colDificuldade;
    @FXML
    private TableColumn<Fase, Double> colGravidade;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoDificuldade;
    @FXML
    private TextField campoGravidade;
    @FXML
    private Button btnSalvar;

    private Fase emEdicao;

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDificuldade.setCellValueFactory(new PropertyValueFactory<>("dificuldade"));
        colGravidade.setCellValueFactory(new PropertyValueFactory<>("gravidade"));
        carregarTabela();

        tabela.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Fase selecionada = tabela.getSelectionModel().getSelectedItem();
                if (selecionada != null) {
                    emEdicao = selecionada;
                    campoNome.setText(selecionada.getNome());
                    campoDificuldade.setText(String.valueOf(selecionada.getDificuldade()));
                    campoGravidade.setText(String.valueOf(selecionada.getGravidade()));
                    btnSalvar.setText("Atualizar");
                }
            }
        });
    }

    @FXML
    private void aoClicarSalvar() {
        if (emEdicao != null) {
            emEdicao.setNome(campoNome.getText());
            emEdicao.setDificuldade(Integer.parseInt(campoDificuldade.getText()));
            emEdicao.setGravidade(Double.parseDouble(campoGravidade.getText()));
            dao.atualizar(emEdicao);
            emEdicao = null;
            btnSalvar.setText("Salvar");
        } else {
            Fase fase = new Fase(
                    campoNome.getText(),
                    Integer.parseInt(campoDificuldade.getText()),
                    Double.parseDouble(campoGravidade.getText())
            );
            dao.salvar(fase);
        }
        carregarTabela();
        limparCampos();
    }

    @FXML
    private void aoClicarExcluir() {
        Fase selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            dao.excluir(selecionada.getId());
            carregarTabela();
        }
    }

    private void carregarTabela() {
        ObservableList<Fase> dados = FXCollections.observableArrayList(dao.listarTodas());
        tabela.setItems(dados);
    }

    private void limparCampos() {
        campoNome.clear();
        campoDificuldade.clear();
        campoGravidade.clear();
    }
}
