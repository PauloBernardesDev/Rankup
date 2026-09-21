package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankManager {

    private final RankUp plugin;
    private final List<Rank> ranks = new ArrayList<>();

    public RankManager(RankUp plugin) {
        this.plugin = plugin;
        carregarRanks();
    }

    private void carregarRanks() {

        ConfigurationSection secao =
                plugin.getConfig()
                        .getConfigurationSection(
                                "ranks"
                        );

        if (secao == null) {

            plugin.getLogger().warning(
                    "Nenhuma seção 'ranks' foi encontrada no config.yml."
            );

            return;
        }

        for (String id : secao.getKeys(false)) {

            String caminho =
                    "ranks." + id;

            int nivel =
                    plugin.getConfig()
                            .getInt(
                                    caminho + ".nivel"
                            );

            String nome =
                    plugin.getConfig()
                            .getString(
                                    caminho + ".nome",
                                    id
                            );

            double dinheiro =
                    plugin.getConfig()
                            .getDouble(
                                    caminho + ".dinheiro",
                                    0
                            );

            double horas =
                    plugin.getConfig()
                            .getDouble(
                                    caminho + ".horas",
                                    0
                            );

            List<String> permissoes =
                    plugin.getConfig()
                            .getStringList(
                                    caminho + ".permissoes"
                            );

            List<String> comandos =
                    plugin.getConfig()
                            .getStringList(
                                    caminho + ".comandos"
                            );

            Rank rank =
                    new Rank(
                            id,
                            nivel,
                            nome,
                            dinheiro,
                            horas,
                            permissoes,
                            comandos
                    );

            ranks.add(
                    rank
            );
        }

        ranks.sort(
                Comparator.comparingInt(
                        Rank::getNivel
                )
        );

        plugin.getLogger().info(
                ranks.size() +
                        " ranks carregados."
        );
    }

    public void recarregarRanks() {

        ranks.clear();

        carregarRanks();
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public Rank getRank(
            int nivel
    ) {

        for (Rank rank : ranks) {

            if (rank.getNivel() == nivel) {
                return rank;
            }
        }

        return null;
    }

    public Rank getProximoRank(
            int nivelAtual
    ) {

        for (Rank rank : ranks) {

            if (rank.getNivel() > nivelAtual) {
                return rank;
            }
        }

        return null;
    }

    public Rank getPrimeiroRank() {

        if (ranks.isEmpty()) {
            return null;
        }

        return ranks.get(0);
    }

    public int getQuantidadeRanks() {
        return ranks.size();
    }
}