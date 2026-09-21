package dev.paulobernardes.rankup.gui;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankUpMenu {

    private static final Map<Inventory, Map<Integer, Rank>>
            RANKS_POR_INVENTARIO =
            new HashMap<>();

    private final RankUp plugin;

    public RankUpMenu(RankUp plugin) {
        this.plugin = plugin;
    }

    public void abrir(Player jogador) {

        String titulo =
                plugin.getConfig()
                        .getString(
                                "gui.titulo",
                                ""
                        );

        int tamanho =
                plugin.getConfig()
                        .getInt(
                                "gui.tamanho",
                                54
                        );

        if (tamanho < 9) {
            tamanho = 9;
        }

        if (tamanho > 54) {
            tamanho = 54;
        }

        if (tamanho % 9 != 0) {
            tamanho = 54;
        }

        titulo =
                colorir(
                        titulo
                );

        Inventory inventario =
                Bukkit.createInventory(
                        null,
                        tamanho,
                        titulo
                );

        Map<Integer, Rank> ranks =
                new HashMap<>();

        RANKS_POR_INVENTARIO.put(
                inventario,
                ranks
        );

        criarPerfil(
                inventario,
                jogador
        );

        criarRanks(
                inventario,
                jogador,
                ranks
        );

        criarInformacao(
                inventario
        );

        jogador.openInventory(
                inventario
        );
    }

    private void criarPerfil(
            Inventory inventario,
            Player jogador
    ) {

        ItemStack cabeca =
                new ItemStack(
                        Material.PLAYER_HEAD
                );

        SkullMeta meta =
                (SkullMeta) cabeca.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.setOwningPlayer(
                jogador
        );

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        double dinheiro =
                plugin.getEconomia()
                        .getBalance(
                                jogador
                        );

        String horas =
                plugin.getHorasManager()
                        .formatarHorasPrecisao(
                                jogador
                        );

        Rank rankAtual =
                plugin.getRankManager()
                        .getRank(
                                nivelAtual
                        );

        String nomeRank;

        if (rankAtual != null) {
            nomeRank =
                    rankAtual.getNome();
        } else {
            nomeRank = "";
        }

        String nome =
                plugin.getConfig()
                        .getString(
                                "gui.textos.perfil.nome",
                                ""
                        );

        nome =
                substituirPlaceholders(
                        nome,
                        "%jogador%",
                        jogador.getName(),
                        "%rank%",
                        nomeRank,
                        "%dinheiro%",
                        formatarDinheiro(
                                dinheiro
                        ),
                        "%horas%",
                        horas
                );

        meta.setDisplayName(
                colorir(
                        nome
                )
        );

        List<String> lore =
                plugin.getConfig()
                        .getStringList(
                                "gui.textos.perfil.lore"
                        );

        List<String> loreFinal =
                new ArrayList<>();

        for (String linha : lore) {

            linha =
                    substituirPlaceholders(
                            linha,
                            "%jogador%",
                            jogador.getName(),
                            "%rank%",
                            nomeRank,
                            "%dinheiro%",
                            formatarDinheiro(
                                    dinheiro
                            ),
                            "%horas%",
                            horas
                    );

            loreFinal.add(
                    colorir(
                            linha
                    )
            );
        }

        meta.setLore(
                loreFinal
        );

        cabeca.setItemMeta(
                meta
        );

        inventario.setItem(
                4,
                cabeca
        );
    }

    private void criarRanks(
            Inventory inventario,
            Player jogador,
            Map<Integer, Rank> ranks
    ) {

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        List<Rank> lista =
                plugin.getRankManager()
                        .getRanks();

        int slot = 18;

        for (Rank rank : lista) {

            if (slot >= inventario.getSize()) {
                break;
            }

            ItemStack item;

            if (rank.getNivel() <= nivelAtual) {

                item =
                        criarRankLiberado(
                                rank,
                                nivelAtual
                        );

            } else {

                item =
                        criarRankBloqueado(
                                rank,
                                nivelAtual,
                                jogador
                        );
            }

            inventario.setItem(
                    slot,
                    item
            );

            ranks.put(
                    slot,
                    rank
            );

            slot++;
        }
    }

    private ItemStack criarRankLiberado(
            Rank rank,
            int nivelAtual
    ) {

        ItemStack item =
                new ItemStack(
                        Material.LIME_CONCRETE
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        String nome =
                plugin.getConfig()
                        .getString(
                                "gui.textos.ranks.conquistado.nome",
                                ""
                        );

        nome =
                substituirPlaceholders(
                        nome,
                        "%rank%",
                        rank.getNome()
                );

        meta.setDisplayName(
                colorir(
                        nome
                )
        );

        List<String> lore =
                criarLoreRank(
                        rank
                );

        String status;

        if (rank.getNivel() == nivelAtual) {

            status =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.atual.status",
                                    ""
                            );

        } else {

            status =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.conquistado.status",
                                    ""
                            );
        }

        status =
                substituirPlaceholders(
                        status,
                        "%rank%",
                        rank.getNome()
                );

        lore.add(
                colorir(
                        ""
                )
        );

        lore.add(
                colorir(
                        status
                )
        );

        meta.setLore(
                lore
        );

        item.setItemMeta(
                meta
        );

        return item;
    }

    private ItemStack criarRankBloqueado(
            Rank rank,
            int nivelAtual,
            Player jogador
    ) {

        Material material;

        if (rank.getNivel() == nivelAtual + 1) {

            material =
                    Material.YELLOW_CONCRETE;

        } else {

            material =
                    Material.RED_CONCRETE;
        }

        ItemStack item =
                new ItemStack(
                        material
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        List<String> lore =
                criarLoreRank(
                        rank
                );

        lore.add(
                colorir(
                        ""
                )
        );

        if (rank.getNivel() == nivelAtual + 1) {

            String nome =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.proximo.nome",
                                    ""
                            );

            nome =
                    substituirPlaceholders(
                            nome,
                            "%rank%",
                            rank.getNome()
                    );

            meta.setDisplayName(
                    colorir(
                            nome
                    )
            );

            if (plugin.getPromocaoManager()
                    .podeSubir(
                            jogador
                    )) {

                String requisitosAtendidos =
                        plugin.getConfig()
                                .getString(
                                        "gui.textos.ranks.proximo.requisitos-atendidos",
                                        ""
                                );

                requisitosAtendidos =
                        substituirPlaceholders(
                                requisitosAtendidos,
                                "%rank%",
                                rank.getNome()
                        );

                lore.add(
                        colorir(
                                requisitosAtendidos
                        )
                );

                lore.add(
                        colorir(
                                ""
                        )
                );

                String clique =
                        plugin.getConfig()
                                .getString(
                                        "gui.textos.ranks.proximo.clique",
                                        ""
                                );

                lore.add(
                        colorir(
                                clique
                        )
                );

            } else {

                String requisitosNaoAtendidos =
                        plugin.getConfig()
                                .getString(
                                        "gui.textos.ranks.proximo.requisitos-nao-atendidos",
                                        ""
                                );

                lore.add(
                        colorir(
                                requisitosNaoAtendidos
                        )
                );

                adicionarRequisitosRestantes(
                        lore,
                        jogador,
                        rank
                );
            }

        } else {

            String nome =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.bloqueado.nome",
                                    ""
                            );

            nome =
                    substituirPlaceholders(
                            nome,
                            "%rank%",
                            rank.getNome()
                    );

            meta.setDisplayName(
                    colorir(
                            nome
                    )
            );

            String status =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.bloqueado.status",
                                    ""
                            );

            lore.add(
                    colorir(
                            status
                    )
            );

            String anteriores =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.bloqueado.anteriores",
                                    ""
                            );

            lore.add(
                    colorir(
                            anteriores
                    )
            );
        }

        meta.setLore(
                lore
        );

        item.setItemMeta(
                meta
        );

        return item;
    }

    private void adicionarRequisitosRestantes(
            List<String> lore,
            Player jogador,
            Rank rank
    ) {

        double dinheiroAtual =
                plugin.getEconomia()
                        .getBalance(
                                jogador
                        );

        double dinheiroFaltante =
                Math.max(
                        0,
                        rank.getDinheiro()
                                - dinheiroAtual
                );

        double horasAtuais =
                plugin.getHorasManager()
                        .getHorasTotaisPrecisao(
                                jogador
                        );

        double horasFaltantes =
                Math.max(
                        0,
                        rank.getHoras()
                                - horasAtuais
                );

        if (dinheiroFaltante > 0) {

            String mensagem =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.proximo.dinheiro-faltante",
                                    ""
                            );

            mensagem =
                    substituirPlaceholders(
                            mensagem,
                            "%dinheiro%",
                            formatarDinheiro(
                                    dinheiroFaltante
                            ),
                            "%horas%",
                            formatarHoras(
                                    horasFaltantes
                            ),
                            "%rank%",
                            rank.getNome()
                    );

            lore.add(
                    colorir(
                            mensagem
                    )
            );
        }

        if (horasFaltantes > 0) {

            String mensagem =
                    plugin.getConfig()
                            .getString(
                                    "gui.textos.ranks.proximo.horas-faltantes",
                                    ""
                            );

            mensagem =
                    substituirPlaceholders(
                            mensagem,
                            "%dinheiro%",
                            formatarDinheiro(
                                    dinheiroFaltante
                            ),
                            "%horas%",
                            formatarHoras(
                                    horasFaltantes
                            ),
                            "%rank%",
                            rank.getNome()
                    );

            lore.add(
                    colorir(
                            mensagem
                    )
            );
        }
    }

    private List<String> criarLoreRank(
            Rank rank
    ) {

        List<String> lore =
                new ArrayList<>();

        List<String> configurada =
                plugin.getConfig()
                        .getStringList(
                                "ranks." +
                                        rank.getId() +
                                        ".lore"
                        );

        for (String linha : configurada) {

            linha =
                    substituirPlaceholders(
                            linha,
                            "%dinheiro%",
                            formatarDinheiro(
                                    rank.getDinheiro()
                            ),
                            "%horas%",
                            formatarHoras(
                                    rank.getHoras()
                            ),
                            "%rank%",
                            rank.getNome()
                    );

            lore.add(
                    colorir(
                            linha
                    )
            );
        }

        return lore;
    }

    private void criarInformacao(
            Inventory inventario
    ) {

        ItemStack item =
                new ItemStack(
                        Material.BOOK
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        String nome =
                plugin.getConfig()
                        .getString(
                                "gui.textos.informacao.nome",
                                ""
                        );

        meta.setDisplayName(
                colorir(
                        nome
                )
        );

        List<String> lore =
                plugin.getConfig()
                        .getStringList(
                                "gui.textos.informacao.lore"
                        );

        List<String> loreFinal =
                new ArrayList<>();

        for (String linha : lore) {

            loreFinal.add(
                    colorir(
                            linha
                    )
            );
        }

        meta.setLore(
                loreFinal
        );

        item.setItemMeta(
                meta
        );

        inventario.setItem(
                49,
                item
        );
    }

    private String formatarDinheiro(
            double valor
    ) {

        return String.format(
                java.util.Locale.US,
                "%.2f",
                valor
        );
    }

    private String formatarHoras(
            double horas
    ) {

        if (horas == (long) horas) {

            return (long) horas + "h";
        }

        return String.format(
                java.util.Locale.US,
                "%.2fh",
                horas
        );
    }

    private String substituirPlaceholders(
            String texto,
            String... placeholders
    ) {

        if (texto == null) {
            return "";
        }

        for (int i = 0;
             i + 1 < placeholders.length;
             i += 2) {

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

            texto =
                    texto.replace(
                            placeholder,
                            valor
                    );
        }

        return texto;
    }

    private String colorir(
            String texto
    ) {

        if (texto == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                texto
        );
    }

    public static boolean isMenuRankUp(
            RankUp plugin,
            String titulo
    ) {

        String tituloConfigurado =
                plugin.getConfig()
                        .getString(
                                "gui.titulo",
                                ""
                        );

        tituloConfigurado =
                ChatColor.translateAlternateColorCodes(
                        '&',
                        tituloConfigurado
                );

        return titulo.equals(
                tituloConfigurado
        );
    }

    public static Rank getRankDoSlot(
            Inventory inventario,
            int slot
    ) {

        Map<Integer, Rank> ranks =
                RANKS_POR_INVENTARIO.get(
                        inventario
                );

        if (ranks == null) {
            return null;
        }

        return ranks.get(
                slot
        );
    }

    public static void removerInventario(
            Inventory inventario
    ) {

        RANKS_POR_INVENTARIO.remove(
                inventario
        );
    }
}