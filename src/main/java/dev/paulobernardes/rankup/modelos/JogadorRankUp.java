package dev.paulobernardes.rankup.modelos;

import java.util.UUID;

public class JogadorRankUp {

    private final UUID uuid;
    private int nivelRank;

    public JogadorRankUp(UUID uuid, int nivelRank) {
        this.uuid = uuid;
        this.nivelRank = nivelRank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getNivelRank() {
        return nivelRank;
    }

    public void setNivelRank(int nivelRank) {
        this.nivelRank = nivelRank;
    }
}