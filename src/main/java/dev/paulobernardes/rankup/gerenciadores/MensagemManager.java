package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MensagemManager {

    private final RankUp plugin;

    public MensagemManager(RankUp plugin) {
        this.plugin = plugin;
    }

    public String obter(
            String caminho
    ) {
        String mensagem =
                plugin.getConfig()
                        .getString(
                                caminho,
                                ""
                        );

        return prepararMensagem(
                mensagem
        );
    }

    public String obter(
            String caminho,
            String... placeholders
    ) {
        String mensagem =
                plugin.getConfig()
                        .getString(
                                caminho,
                                ""
                        );

        mensagem =
                substituirPlaceholders(
                        mensagem,
                        placeholders
                );

        return prepararMensagem(
                mensagem
        );
    }

    public void enviar(
            Player jogador,
            String caminho
    ) {
        String mensagem =
                obter(
                        caminho
                );

        if (mensagem.isBlank()) {
            return;
        }

        jogador.sendMessage(
                mensagem
        );
    }

    public void enviar(
            Player jogador,
            String caminho,
            String... placeholders
    ) {
        String mensagem =
                obter(
                        caminho,
                        placeholders
                );

        if (mensagem.isBlank()) {
            return;
        }

        jogador.sendMessage(
                mensagem
        );
    }

    public void enviar(
            CommandSender sender,
            String caminho
    ) {
        String mensagem =
                obter(
                        caminho
                );

        if (mensagem.isBlank()) {
            return;
        }

        sender.sendMessage(
                mensagem
        );
    }

    public void enviar(
            CommandSender sender,
            String caminho,
            String... placeholders
    ) {
        String mensagem =
                obter(
                        caminho,
                        placeholders
                );

        if (mensagem.isBlank()) {
            return;
        }

        sender.sendMessage(
                mensagem
        );
    }

    public void anunciar(
            String caminho
    ) {
        String mensagem =
                obter(
                        caminho
                );

        if (mensagem.isBlank()) {
            return;
        }

        plugin.getServer()
                .broadcastMessage(
                        mensagem
                );
    }

    public void anunciar(
            String caminho,
            String... placeholders
    ) {
        String mensagem =
                obter(
                        caminho,
                        placeholders
                );

        if (mensagem.isBlank()) {
            return;
        }

        plugin.getServer()
                .broadcastMessage(
                        mensagem
                );
    }

    private String substituirPlaceholders(
            String mensagem,
            String... placeholders
    ) {
        if (mensagem == null) {
            return "";
        }

        for (int i = 0; i + 1 < placeholders.length; i += 2) {

            String placeholder =
                    placeholders[i];

            String valor =
                    placeholders[i + 1];

            if (placeholder == null) {
                continue;
            }

            if (valor == null) {
                valor = "";
            }

            mensagem =
                    mensagem.replace(
                            placeholder,
                            valor
                    );
        }

        return mensagem;
    }

    private String prepararMensagem(
            String mensagem
    ) {
        if (mensagem == null) {
            return "";
        }

        String prefixo =
                plugin.getConfig()
                        .getString(
                                "configuracao.prefixo",
                                ""
                        );

        mensagem =
                mensagem.replace(
                        "%prefixo%",
                        prefixo
                );

        return ChatColor.translateAlternateColorCodes(
                '&',
                mensagem
        );
    }

    public void enviarTexto(
            CommandSender sender,
            String mensagem
    ) {
        if (mensagem == null || mensagem.isBlank()) {
            return;
        }

        sender.sendMessage(
                mensagem
        );
    }
}