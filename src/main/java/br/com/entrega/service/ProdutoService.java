package br.com.entrega.service;

import br.com.entrega.domain.Produto;
import br.com.entrega.domain.ProdutoDigital;
import br.com.entrega.domain.ProdutoFisico;
import br.com.entrega.exception.NotFoundException;
import br.com.entrega.exception.ValidationException;
import br.com.entrega.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProdutoService {
    private final CrudRepository<Produto> repository;

    public ProdutoService(CrudRepository<Produto> repository) {
        this.repository = repository;
    }

    public Produto cadastrar(Map<String, Object> data) {
        String tipo = Input.requiredString(data, "tipo").toUpperCase(Locale.ROOT);
        Produto produto = switch (tipo) {
            case "FISICO" -> criarFisico(data);
            case "DIGITAL" -> criarDigital(data);
            default -> throw new ValidationException("Tipo de produto invalido. Use FISICO ou DIGITAL.");
        };
        validarProduto(produto);
        return repository.save(produto);
    }

    public Produto buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto nao encontrado."));
    }

    public List<Map<String, Object>> listar(Map<String, String> filtros) {
        String nome = filtros.getOrDefault("nome", "").trim().toLowerCase(Locale.ROOT);
        String tipo = filtros.getOrDefault("tipo", "").trim().toUpperCase(Locale.ROOT);

        ArrayList<Map<String, Object>> result = new ArrayList<>();
        for (Produto produto : repository.findAll()) {
            boolean matchesNome = nome.isBlank() || produto.getNome().toLowerCase(Locale.ROOT).contains(nome);
            boolean matchesTipo = tipo.isBlank() || produto.getTipo().equals(tipo);
            if (matchesNome && matchesTipo) {
                result.add(produto.toMap());
            }
        }
        return result;
    }

    public Produto atualizar(long id, Map<String, Object> data) {
        Produto produto = buscarPorId(id);

        if (data.containsKey("tipo") && !produto.getTipo().equals(Input.requiredString(data, "tipo").toUpperCase(Locale.ROOT))) {
            throw new ValidationException("O tipo do produto nao pode ser alterado. Remova e cadastre novamente.");
        }

        String nome = Input.optionalString(data, "nome", produto.getNome());
        String descricao = Input.optionalString(data, "descricao", produto.getDescricao());
        double preco = Input.optionalDouble(data, "preco", produto.getPreco());

        if (produto instanceof ProdutoFisico fisico) {
            double pesoKg = Input.optionalDouble(data, "pesoKg", fisico.getPesoKg());
            int estoque = Input.optionalInt(data, "estoque", fisico.getEstoque());
            validarProduto(new ProdutoFisico(nome, descricao, preco, pesoKg, estoque));

            fisico.setNome(nome);
            fisico.setDescricao(descricao);
            fisico.setPreco(preco);
            fisico.setPesoKg(pesoKg);
            fisico.setEstoque(estoque);
            return repository.save(fisico);
        }

        if (produto instanceof ProdutoDigital digital) {
            double tamanhoMb = Input.optionalDouble(data, "tamanhoMb", digital.getTamanhoMb());
            int licencasDisponiveis = Input.optionalInt(data, "licencasDisponiveis", digital.getLicencasDisponiveis());
            validarProduto(new ProdutoDigital(nome, descricao, preco, tamanhoMb, licencasDisponiveis));

            digital.setNome(nome);
            digital.setDescricao(descricao);
            digital.setPreco(preco);
            digital.setTamanhoMb(tamanhoMb);
            digital.setLicencasDisponiveis(licencasDisponiveis);
            return repository.save(digital);
        }

        throw new ValidationException("Tipo de produto desconhecido.");
    }

    public void remover(long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Produto nao encontrado.");
        }
        repository.deleteById(id);
    }

    private Produto criarFisico(Map<String, Object> data) {
        return new ProdutoFisico(
                Input.requiredString(data, "nome"),
                Input.requiredString(data, "descricao"),
                Input.requiredDouble(data, "preco"),
                Input.requiredDouble(data, "pesoKg"),
                Input.requiredInt(data, "estoque")
        );
    }

    private Produto criarDigital(Map<String, Object> data) {
        return new ProdutoDigital(
                Input.requiredString(data, "nome"),
                Input.requiredString(data, "descricao"),
                Input.requiredDouble(data, "preco"),
                Input.requiredDouble(data, "tamanhoMb"),
                Input.requiredInt(data, "licencasDisponiveis")
        );
    }

    private void validarProduto(Produto produto) {
        if (produto.getNome().length() < 2) {
            throw new ValidationException("Nome do produto deve possuir pelo menos 2 caracteres.");
        }

        if (produto.getDescricao().length() < 3) {
            throw new ValidationException("Descricao do produto deve possuir pelo menos 3 caracteres.");
        }

        if (produto.getPreco() <= 0) {
            throw new ValidationException("Preco deve ser maior que zero.");
        }

        if (produto instanceof ProdutoFisico fisico) {
            if (fisico.getPesoKg() <= 0) {
                throw new ValidationException("Peso deve ser maior que zero.");
            }
            if (fisico.getEstoque() < 0) {
                throw new ValidationException("Estoque nao pode ser negativo.");
            }
        }

        if (produto instanceof ProdutoDigital digital) {
            if (digital.getTamanhoMb() <= 0) {
                throw new ValidationException("Tamanho do arquivo deve ser maior que zero.");
            }
            if (digital.getLicencasDisponiveis() < 0) {
                throw new ValidationException("Licencas disponiveis nao podem ser negativas.");
            }
        }
    }
}
