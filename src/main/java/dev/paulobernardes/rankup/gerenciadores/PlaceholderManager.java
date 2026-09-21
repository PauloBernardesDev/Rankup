package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    private final RankUp plugin;

    public PlaceholderManager(RankUp plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rankup";
    }

    @Override
    public @NotNull String getAuthor() {
        return "PauloBernardesDev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(
            Player player,
            @NotNull String params
    ) {

        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("horas")) {

            return String.valueOf(
                    plugin.getHorasManager()
                            .getHorasTotais(player)
            );
        }

        if (params.equalsIgnoreCase("horas_bonus")) {

            return String.valueOf(
                    plugin.getHorasManager()
                            .getHorasBonus(
                                    player.getUniqueId()
                            )
            );
        }

        if (params.equalsIgnoreCase("horas_minecraft")) {

            return String.valueOf(
                    plugin.getHorasManager()
                            .getHorasInteirasVanilla(player)
            );
        }

        if (params.equalsIgnoreCase("rank")) {

            int nivel = plugin.getJogadorManager()
                    .getNivelRank(
                            player.getUniqueId()
                    );

            Rank rank = plugin.getRankManager()
                    .getRank(nivel);

            if (rank == null) {
                return String.valueOf(nivel);
            }

            return rank.getNome();
        }

        if (params.equalsIgnoreCase("rank_nivel")) {

            return String.valueOf(
                    plugin.getJogadorManager()
                            .getNivelRank(
                                    player.getUniqueId()
                            )
            );
        }

        return null;
    }
}