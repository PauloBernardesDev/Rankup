package dev.paulobernardes.rankup.comandos;

import dev.paulobernardes.rankup.RankUp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HorasComando implements CommandExecutor {

    private final RankUp plugin;

    public HorasComando(RankUp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("rankup.admin")) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.comandos.sem-permissao"
                    );

            return true;
        }

        String comando =
                command.getName().toLowerCase();

        if (comando.equals("horas")) {

            consultarHoras(
                    sender,
                    args
            );

            return true;
        }

        if (comando.equals("addhoras")) {

            adicionarHoras(
                    sender,
                    args
            );

            return true;
        }

        if (comando.equals("removehoras")) {

            removerHoras(
                    sender,
                    args
            );

            return true;
        }

        return true;
    }

    private void consultarHoras(
            CommandSender sender,
            String[] args
    ) {

        if (args.length != 1) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.uso-horas"
                    );

            return;
        }

        String nomeJogador =
                args[0];

        OfflinePlayer jogador =
                Bukkit.getOfflinePlayer(
                        nomeJogador
                );

        if (!jogador.hasPlayedBefore()
                && !jogador.isOnline()) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.jogador-nao-encontrado",
                            "%jogador%",
                            nomeJogador
                    );

            return;
        }

        long horasBonus =
                plugin.getHorasManager()
                        .getHorasBonus(
                                jogador.getUniqueId()
                        );

        long horasVanilla = 0;

        if (jogador.isOnline()) {

            Player jogadorOnline =
                    jogador.getPlayer();

            if (jogadorOnline != null) {

                horasVanilla =
                        plugin.getHorasManager()
                                .getHorasInteirasVanilla(
                                        jogadorOnline
                                );
            }
        }

        long horasTotais =
                horasVanilla + horasBonus;

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.horas.consulta",
                        "%jogador%",
                        jogador.getName() != null
                                ? jogador.getName()
                                : nomeJogador,
                        "%horas%",
                        horasTotais + "h"
                );
    }

    private void adicionarHoras(
            CommandSender sender,
            String[] args
    ) {

        if (args.length != 2) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.uso-addhoras"
                    );

            return;
        }

        String nomeJogador =
                args[0];

        long horas;

        try {

            horas =
                    Long.parseLong(
                            args[1]
                    );

        } catch (NumberFormatException e) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.valor-invalido"
                    );

            return;
        }

        if (horas <= 0) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.quantidade-invalida"
                    );

            return;
        }

        OfflinePlayer jogador =
                Bukkit.getOfflinePlayer(
                        nomeJogador
                );

        if (!jogador.hasPlayedBefore()
                && !jogador.isOnline()) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.jogador-nao-encontrado",
                            "%jogador%",
                            nomeJogador
                    );

            return;
        }

        plugin.getHorasManager()
                .adicionarHorasBonus(
                        jogador.getUniqueId(),
                        horas
                );

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.horas.adicionadas",
                        "%jogador%",
                        jogador.getName() != null
                                ? jogador.getName()
                                : nomeJogador,
                        "%horas%",
                        String.valueOf(horas)
                );
    }

    private void removerHoras(
            CommandSender sender,
            String[] args
    ) {

        if (args.length != 2) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.uso-removehoras"
                    );

            return;
        }

        String nomeJogador =
                args[0];

        long horas;

        try {

            horas =
                    Long.parseLong(
                            args[1]
                    );

        } catch (NumberFormatException e) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.valor-invalido"
                    );

            return;
        }

        if (horas <= 0) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.quantidade-invalida"
                    );

            return;
        }

        OfflinePlayer jogador =
                Bukkit.getOfflinePlayer(
                        nomeJogador
                );

        if (!jogador.hasPlayedBefore()
                && !jogador.isOnline()) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.horas.jogador-nao-encontrado",
                            "%jogador%",
                            nomeJogador
                    );

            return;
        }

        long novoBonus =
                plugin.getHorasManager()
                        .removerHorasBonus(
                                jogador.getUniqueId(),
                                horas
                        );

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.horas.removidas",
                        "%jogador%",
                        jogador.getName() != null
                                ? jogador.getName()
                                : nomeJogador,
                        "%horas%",
                        String.valueOf(horas)
                );
    }
}