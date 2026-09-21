package dev.paulobernardes.rankup.banco;

import dev.paulobernardes.rankup.RankUp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BancoDados {

    private final RankUp plugin;
    private Connection connection;

    public BancoDados(RankUp plugin) {
        this.plugin = plugin;
    }

    public void conectar() {

        try {

            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            File arquivo = new File(
                    plugin.getDataFolder(),
                    "rankup.db"
            );

            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + arquivo.getAbsolutePath()
            );

            criarTabelas();

            plugin.getLogger().info(
                    "Banco de dados SQLite conectado."
            );

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Não foi possível conectar ao banco de dados SQLite."
            );

            e.printStackTrace();
        }
    }

    private void criarTabelas() {

        String sqlJogadores = """
                CREATE TABLE IF NOT EXISTS jogadores (
                    uuid TEXT PRIMARY KEY,
                    nivel_rank INTEGER NOT NULL DEFAULT 1,
                    horas_bonus INTEGER NOT NULL DEFAULT 0
                )
                """;

        String sqlPermissoes = """
                CREATE TABLE IF NOT EXISTS permissoes_rankup (
                    uuid TEXT NOT NULL,
                    permissao TEXT NOT NULL,
                    PRIMARY KEY (uuid, permissao)
                )
                """;

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    sqlJogadores
            );

            statement.executeUpdate(
                    sqlPermissoes
            );

            adicionarColunaHorasBonus();

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Não foi possível criar as tabelas do banco de dados."
            );

            e.printStackTrace();
        }
    }

    private void adicionarColunaHorasBonus() {

        String sql = """
                ALTER TABLE jogadores
                ADD COLUMN horas_bonus INTEGER NOT NULL DEFAULT 0
                """;

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    sql
            );

        } catch (SQLException e) {

            if (e.getMessage() == null
                    || !e.getMessage().contains(
                    "duplicate column name"
            )) {

                e.printStackTrace();
            }
        }
    }

    public boolean jogadorExiste(
            UUID uuid
    ) {

        String sql = """
                SELECT uuid
                FROM jogadores
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                return resultado.next();
            }

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    public void criarJogador(
            UUID uuid
    ) {

        String sql = """
                INSERT OR IGNORE INTO jogadores
                (uuid, nivel_rank, horas_bonus)
                VALUES (?, 1, 0)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public int getNivelRank(
            UUID uuid
    ) {

        String sql = """
                SELECT nivel_rank
                FROM jogadores
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                if (resultado.next()) {
                    return resultado.getInt(
                            "nivel_rank"
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 1;
    }

    public long getHorasBonus(
            UUID uuid
    ) {

        String sql = """
                SELECT horas_bonus
                FROM jogadores
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                if (resultado.next()) {
                    return resultado.getLong(
                            "horas_bonus"
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0L;
    }

    public void atualizarRank(
            UUID uuid,
            int nivelRank
    ) {

        String sql = """
                UPDATE jogadores
                SET nivel_rank = ?
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    nivelRank
            );

            statement.setString(
                    2,
                    uuid.toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void atualizarHorasBonus(
            UUID uuid,
            long horas
    ) {

        String sql = """
                UPDATE jogadores
                SET horas_bonus = ?
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    horas
            );

            statement.setString(
                    2,
                    uuid.toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void adicionarHorasBonus(
            UUID uuid,
            long horas
    ) {

        String sql = """
                UPDATE jogadores
                SET horas_bonus = horas_bonus + ?
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    horas
            );

            statement.setString(
                    2,
                    uuid.toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void registrarPermissaoRankUp(
            UUID uuid,
            String permissao
    ) {

        String sql = """
                INSERT OR IGNORE INTO permissoes_rankup
                (uuid, permissao)
                VALUES (?, ?)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            statement.setString(
                    2,
                    permissao
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public List<String> getPermissoesRankUp(
            UUID uuid
    ) {

        String sql = """
                SELECT permissao
                FROM permissoes_rankup
                WHERE uuid = ?
                """;

        List<String> permissoes =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (resultado.next()) {

                    permissoes.add(
                            resultado.getString(
                                    "permissao"
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return permissoes;
    }

    public void limparPermissoesRankUp(
            UUID uuid
    ) {

        String sql = """
                DELETE FROM permissoes_rankup
                WHERE uuid = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    uuid.toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    public List<UUID> getTodosJogadores() {

        String sql = """
            SELECT uuid
            FROM jogadores
            """;

        List<UUID> jogadores =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (resultado.next()) {

                jogadores.add(
                        UUID.fromString(
                                resultado.getString(
                                        "uuid"
                                )
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return jogadores;
    }

    public void fechar() {

        if (connection == null) {
            return;
        }

        try {

            if (!connection.isClosed()) {

                connection.close();

                plugin.getLogger().info(
                        "Banco de dados SQLite fechado."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}