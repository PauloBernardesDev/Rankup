package dev.paulobernardes.rankup.eventos;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.gui.RankUpMenu;
import dev.paulobernardes.rankup.modelos.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RankUpMenuListener implements Listener {

    private final RankUp plugin;

    public RankUpMenuListener(RankUp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void aoClicar(
            InventoryClickEvent evento
    ) {

        if (!(evento.getWhoClicked() instanceof Player jogador)) {
            return;
        }

        if (!RankUpMenu.isMenuRankUp(
                plugin,
                evento.getView().getTitle()
        )) {
            return;
        }

        evento.setCancelled(true);

        int slot =
                evento.getRawSlot();

        if (slot < 0
                || slot >= evento.getView()
                .getTopInventory()
                .getSize()) {

            return;
        }

        Rank rank =
                RankUpMenu.getRankDoSlot(
                        evento.getView()
                                .getTopInventory(),
                        slot
                );

        if (rank == null) {
            return;
        }

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        if (rank.getNivel() <= nivelAtual) {
            return;
        }

        if (rank.getNivel() != nivelAtual + 1) {

            plugin.getMensagemManager()
                    .enviar(
                            jogador,
                            "configuracao.mensagens.rankup.rank-bloqueado"
                    );

            return;
        }

        if (!plugin.getPromocaoManager()
                .podeSubir(jogador)) {

            String motivo =
                    plugin.getPromocaoManager()
                            .getMotivoBloqueio(
                                    jogador
                            );

            if (motivo != null) {

                plugin.getMensagemManager()
                        .enviarTexto(
                                jogador,
                                motivo
                        );
            }

            return;
        }

        boolean sucesso =
                plugin.getPromocaoManager()
                        .promover(
                                jogador
                        );

        if (!sucesso) {
            return;
        }

        plugin.getMensagemManager()
                .enviar(
                        jogador,
                        "configuracao.mensagens.rankup.sucesso",
                        "%rank%",
                        rank.getNome()
                );

        jogador.closeInventory();

        new RankUpMenu(plugin)
                .abrir(
                        jogador
                );
    }

    @EventHandler
    public void aoFechar(
            InventoryCloseEvent evento
    ) {

        if (!RankUpMenu.isMenuRankUp(
                plugin,
                evento.getView().getTitle()
        )) {
            return;
        }

        RankUpMenu.removerInventario(
                evento.getInventory()
        );
    }
}