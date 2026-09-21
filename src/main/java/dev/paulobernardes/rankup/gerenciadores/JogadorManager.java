package dev.paulobernardes.rankup.gerenciadores;

import dev.paulobernardes.rankup.RankUp;
import dev.paulobernardes.rankup.banco.BancoDados;
import dev.paulobernardes.rankup.modelos.JogadorRankUp;

import java.util.UUID;

public class JogadorManager {

    private final RankUp plugin;
    private final BancoDados bancoDados;

    public JogadorManager(RankUp plugin, BancoDados bancoDados) {
        this.plugin = plugin;
        this.bancoDados = bancoDados;
    }

    public void registrarJogador(UUID uuid) {

        if (!bancoDados.jogadorExiste(uuid)) {
            bancoDados.criarJogador(uuid);
        }
    }

    public JogadorRankUp getJogador(UUID uuid) {

        registrarJogador(uuid);

        int nivelRank = bancoDados.getNivelRank(uuid);

        return new JogadorRankUp(
                uuid,
                nivelRank
        );
    }

    public void definirRank(UUID uuid, int nivelRank) {

        registrarJogador(uuid);

        bancoDados.atualizarRank(
                uuid,
                nivelRank
        );
    }

    public int getNivelRank(UUID uuid) {

        registrarJogador(uuid);

        return bancoDados.getNivelRank(uuid);
    }
}