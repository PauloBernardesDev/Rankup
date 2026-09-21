package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.modelos.Rank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class PromocaoManager {

    private final RankUp plugin;
    private final Economy economia;

    public PromocaoManager(RankUp plugin) {
        this.plugin = plugin;
        this.economia = plugin.getEconomia();
    }

    public boolean podeSubir(Player jogador) {

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        Rank proximoRank =
                plugin.getRankManager()
                        .getProximoRank(
                                nivelAtual
                        );

        if (proximoRank == null) {
            return false;
        }

        double dinheiroAtual =
                economia.getBalance(
                        jogador
                );

        double dinheiroNecessario =
                proximoRank.getDinheiro();

        double horasAtuais =
                plugin.getHorasManager()
                        .getHorasTotaisPrecisao(
                                jogador
                        );

        double horasNecessarias =
                proximoRank.getHoras();

        return dinheiroAtual >= dinheiroNecessario
                && horasAtuais >= horasNecessarias;
    }

    public String getMotivoBloqueio(
            Player jogador
    ) {

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        Rank proximoRank =
                plugin.getRankManager()
                        .getProximoRank(
                                nivelAtual
                        );

        if (proximoRank == null) {

            return plugin.getMensagemManager()
                    .obter(
                            "configuracao.mensagens.rankup.rank-maximo"
                    );
        }

        double dinheiroAtual =
                economia.getBalance(
                        jogador
                );

        double dinheiroNecessario =
                proximoRank.getDinheiro();

        double horasAtuais =
                plugin.getHorasManager()
                        .getHorasTotaisPrecisao(
                                jogador
                        );

        double horasNecessarias =
                proximoRank.getHoras();

        boolean possuiDinheiro =
                dinheiroAtual >= dinheiroNecessario;

        boolean possuiHoras =
                horasAtuais >= horasNecessarias;

        if (!possuiDinheiro && !possuiHoras) {

            double dinheiroFaltante =
                    dinheiroNecessario -
                            dinheiroAtual;

            double horasFaltantes =
                    horasNecessarias -
                            horasAtuais;

            return plugin.getMensagemManager()
                    .obter(
                            "configuracao.mensagens.rankup.sem-dinheiro-e-tempo",
                            "%dinheiro%",
                            formatarDinheiro(
                                    dinheiroFaltante
                            ),
                            "%horas%",
                            formatarHoras(
                                    horasFaltantes
                            )
                    );
        }

        if (!possuiDinheiro) {

            double dinheiroFaltante =
                    dinheiroNecessario -
                            dinheiroAtual;

            return plugin.getMensagemManager()
                    .obter(
                            "configuracao.mensagens.rankup.sem-dinheiro",
                            "%dinheiro%",
                            formatarDinheiro(
                                    dinheiroFaltante
                            )
                    );
        }

        if (!possuiHoras) {

            double horasFaltantes =
                    horasNecessarias -
                            horasAtuais;

            return plugin.getMensagemManager()
                    .obter(
                            "configuracao.mensagens.rankup.sem-tempo",
                            "%horas%",
                            formatarHoras(
                                    horasFaltantes
                            )
                    );
        }

        return null;
    }

    public boolean promover(
            Player jogador
    ) {

        int nivelAtual =
                plugin.getJogadorManager()
                        .getNivelRank(
                                jogador.getUniqueId()
                        );

        Rank proximoRank =
                plugin.getRankManager()
                        .getProximoRank(
                                nivelAtual
                        );

        if (proximoRank == null) {
            return false;
        }

        double dinheiroAtual =
                economia.getBalance(
                        jogador
                );

        double dinheiroNecessario =
                proximoRank.getDinheiro();

        double horasAtuais =
                plugin.getHorasManager()
                        .getHorasTotaisPrecisao(
                                jogador
                        );

        double horasNecessarias =
                proximoRank.getHoras();

        if (dinheiroAtual < dinheiroNecessario) {
            return false;
        }

        if (horasAtuais < horasNecessarias) {
            return false;
        }

        if (dinheiroNecessario > 0) {

            var resultado =
                    economia.withdrawPlayer(
                            jogador,
                            dinheiroNecessario
                    );

            if (!resultado.transactionSuccess()) {
                return false;
            }
        }

        plugin.getJogadorManager()
                .definirRank(
                        jogador.getUniqueId(),
                        proximoRank.getNivel()
                );

        plugin.getPermissaoManager()
                .aplicarPermissoesDoRank(
                        jogador.getUniqueId(),
                        proximoRank.getNivel()
                );

        plugin.getRecompensaManager()
                .executarRecompensas(
                        jogador,
                        proximoRank
                );

        plugin.getMensagemManager()
                .anunciar(
                        "configuracao.mensagens.rankup.anuncio-global",
                        "%jogador%",
                        jogador.getName(),
                        "%rank%",
                        proximoRank.getNome()
                );

        return true;
    }

    private String formatarDinheiro(
            double valor
    ) {

        return String.format(
                java.util.Locale.US,
                "$%.2f",
                valor
        );
    }

    private String formatarHoras(
            double valor
    ) {

        if (valor == (long) valor) {

            return (long) valor + "h";
        }

        return String.format(
                java.util.Locale.US,
                "%.2fh",
                valor
        );
    }
}