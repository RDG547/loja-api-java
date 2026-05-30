# Relatorio breve

## Decisoes de design

A aplicacao foi implementada como uma API HTTP em Java puro usando
`com.sun.net.httpserver.HttpServer`, evitando dependencias externas. Essa
decisao facilita a execucao em qualquer ambiente com JDK instalado.

O dominio foi separado em entidades, repositorios, servicos, excecoes e camada
HTTP. A regra de negocio fica nos servicos, enquanto o servidor apenas recebe
requisicoes, chama o servico adequado e transforma os resultados em JSON.

Para demonstrar heranca e polimorfismo, `Usuario` herda de `Pessoa`, enquanto
`ProdutoFisico` e `ProdutoDigital` herdam de `Produto`. Cada tipo de produto
implementa seu proprio calculo de valor final e seus detalhes especificos.

Os dados sao armazenados em memoria por um repositorio generico baseado em
`HashMap<Long, T>`. As consultas retornam `ArrayList`, atendendo ao requisito
de manipulacao de colecoes.

## Tratamento de erros

Foram criadas excecoes especificas:

- `ValidationException` para entradas invalidas.
- `NotFoundException` para registros inexistentes.
- `ConflictException` para conflitos, como e-mail repetido.
- `MethodNotAllowedException` para metodos HTTP nao aceitos.

Todas sao convertidas em respostas JSON com codigo HTTP apropriado.

## Dificuldades encontradas

Como o projeto nao usa bibliotecas externas, foi necessario implementar um
parser e serializador JSON simples para receber e responder dados da API. A
solucao suporta os tipos necessarios para a atividade: objetos, listas,
strings, numeros, booleanos e nulo.

Outra dificuldade foi manter a API simples para fins academicos sem perder a
separacao de responsabilidades. Por isso, a persistencia foi mantida em
memoria, mas isolada em um repositorio generico para permitir evolucao futura
para arquivo ou banco de dados.
