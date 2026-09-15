package biblioteca.dao;

import biblioteca.model.Filme;
import biblioteca.model.ItemDeAcervo;
import biblioteca.model.Livro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOSQLite implements ItemDAO {

    @Override
    public void salvar(ItemDeAcervo item) {
        String sql = """
            INSERT OR REPLACE INTO item
            (id, categoria, titulo, ano, avaliacao, autor, paginas, diretor, duracaoMinutos)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getCategoria());
            ps.setString(3, item.getTitulo());
            ps.setInt(4, item.getAno());
            ps.setInt(5, item.getAvaliacao());
            if (item instanceof Livro l) {
                ps.setString(6, l.getAutor());
                ps.setInt(7, l.getPaginas());
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.INTEGER);
            } else if (item instanceof Filme f) {
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.INTEGER);
                ps.setString(8, f.getDiretor());
                ps.setInt(9, f.getDuracaoMinutos());
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao salvar item", ex);
        }
    }

    @Override
    public List<ItemDeAcervo> listarTodos() {
        List<ItemDeAcervo> resultado = new ArrayList<>();
        String sql = "SELECT * FROM item";
        try (Connection conn = Conexao.obter();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(reconstruir(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao listar itens", ex);
        }
        return resultado;
    }

    private ItemDeAcervo reconstruir(ResultSet rs) throws SQLException {
        String categoria = rs.getString("categoria");
        String titulo = rs.getString("titulo");
        int ano = rs.getInt("ano");
        int avaliacao = rs.getInt("avaliacao");

        ItemDeAcervo item = switch (categoria) {
            case "Livro" -> new Livro(titulo, ano, avaliacao, rs.getString("autor"), rs.getInt("paginas"));
            case "Filme" -> new Filme(titulo, ano, avaliacao, rs.getString("diretor"), rs.getInt("duracaoMinutos"));
            default -> throw new IllegalArgumentException("Categoria desconhecida: " + categoria);
        };
        item.setId(rs.getString("id"));
        return item;
    }

    @Override
    public void remover(String id) {
        String sql = "DELETE FROM item WHERE id = ?";
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao remover item", ex);
        }
    }
}
