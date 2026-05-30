package br.com.entrega.domain;

import java.util.Map;

public class ProdutoDigital extends Produto {
    private double tamanhoMb;
    private int licencasDisponiveis;

    public ProdutoDigital(String nome, String descricao, double preco, double tamanhoMb, int licencasDisponiveis) {
        super(nome, descricao, preco);
        this.tamanhoMb = tamanhoMb;
        this.licencasDisponiveis = licencasDisponiveis;
    }

    public double getTamanhoMb() {
        return tamanhoMb;
    }

    public void setTamanhoMb(double tamanhoMb) {
        this.tamanhoMb = tamanhoMb;
    }

    public int getLicencasDisponiveis() {
        return licencasDisponiveis;
    }

    public void setLicencasDisponiveis(int licencasDisponiveis) {
        this.licencasDisponiveis = licencasDisponiveis;
    }

    @Override
    public String getTipo() {
        return "DIGITAL";
    }

    @Override
    public double calcularValorFinal() {
        return getPreco();
    }

    @Override
    protected void preencherDetalhes(Map<String, Object> map) {
        map.put("tamanhoMb", round(tamanhoMb));
        map.put("licencasDisponiveis", licencasDisponiveis);
    }
}
