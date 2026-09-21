package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RecompensaManager {

    private final RankUp plugin;

    public RecompensaManager(RankUp plugin) {
        this.plugin = plugin;
    }

    public void executarRecompensas(
            Player jogador,
            Rank rank
    ) {

        for (String comando : rank.getComandos()) {

            if (comando == null || comando.isBlank()) {
                continue;
            }

            comando =
                    substituirPlaceholders(
                            comando,
                            jogador,
                            rank
                    );

            comando =
                    comando.trim();

            if (comando.startsWith("/")) {
                comando = comando.substring(1);
            }

            if (comando.isBlank()) {
                continue;
            }

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    comando
            );
        }
    }

    private String substituirPlaceholders(
            String comando,
            Player jogador,
            Rank rank
    ) {

        comando =
                comando.replace(
                        "%jogador%",
                        jogador.getName()
                );

        comando =
                comando.replace(
                        "%uuid%",
                        jogador.getUniqueId().toString()
                );

        comando =
                comando.replace(
                        "%rank%",
                        rank.getNome()
                );

        comando =
                comando.replace(
                        "%nivel%",
                        String.valueOf(
                                rank.getNivel()
                        )
                );

        return comando;
    }
}