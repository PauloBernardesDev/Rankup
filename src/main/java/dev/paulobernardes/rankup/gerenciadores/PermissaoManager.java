package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissaoManager {

    private final RankUp plugin;
    private LuckPerms luckPerms;

    public PermissaoManager(
            RankUp plugin
    ) {

        this.plugin = plugin;

        conectarLuckPerms();
    }

    private void conectarLuckPerms() {

        try {

            luckPerms =
                    LuckPermsProvider.get();

            plugin.getLogger().info(
                    "API do LuckPerms conectada."
            );

        } catch (IllegalStateException e) {

            plugin.getLogger().severe(
                    "Não foi possível conectar à API do LuckPerms."
            );
        }
    }

    public void aplicarPermissoesDoRank(
            User user,
            int nivelRank
    ) {

        if (luckPerms == null) {
            return;
        }

        removerPermissoesDoRankUp(
                user
        );

        List<String> permissoes =
                obterPermissoesCumulativas(
                        nivelRank
                );

        for (String permissao : permissoes) {

            if (permissao == null
                    || permissao.isBlank()) {

                continue;
            }

            Node node =
                    Node.builder(
                                    permissao
                            )
                            .value(true)
                            .build();

            user.data().add(
                    node
            );

            plugin.getBancoDados()
                    .registrarPermissaoRankUp(
                            user.getUniqueId(),
                            permissao
                    );
        }

        luckPerms.getUserManager()
                .saveUser(
                        user
                );
    }

    public void removerPermissoesDoRankUp(
            User user
    ) {

        if (luckPerms == null) {
            return;
        }

        List<String> permissoes =
                plugin.getBancoDados()
                        .getPermissoesRankUp(
                                user.getUniqueId()
                        );

        for (String permissao : permissoes) {

            if (permissao == null
                    || permissao.isBlank()) {

                continue;
            }

            Node node =
                    Node.builder(
                                    permissao
                            )
                            .value(true)
                            .build();

            user.data().remove(
                    node
            );
        }

        plugin.getBancoDados()
                .limparPermissoesRankUp(
                        user.getUniqueId()
                );

        luckPerms.getUserManager()
                .saveUser(
                        user
                );
    }

    public void sincronizarTodosJogadores() {

        if (luckPerms == null) {

            plugin.getLogger().warning(
                    "Não foi possível sincronizar permissões: LuckPerms não está conectado."
            );

            return;
        }

        List<UUID> jogadores =
                plugin.getBancoDados()
                        .getTodosJogadores();

        plugin.getLogger().info(
                "Iniciando sincronização das permissões do RankUp para "
                        + jogadores.size()
                        + " jogadores."
        );

        for (UUID uuid : jogadores) {

            luckPerms.getUserManager()
                    .loadUser(
                            uuid
                    )
                    .thenAccept(
                            user -> Bukkit.getScheduler()
                                    .runTask(
                                            plugin,
                                            () -> sincronizarJogador(
                                                    user
                                            )
                                    )
                    );
        }
    }

    private void sincronizarJogador(
            User user
    ) {

        removerPermissoesAntigasDoRankUp(
                user
        );

        int nivel =
                plugin.getJogadorManager()
                        .getNivelRank(
                                user.getUniqueId()
                        );

        List<String> permissoes =
                obterPermissoesCumulativas(
                        nivel
                );

        for (String permissao : permissoes) {

            if (permissao == null
                    || permissao.isBlank()) {

                continue;
            }

            Node node =
                    Node.builder(
                                    permissao
                            )
                            .value(true)
                            .build();

            user.data().add(
                    node
            );

            plugin.getBancoDados()
                    .registrarPermissaoRankUp(
                            user.getUniqueId(),
                            permissao
                    );
        }

        luckPerms.getUserManager()
                .saveUser(
                        user
                );
    }

    private void removerPermissoesAntigasDoRankUp(
            User user
    ) {

        List<String> permissoes =
                obterTodasPermissoesDosRanks();

        for (String permissao : permissoes) {

            if (permissao == null
                    || permissao.isBlank()) {

                continue;
            }

            Node node =
                    Node.builder(
                                    permissao
                            )
                            .value(true)
                            .build();

            user.data().remove(
                    node
            );
        }

        plugin.getBancoDados()
                .limparPermissoesRankUp(
                        user.getUniqueId()
                );
    }

    public List<String> obterPermissoesCumulativas(
            int nivelRank
    ) {

        List<String> permissoes =
                new ArrayList<>();

        for (Rank rank :
                plugin.getRankManager()
                        .getRanks()) {

            if (rank.getNivel() > nivelRank) {
                break;
            }

            for (String permissao :
                    rank.getPermissoes()) {

                if (permissao == null
                        || permissao.isBlank()) {

                    continue;
                }

                if (!permissoes.contains(
                        permissao
                )) {

                    permissoes.add(
                            permissao
                    );
                }
            }
        }

        return permissoes;
    }

    public List<String> obterTodasPermissoesDosRanks() {

        List<String> permissoes =
                new ArrayList<>();

        for (Rank rank :
                plugin.getRankManager()
                        .getRanks()) {

            for (String permissao :
                    rank.getPermissoes()) {

                if (permissao == null
                        || permissao.isBlank()) {

                    continue;
                }

                if (!permissoes.contains(
                        permissao
                )) {

                    permissoes.add(
                            permissao
                    );
                }
            }
        }

        return permissoes;
    }

    public void aplicarPermissoesDoJogador(
            User user
    ) {

        int nivel =
                plugin.getJogadorManager()
                        .getNivelRank(
                                user.getUniqueId()
                        );

        aplicarPermissoesDoRank(
                user,
                nivel
        );
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public void aplicarPermissoesDoRank(
            UUID uuid,
            int nivelRank
    ) {

        if (luckPerms == null) {
            return;
        }

        luckPerms.getUserManager()
                .loadUser(uuid)
                .thenAccept(user -> {

                    aplicarPermissoesDoRank(
                            user,
                            nivelRank
                    );

                });
    }
}