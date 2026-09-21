package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HorasManager {

    private final RankUp plugin;

    public HorasManager(RankUp plugin) {
        this.plugin = plugin;
    }

    public long getTicksVanilla(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }

    public long getSegundosVanilla(Player player) {
        return getTicksVanilla(player) / 20L;
    }

    public double getHorasVanilla(Player player) {
        return getSegundosVanilla(player) / 3600.0;
    }

    public long getHorasInteirasVanilla(Player player) {
        return getSegundosVanilla(player) / 3600L;
    }

    public long getHorasBonus(UUID uuid) {
        plugin.getJogadorManager().registrarJogador(uuid);

        return plugin.getBancoDados().getHorasBonus(uuid);
    }

    public long getHorasTotais(Player player) {
        return getHorasInteirasVanilla(player)
                + getHorasBonus(player.getUniqueId());
    }

    public double getHorasTotaisPrecisao(Player player) {
        return getHorasVanilla(player)
                + getHorasBonus(player.getUniqueId());
    }

    public void adicionarHorasBonus(UUID uuid, long horas) {
        plugin.getJogadorManager().registrarJogador(uuid);

        plugin.getBancoDados().adicionarHorasBonus(
                uuid,
                horas
        );
    }

    public long removerHorasBonus(UUID uuid, long horas) {
        plugin.getJogadorManager().registrarJogador(uuid);

        long bonusAtual = plugin.getBancoDados().getHorasBonus(uuid);

        long novoBonus = Math.max(
                0L,
                bonusAtual - horas
        );

        plugin.getBancoDados().atualizarHorasBonus(
                uuid,
                novoBonus
        );

        return novoBonus;
    }

    public String formatarHoras(Player player) {

        long horas = getHorasTotais(player);

        return horas + "h";
    }

    public String formatarHorasPrecisao(Player player) {

        double horas = getHorasTotaisPrecisao(player);

        if (horas == (long) horas) {
            return (long) horas + "h";
        }

        return String.format(
                java.util.Locale.US,
                "%.2fh",
                horas
        );
    }
}