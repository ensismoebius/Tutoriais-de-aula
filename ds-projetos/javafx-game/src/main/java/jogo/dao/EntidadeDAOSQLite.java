package jogo.dao;

import jogo.model.Entidade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EntidadeDAOSQLite implements EntidadeDAO {

    @Override
    public void salvar(Entidade e) {
        String sql = "INSERT OR REPLACE INTO entidade (id, tipo, x, y, tamanho) VALUES (?,?,?,?,?)";
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getClass().getSimpleName());
            ps.setDouble(3, e.getX());
            ps.setDouble(4, e.getY());
            ps.setDouble(5, e.getTamanho());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao salvar entidade", ex);
        }
    }

    @Override
    public List<Entidade> listarTodas() {
        List<Entidade> resultado = new ArrayList<>();
        String sql = "SELECT * FROM entidade";
        try (Connection conn = Conexao.obter();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(reconstruir(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao listar entidades", ex);
        }
        return resultado;
    }

    private Entidade reconstruir(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        double x = rs.getDouble("x"), y = rs.getDouble("y"), tamanho = rs.getDouble("tamanho");
        Entidade e = EntidadeFactory.criar(tipo, x, y, tamanho);
        e.setId(rs.getString("id"));
        return e;
    }

    @Override
    public void remover(String id) {
        String sql = "DELETE FROM entidade WHERE id = ?";
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao remover entidade", ex);
        }
    }
}
