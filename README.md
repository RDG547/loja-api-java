# Loja API - Aplicação back-end em Java

Aplicação back-end completa em Java puro para demonstrar POO, exceções,
coleções e operações CRUD por meio de uma API HTTP.

## Requisitos

- JDK 17 ou superior
- Terminal com `javac` e `java` disponíveis

Não é necessário Maven, Gradle ou banco de dados externo. Os dados ficam em
memória enquanto a aplicação está rodando.

## Como baixar o código-fonte pelo GitHub

Qualquer pessoa pode baixar o código-fonte clonando o repositório:

```bash
git clone https://github.com/RDG547/loja-api-java.git
cd loja-api-java
```

Se a pessoa não tiver Git instalado, também pode abrir o repositório no
navegador, clicar em **Code**, depois em **Download ZIP**, extrair o arquivo e
abrir a pasta extraída no terminal.

## Como compilar

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```

## Como executar

```bash
java -cp out br.com.entrega.Main 8080
```

Se a porta não for informada, a API usa `8080`.

Depois de iniciar, acesse:

```text
http://localhost:8080/api/status
```

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | `/api/status` | Verifica se a API está ativa |
| GET | `/api/usuarios` | Lista usuários |
| GET | `/api/usuarios/{id}` | Busca usuário por id |
| POST | `/api/usuarios` | Cadastra usuário |
| PUT | `/api/usuarios/{id}` | Atualiza usuário |
| DELETE | `/api/usuarios/{id}` | Remove usuário |
| GET | `/api/produtos` | Lista produtos |
| GET | `/api/produtos/{id}` | Busca produto por id |
| POST | `/api/produtos` | Cadastra produto |
| PUT | `/api/produtos/{id}` | Atualiza produto |
| DELETE | `/api/produtos/{id}` | Remove produto |

## Exemplos de teste com curl

Criar usuário:

```bash
curl -s -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza","email":"ana@email.com","perfil":"CLIENTE"}'
```

Listar usuários:

```bash
curl -s http://localhost:8080/api/usuarios
```

Criar produto físico:

```bash
curl -s -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"tipo":"FISICO","nome":"Teclado","descricao":"Teclado mecânico","preco":250.0,"pesoKg":0.9,"estoque":12}'
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

Remover usuário:

```bash
curl -s -X DELETE http://localhost:8080/api/usuarios/1
```

## Observações

- A base de dados é simulada em memória por meio de `HashMap`.
- As listagens retornam `ArrayList`.
- Os erros são tratados por exceções personalizadas e devolvidos em JSON.
- O projeto usa herança e polimorfismo em `Pessoa -> Usuario` e em
  `Produto -> ProdutoFisico/ProdutoDigital`.
