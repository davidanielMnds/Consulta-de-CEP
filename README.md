# Consulta de CEP

API REST em Java com Spring Boot que consulta endereços a partir de um CEP usando a API pública [ViaCEP](https://viacep.com.br). O projeto conta com histórico de buscas, validação de entrada, tratamento padronizado de erros, limite de requisições por IP e uma interface web em HTML, CSS e JavaScript puro.

Começou como um projeto de estudo de Controller, Service e DTO e evoluiu para uma aplicação com camadas bem separadas, resiliência na chamada a um serviço externo e testes automatizados.

## Funcionalidades

- Consulta de endereço por CEP, aceitando o valor com ou sem hífen
- Validação do formato (8 dígitos) antes de chamar a API externa
- Histórico das consultas bem-sucedidas, completo ou apenas com os CEPs
- Limite de requisições por IP (rate limiting) com resposta `429` e cabeçalho `Retry-After`
- Tratamento centralizado de erros com respostas JSON padronizadas e status HTTP adequados
- Timeouts de conexão e leitura na chamada ao ViaCEP
- Interface web com página de busca e página de histórico com filtro de campos exibidos
- Testes unitários da camada de serviço com Mockito

## Tecnologias

| Área | Tecnologia |
|------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 (Spring MVC) |
| Cliente HTTP | `RestTemplate` com timeouts configurados |
| Rate limiting | Bucket4j 8.20.0 (algoritmo token bucket) |
| Redução de boilerplate | Lombok |
| Testes | JUnit 5, Mockito, Spring Boot Test |
| Build | Maven (Maven Wrapper incluído) |
| Front-end | HTML, CSS e JavaScript puro (`fetch` e manipulação do DOM) |

## Arquitetura

```
src/main/java/com/d4igen/visualizar_cep/
├── VisualizarCepApplication.java   → ponto de entrada da aplicação
├── AppConfig.java                  → bean do RestTemplate (timeouts de 3s e 5s)
├── config/
│   ├── RateLimitInterceptor.java   → intercepta /cep/** e aplica o limite por IP
│   └── WebConfig.java              → registra o interceptor no Spring MVC
├── controller/
│   └── CepController.java          → endpoints REST
├── service/
│   ├── CepService.java             → validação, consulta ao ViaCEP e histórico
│   └── RateLimitService.java       → mantém um balde de tokens por IP
├── dto/
│   ├── EnderecoDTO.java            → endereço retornado pela API
│   └── ErroDTO.java                → corpo padrão das respostas de erro
└── exception/
    ├── CepException.java                    → exceção base, carrega o HttpStatus
    ├── CepComFormatoInvalidoException.java  → 400
    ├── CepNaoEncontradoException.java       → 404
    └── GlobalExceptionHandler.java          → converte exceções em respostas JSON

src/main/resources/static/
├── index.html       → página de busca
├── historico.html   → página de histórico com filtros
├── style.css
└── historico.css

src/test/java/com/d4igen/visualizar_cep/
├── VisualizarCepApplicationTests.java   → carregamento do contexto
└── service/CepServiceTest.java          → testes unitários do CepService
```

### Fluxo de uma requisição

1. A requisição para `/cep/**` passa primeiro pelo `RateLimitInterceptor`, que consome um token do balde associado ao IP de origem. Sem tokens disponíveis, a resposta é `429`.
2. O `CepController` recebe a requisição e delega ao `CepService`.
3. O `CepService` remove o hífen, valida que o CEP tem 8 dígitos, consulta o ViaCEP via `RestTemplate` e registra o resultado no histórico.
4. Qualquer falha é traduzida pelo `GlobalExceptionHandler` em uma resposta JSON com o status HTTP correspondente.

## Decisões técnicas

- **Hierarquia de exceções de domínio.** `CepException` carrega o `HttpStatus` da resposta. Cada erro de negócio (`CepComFormatoInvalidoException`, `CepNaoEncontradoException`) só precisa informar a mensagem e o status, e um único handler trata todas elas.
- **Validação antes da chamada externa.** CEPs com formato inválido são rejeitados sem acionar o ViaCEP, o que evita tráfego desnecessário. Os testes verificam que o cliente HTTP nunca é chamado nesses casos.
- **Rate limiting com Bucket4j.** Cada IP recebe um balde com capacidade de 10 tokens e reposição de 10 tokens por minuto, armazenado em um `ConcurrentHashMap`. Ao estourar o limite, a API devolve `429` com o tempo de espera no cabeçalho `Retry-After`.
- **Resiliência na integração.** O `RestTemplate` usa timeout de 3 segundos para conexão e 5 segundos para leitura. Falhas de comunicação viram `502`, e um `429` vindo do próprio ViaCEP é tratado com mensagem específica.
- **Tolerância a mudanças na API externa.** O `EnderecoDTO` usa `@JsonIgnoreProperties(ignoreUnknown = true)`, então campos adicionais do ViaCEP (como `ibge`, `gia`, `ddd` e `siafi`) não quebram a desserialização.
- **CEP inexistente.** O ViaCEP responde com sucesso mesmo quando o CEP não existe, então o serviço identifica a ausência de localidade na resposta e retorna `404`.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/cep/{cep}` | Busca um endereço pelo CEP (com ou sem hífen) |
| GET | `/cep/historico` | Retorna o histórico completo de buscas |
| GET | `/cep/historico/cep` | Retorna apenas os CEPs já buscados |

### Exemplo de sucesso

```
GET /cep/01001000
```

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "localidade": "São Paulo",
  "uf": "SP"
}
```

### Respostas de erro

Todos os erros seguem o formato `{ "mensagem": "..." }`.

| Status | Situação | Mensagem |
|--------|----------|----------|
| 400 | CEP com formato inválido | `cep com formato inválido: <cep>` |
| 404 | CEP não encontrado no ViaCEP | `CEP não encontrado: <cep>` |
| 429 | Limite de requisições por IP excedido | `Muitas requisições, tente novamente em N segundos` (com cabeçalho `Retry-After`) |
| 429 | Limite atingido no ViaCEP | `Limite de pesquisas alcançado, espere antes de continuar.` |
| 502 | ViaCEP indisponível ou com timeout | `Serviço de consulta de CEP indisponível` |
| 500 | Erro inesperado | `Erro interno` |

## Interface web

O front-end é servido pelo próprio Spring Boot a partir de `src/main/resources/static`.

- **Busca** (`/index.html`): formulário que consome `/cep/{cep}` e exibe CEP, logradouro, bairro, localidade e UF.
- **Histórico** (`/historico.html`): lista as consultas realizadas e permite escolher quais campos exibir por meio de filtros. Pelo menos um campo permanece sempre selecionado.

## Testes

Os testes unitários de `CepService` usam Mockito para isolar o `RestTemplate`, sem depender da rede. Cenários cobertos:

- CEP existente é retornado e adicionado ao histórico
- CEP inexistente lança `CepNaoEncontradoException` e não entra no histórico
- Formatos inválidos (`123`, `abc`, `123456789`, `1234-678`, `asdfghjk` e string vazia) lançam `CepComFormatoInvalidoException` sem chamar o ViaCEP, em um teste parametrizado
- Indisponibilidade do ViaCEP (`ResourceAccessException`) é propagada e não altera o histórico
- Erro `5xx` do ViaCEP é propagado e não altera o histórico
- O contexto da aplicação sobe corretamente (`@SpringBootTest`)

Para executar:

```
./mvnw test
```

## Como executar

Pré-requisitos: Java 21. O Maven Wrapper já está no repositório, então não é preciso instalar o Maven.

```
git clone <url-do-repositorio>
cd Consulta-de-CEP
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`. Também é possível abrir o projeto em uma IDE com suporte a Maven (como o IntelliJ) e executar a classe `VisualizarCepApplication`.

Com a aplicação no ar, acesse `http://localhost:8080/index.html`. Para testar a API diretamente:

```
curl http://localhost:8080/cep/01001000
```

## Limitações conhecidas e próximos passos

- O histórico fica em memória e é compartilhado entre todos os usuários, portanto é perdido ao reiniciar a aplicação.
- Testes de camada web com `@WebMvcTest`, cobrindo o controller, o tratamento de erros e o rate limiting.