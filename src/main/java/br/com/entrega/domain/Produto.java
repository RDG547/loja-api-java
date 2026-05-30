package br.com.entrega.domain;

import java.util.Map;

public abstract class Produto extends Entidade {
    private String nome;
    private String descricao;
    private double preco;

    protected Produto(String nome, String descricao, double preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public abstract String getTipo();

    public abstract double calcularValorFinal();

    protected abstract void preencherDetalhes(Map<String, Object> map);

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = baseMap();
        map.put("tipo", getTipo());
        map.put("nome", nome);
        map.put("descricao", descricao);
        map.put("preco", round(preco));
        map.put("valorFinal", round(calcularValorFinal()));
        preencherDetalhes(map);
        return map;
    }

    protected double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
