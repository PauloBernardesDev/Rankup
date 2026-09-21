package dev.paulobernardes.rankup.comandos;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.gui.RankUpMenu;
import dev.paulobernardes.rankup.modelos.Rank;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankUpComando implements CommandExecutor {

    private final RankUp plugin;
    private final RankUpMenu menu;

    public RankUpComando(RankUp plugin) {
        this.plugin = plugin;
        this.menu = new RankUpMenu(plugin);
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (args.length == 0) {

            if (!(sender instanceof Player jogador)) {

                plugin.getMensagemManager()
                        .enviar(
                                sender,
                                "configuracao.mensagens.comandos.jogador-apenas"
                        );

                return true;
            }

            menu.abrir(jogador);

            return true;
        }

        if (args.length == 1
                && args[0].equalsIgnoreCase("reload")) {

            recarregar(sender);

            return true;
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("set")) {

            definirRank(
                    sender,
                    args
            );

            return true;
        }

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.comandos.uso-rankup"
                );

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.comandos.uso-rankup-set"
                );

        return true;
    }

    private void recarregar(
            CommandSender sender
    ) {

        if (sender instanceof Player jogador
                && !jogador.isOp()) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.comandos.sem-permissao-reload"
                    );

            return;
        }

        plugin.reloadConfig();

        plugin.getRankManager()
                .recarregarRanks();

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.comandos.reload"
                );

        plugin.getLogger().info(
                "Configuração recarregada através de /rankup reload."
        );
    }

    private void definirRank(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission("rankup.admin")) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.comandos.sem-permissao"
                    );

            return;
        }

        String nomeJogador =
                args[1];

        int nivel;

        try {

            nivel =
                    Integer.parseInt(
                            args[2]
                    );

        } catch (NumberFormatException e) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.administrativo.rank-numero"
                    );

            return;
        }

        Rank rank =
                plugin.getRankManager()
                        .getRank(
                                nivel
                        );

        if (rank == null) {

            plugin.getMensagemManager()
                    .enviar(
                            sender,
                            "configuracao.mensagens.administrativo.rank-inexistente",
                            "%rank%",
                            String.valueOf(
                                    nivel
                            )
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
                            "configuracao.mensagens.administrativo.jogador-nao-encontrado",
                            "%jogador%",
                            nomeJogador
                    );

            return;
        }

        plugin.getJogadorManager()
                .definirRank(
                        jogador.getUniqueId(),
                        nivel
                );

        plugin.getPermissaoManager()
                .aplicarPermissoesDoRank(
                        jogador.getUniqueId(),
                        nivel
                );

        plugin.getMensagemManager()
                .enviar(
                        sender,
                        "configuracao.mensagens.administrativo.rank-definido",
                        "%jogador%",
                        jogador.getName() != null
                                ? jogador.getName()
                                : nomeJogador,
                        "%rank%",
                        rank.getNome()
                );
    }
}