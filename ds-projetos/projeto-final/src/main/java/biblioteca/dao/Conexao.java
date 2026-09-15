package biblioteca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexao {
    private static final String URL = "jdbc:sqlite:estante.db";

    private Conexao() {
    }

    public static Connection obter() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializar() throws SQLException {
        try (Connection conn = obter(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS item (
                    id TEXT PRIMARY KEY,
                    categoria TEXT NOT NULL,
                    titulo TEXT NOT NULL,
                    ano INTEGER,
                    avaliacao INTEGER,
                    autor TEXT,
                    paginas INTEGER,
                    diretor TEXT,
                    duracaoMinutos INTEGER
                )
            """);
        }
    }
}
