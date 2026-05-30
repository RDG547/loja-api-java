package br.com.entrega.domain;

import java.util.Map;

public class ProdutoFisico extends Produto {
    private double pesoKg;
    private int estoque;

    public ProdutoFisico(String nome, String descricao, double preco, double pesoKg, int estoque) {
        super(nome, descricao, preco);
        this.pesoKg = pesoKg;
        this.estoque = estoque;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    @Override
    public String getTipo() {
        return "FISICO";
    }

    @Override
    public double calcularValorFinal() {
        return getPreco() + (pesoKg * 8.50);
    }

    @Override
    protected void preencherDetalhes(Map<String, Object> map) {
        map.put("pesoKg", round(pesoKg));
        map.put("estoque", estoque);
    }
}
