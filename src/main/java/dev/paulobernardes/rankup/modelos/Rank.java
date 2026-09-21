package dev.paulobernardes.rankup.modelos;

import java.util.List;

public class Rank {

    private final String id;
    private final int nivel;
    private final String nome;
    private final double dinheiro;
    private final double horas;
    private final List<String> permissoes;
    private final List<String> comandos;

    public Rank(
            String id,
            int nivel,
            String nome,
            double dinheiro,
            double horas,
            List<String> permissoes,
            List<String> comandos
    ) {
        this.id = id;
        this.nivel = nivel;
        this.nome = nome;
        this.dinheiro = dinheiro;
        this.horas = horas;
        this.permissoes = permissoes;
        this.comandos = comandos;
    }

    public String getId() {
        return id;
    }

    public int getNivel() {
        return nivel;
    }

    public String getNome() {
        return nome;
    }

    public double getDinheiro() {
        return dinheiro;
    }

    public double getHoras() {
        return horas;
    }

    public List<String> getPermissoes() {
        return permissoes;
    }

    public List<String> getComandos() {
        return comandos;
    }
}