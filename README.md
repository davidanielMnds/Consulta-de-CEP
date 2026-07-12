# Consulta de CEP

Projeto simples em Spring Boot que consome a API pública do [ViaCEP](https://viacep.com.br) para consultar endereços a partir de um CEP, com histórico de buscas.

## Tecnologias

- Java 21
- Spring Boot (Spring Web)
- Lombok
- HTML, CSS e JavaScript puro no front-end

## Funcionalidades

- Consulta de endereço por CEP via API ViaCEP
- Histórico de CEPs já buscados
- Filtro de campos exibidos no histórico (CEP, logradouro, bairro, localidade, UF)

## Como rodar

1. Clone o repositório
2. Abra o projeto no IntelliJ (ou outra IDE com suporte a Maven)
3. Rode a classe `VisualizarCepApplication`
4. Acesse `http://localhost:8080/index.html`

## Estrutura

```
src/main/java/com/d4igen/visualizar_cep/
├── controller/   → endpoints da API
├── service/      → lógica de consumo da ViaCEP
└── dto/          → objeto de transferência de dados (EnderecoDTO)

src/main/resources/static/
├── index.html       → página de busca
├── historico.html    → página de histórico
├── style.css
└── historico.css
```

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/cep/{cep}` | Busca um endereço pelo CEP |
| GET | `/cep/historico` | Retorna o histórico completo de buscas |
| GET | `/cep/historico/cep` | Retorna apenas os CEPs já buscados |

## Status

Projeto de estudo, focado em aprender Spring Boot (Controller, Service, DTO) e JavaScript (fetch, manipulação do DOM).
