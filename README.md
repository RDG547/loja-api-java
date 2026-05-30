# Loja API - Aplicacao back-end em Java

Aplicacao back-end completa em Java puro para demonstrar POO, excecoes,
colecoes e operacoes CRUD por meio de uma API HTTP.

## Requisitos

- JDK 17 ou superior
- Terminal com `javac` e `java` disponiveis

Nao e necessario Maven, Gradle ou banco de dados externo. Os dados ficam em
memoria enquanto a aplicacao esta rodando.

## Como baixar o codigo fonte pelo GitHub

Depois que o projeto estiver no GitHub, qualquer pessoa pode baixar o codigo
fonte clonando o repositorio:

```bash
git clone https://github.com/RDG547/loja-api-java.git
cd loja-api-java
```

Se a pessoa nao tiver Git instalado, tambem pode abrir o repositorio no
navegador, clicar em **Code**, depois em **Download ZIP**, extrair o arquivo e
abrir a pasta extraida no terminal.

## Como compilar

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```

## Como executar

```bash
java -cp out br.com.entrega.Main 8080
```

Se a porta nao for informada, a API usa `8080`.

Depois de iniciar, acesse:

```text
http://localhost:8080/api/status
```

## Endpoints

| Metodo | Rota | Descricao |
| --- | --- | --- |
| GET | `/api/status` | Verifica se a API esta ativa |
| GET | `/api/usuarios` | Lista usuarios |
| GET | `/api/usuarios/{id}` | Busca usuario por id |
| POST | `/api/usuarios` | Cadastra usuario |
| PUT | `/api/usuarios/{id}` | Atualiza usuario |
| DELETE | `/api/usuarios/{id}` | Remove usuario |
| GET | `/api/produtos` | Lista produtos |
| GET | `/api/produtos/{id}` | Busca produto por id |
| POST | `/api/produtos` | Cadastra produto |
| PUT | `/api/produtos/{id}` | Atualiza produto |
| DELETE | `/api/produtos/{id}` | Remove produto |

## Exemplos de teste com curl

Criar usuario:

```bash
curl -s -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza","email":"ana@email.com","perfil":"CLIENTE"}'
```

Listar usuarios:

```bash
curl -s http://localhost:8080/api/usuarios
```

Criar produto fisico:

```bash
curl -s -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"tipo":"FISICO","nome":"Teclado","descricao":"Teclado mecanico","preco":250.0,"pesoKg":0.9,"estoque":12}'
```

Criar produto digital:

```bash
curl -s -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"tipo":"DIGITAL","nome":"Curso Java","descricao":"Aulas gravadas","preco":99.9,"tamanhoMb":850,"licencasDisponiveis":30}'
```

Atualizar produto:

```bash
curl -s -X PUT http://localhost:8080/api/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Notebook Pro","preco":4599.9,"estoque":4}'
```

Remover usuario:

```bash
curl -s -X DELETE http://localhost:8080/api/usuarios/1
```

## Observacoes

- A base de dados e simulada em memoria por meio de `HashMap`.
- As listagens retornam `ArrayList`.
- Os erros sao tratados por excecoes personalizadas e devolvidos em JSON.
- O projeto usa heranca e polimorfismo em `Pessoa -> Usuario` e em
  `Produto -> ProdutoFisico/ProdutoDigital`.
