package br.com.entrega.api;

import br.com.entrega.domain.Produto;
import br.com.entrega.domain.Usuario;
import br.com.entrega.exception.ApiException;
import br.com.entrega.exception.MethodNotAllowedException;
import br.com.entrega.exception.NotFoundException;
import br.com.entrega.repository.InMemoryRepository;
import br.com.entrega.service.ProdutoService;
import br.com.entrega.service.UsuarioService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ApiServer {
    private final int port;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;

    public ApiServer(int port) {
        this.port = port;
        this.usuarioService = new UsuarioService(new InMemoryRepository<>());
        this.produtoService = new ProdutoService(new InMemoryRepository<>());
        seedData();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleSafely);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Loja API rodando em http://localhost:" + port);
    }

    private void handleSafely(HttpExchange exchange) throws IOException {
        try {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                Response.empty(exchange, 204);
                return;
            }

            route(exchange);
        } catch (ApiException exception) {
            Response.json(exchange, exception.getStatusCode(), errorBody(exception.getStatusCode(), exception.getMessage()));
        } catch (Exception exception) {
            exception.printStackTrace();
            Response.json(exchange, 500, errorBody(500, "Erro interno no servidor."));
        }
    }

    private void route(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if ("/".equals(path)) {
            Response.json(exchange, 200, apiIndex());
            return;
        }

        if ("/api/status".equals(path)) {
            Response.json(exchange, 200, success("API ativa.", Map.of("status", "ok")));
            return;
        }

        if ("/api/usuarios".equals(path) || path.startsWith("/api/usuarios/")) {
            handleUsuarios(exchange);
            return;
        }

        if ("/api/produtos".equals(path) || path.startsWith("/api/produtos/")) {
            handleProdutos(exchange);
            return;
        }

        throw new NotFoundException("Rota nao encontrada.");
    }

    private void handleUsuarios(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Long id = RequestUtils.extractId(exchange.getRequestURI().getPath(), "/api/usuarios");

        if (id == null) {
            if ("GET".equals(method)) {
                List<Map<String, Object>> usuarios = usuarioService.listar(RequestUtils.queryParams(exchange));
                Response.json(exchange, 200, listBody("Usuarios encontrados.", usuarios));
                return;
            }

            if ("POST".equals(method)) {
                Usuario usuario = usuarioService.cadastrar(RequestUtils.bodyAsObject(exchange));
                Response.json(exchange, 201, success("Usuario cadastrado com sucesso.", usuario.toMap()));
                return;
            }
        } else {
            if ("GET".equals(method)) {
                Usuario usuario = usuarioService.buscarPorId(id);
                Response.json(exchange, 200, success("Usuario encontrado.", usuario.toMap()));
                return;
            }

            if ("PUT".equals(method)) {
                Usuario usuario = usuarioService.atualizar(id, RequestUtils.bodyAsObject(exchange));
                Response.json(exchange, 200, success("Usuario atualizado com sucesso.", usuario.toMap()));
                return;
            }

            if ("DELETE".equals(method)) {
                usuarioService.remover(id);
                Response.json(exchange, 200, success("Usuario removido com sucesso.", Map.of("id", id)));
                return;
            }
        }

        throw new MethodNotAllowedException("Metodo HTTP nao permitido para usuarios.");
    }

    private void handleProdutos(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Long id = RequestUtils.extractId(exchange.getRequestURI().getPath(), "/api/produtos");

        if (id == null) {
            if ("GET".equals(method)) {
                List<Map<String, Object>> produtos = produtoService.listar(RequestUtils.queryParams(exchange));
                Response.json(exchange, 200, listBody("Produtos encontrados.", produtos));
                return;
            }

            if ("POST".equals(method)) {
                Produto produto = produtoService.cadastrar(RequestUtils.bodyAsObject(exchange));
                Response.json(exchange, 201, success("Produto cadastrado com sucesso.", produto.toMap()));
                return;
            }
        } else {
            if ("GET".equals(method)) {
                Produto produto = produtoService.buscarPorId(id);
                Response.json(exchange, 200, success("Produto encontrado.", produto.toMap()));
                return;
            }

            if ("PUT".equals(method)) {
                Produto produto = produtoService.atualizar(id, RequestUtils.bodyAsObject(exchange));
                Response.json(exchange, 200, success("Produto atualizado com sucesso.", produto.toMap()));
                return;
            }

            if ("DELETE".equals(method)) {
                produtoService.remover(id);
                Response.json(exchange, 200, success("Produto removido com sucesso.", Map.of("id", id)));
                return;
            }
        }

        throw new MethodNotAllowedException("Metodo HTTP nao permitido para produtos.");
    }

    private Map<String, Object> apiIndex() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("nome", "Loja API");
        body.put("rotas", List.of(
                "/api/status",
                "/api/usuarios",
                "/api/produtos"
        ));
        return body;
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mensagem", message);
        body.put("dados", data);
        return body;
    }

    private Map<String, Object> listBody(String message, List<Map<String, Object>> items) {
        Map<String, Object> body = success(message, items);
        body.put("total", items.size());
        return body;
    }

    private Map<String, Object> errorBody(int statusCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("erro", message);
        body.put("status", statusCode);
        return body;
    }

    private void seedData() {
        usuarioService.cadastrar(Map.of(
                "nome", "Maria Oliveira",
                "email", "maria@email.com",
                "perfil", "ADMIN"
        ));
        usuarioService.cadastrar(Map.of(
                "nome", "Joao Silva",
                "email", "joao@email.com",
                "perfil", "CLIENTE"
        ));

        produtoService.cadastrar(Map.of(
                "tipo", "FISICO",
                "nome", "Notebook Pro",
                "descricao", "Notebook para desenvolvimento",
                "preco", 4200.0,
                "pesoKg", 1.7,
                "estoque", 5
        ));
        produtoService.cadastrar(Map.of(
                "tipo", "DIGITAL",
                "nome", "E-book Java",
                "descricao", "Material introdutorio de Java",
                "preco", 49.9,
                "tamanhoMb", 18,
                "licencasDisponiveis", 100
        ));
    }
}
