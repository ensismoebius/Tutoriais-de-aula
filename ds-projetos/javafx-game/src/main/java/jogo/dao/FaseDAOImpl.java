package jogo.dao;

import jogo.model.Fase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FaseDAOImpl implements FaseDAO {
    private static final String URL = "jdbc:sqlite:fases.db";

    public FaseDAOImpl() {
        criarTabelaSeNaoExiste();
    }

    private void criarTabelaSeNaoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fase (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                dificuldade INTEGER NOT NULL,
                gravidade REAL NOT NULL
            )
        """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void salvar(Fase fase) {
        String sql = "INSERT INTO fase (nome, dificuldade, gravidade) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNome());
            stmt.setInt(2, fase.getDificuldade());
            stmt.setDouble(3, fase.getGravidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Fase> listarTodas() {
        List<Fase> fases = new ArrayList<>();
        String sql = "SELECT * FROM fase";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fase f = new Fase(rs.getString("nome"), rs.getInt("dificuldade"), rs.getDouble("gravidade"));
                f.setId(rs.getInt("id"));
                fases.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fases;
    }

    @Override
    public void atualizar(Fase fase) {
        String sql = "UPDATE fase SET nome = ?, dificuldade = ?, gravidade = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNome());
            stmt.setInt(2, fase.getDificuldade());
            stmt.setDouble(3, fase.getGravidade());
            stmt.setInt(4, fase.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM fase WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
