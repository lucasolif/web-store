# WebStore — E-commerce com Orquestração de Serviços

Projeto acadêmico de e-commerce desenvolvido com uma arquitetura distribuída baseada em APIs HTTP. A `api-webStore` atua como orquestradora do fluxo e integra os serviços de produtos, CEP, pagamento, e-mail, nota fiscal e entrega.

Os front-ends não devem consumir diretamente as APIs especializadas. Todas as operações da aplicação devem passar pela `api-webStore`, disponível por padrão em `http://localhost:8080`.

## Serviços e portas

| Projeto | Porta padrão | Responsabilidade |
|---|---:|---|
| `api-webStore` | `8080` | API principal e orquestradora dos serviços. É o ponto de entrada utilizado pelos front-ends. |
| `api-produto` | `8081` | Cadastro, consulta, alteração e exclusão de produtos, imagens e movimentações de estoque. |
| `api-cep` | `8082` | Consulta de endereço por CEP e integração com o ViaCEP. |
| `api-pagamento` | `8083` | Processamento, confirmação e cancelamento de pagamentos por PIX, boleto e cartão. |
| `api-email` | `8084` | Envio dos e-mails de compra, pagamento, nota fiscal e entrega. |
| `api-notaFiscal` | `8085` | Cadastro e consulta das notas fiscais e de seus produtos. |
| `api-delivery` | `8086` | Cadastro dos dados de entrega e geração das informações de rastreamento. |

As portas podem ser sobrescritas individualmente pela variável de ambiente `PORTA_SERVIDOR`.

## Front-ends

| Projeto | Porta sugerida | Finalidade |
|---|---:|---|
| `front-webStore` | `4200` | Catálogo, carrinho, checkout, pagamento e acompanhamento do fluxo da compra. |
| `front-cadastro-produto` | `4201` | Administração de produtos e movimentação de entrada e saída do estoque. |

Os dois front-ends consomem a `api-webStore` pelo endereço:

```text
http://localhost:8080/api
```

Como o Angular utiliza a porta `4200` por padrão, utilize portas diferentes quando os dois front-ends forem executados simultaneamente.

## Banco de dados

O banco utilizado pelas APIs que possuem persistência é o MySQL.

- Banco/schema: `ecommerce`
- Host padrão: `localhost`
- Porta padrão do MySQL: `3306`
- Estratégia Hibernate: `ddl-auto: update`

As APIs `api-webStore`, `api-produto`, `api-pagamento`, `api-email`, `api-notaFiscal` e `api-delivery` utilizam o mesmo schema `ecommerce`.

A `api-cep` não utiliza banco de dados, pois funciona como integração com o serviço externo ViaCEP.

As configurações do banco podem ser substituídas pelas variáveis:

```text
BANCO_URL
BANCO_USUARIO
BANCO_SENHA
```

Exemplo de URL local:

```text
jdbc:mysql://localhost:3306/ecommerce?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
```

O usuário do MySQL precisa ter permissão para criar e alterar as tabelas do schema. Não devem ser versionadas senhas reais do banco ou credenciais de serviços externos.

## Pré-requisitos

- Java 21;
- Maven 3.9 ou superior;
- MySQL 8 ou superior;
- Node.js compatível com Angular 21;
- npm;
- acesso à Internet para a consulta ao ViaCEP e para o envio SMTP de e-mails.

Todas as APIs utilizam Spring Boot `3.5.16`.

## Ordem recomendada de inicialização

1. Iniciar o MySQL na porta `3306`.
2. Iniciar `api-produto` na porta `8081`.
3. Iniciar `api-cep` na porta `8082`.
4. Iniciar `api-pagamento` na porta `8083`.
5. Iniciar `api-email` na porta `8084`.
6. Iniciar `api-notaFiscal` na porta `8085`.
7. Iniciar `api-delivery` na porta `8086`.
8. Iniciar `api-webStore` na porta `8080`.
9. Iniciar os front-ends.

A `api-webStore` deve ser iniciada depois das APIs especializadas, pois é responsável por consumir e orquestrar esses serviços.

## Executando as APIs

Dentro da pasta de cada API, execute:

```bash
mvn spring-boot:run
```

Também é possível executar a classe principal de cada projeto diretamente pela IDE, utilizando o JDK 21.

Cada API deve permanecer em um processo separado e não pode compartilhar a mesma porta com outro serviço.

## Executando os front-ends

### Loja e fluxo de compra

```bash
cd front-webStore
npm install
npm start -- --port 4200
```

### Cadastro e estoque de produtos

```bash
cd front-cadastro-produto
npm install
npm start -- --port 4201
```

Quando o arquivo `package-lock.json` estiver atualizado, pode-se utilizar `npm ci` no lugar de `npm install` para uma instalação reproduzível das dependências.

## Configuração das integrações

A `api-webStore` possui, por padrão, as seguintes integrações locais:

```text
Produtos:    http://localhost:8081
CEP:         http://localhost:8082
Pagamento:  http://localhost:8083
E-mail:      http://localhost:8084
Nota fiscal: http://localhost:8085
Entrega:     http://localhost:8086
```

Caso algum serviço seja executado em outro endereço ou porta, configure a variável de ambiente correspondente antes de iniciar a `api-webStore`:

```text
PRODUCT_API_BASE_URL
CEP_API_BASE_URL
PAYMENT_API_BASE_URL
EMAIL_API_BASE_URL
INVOICE_API_BASE_URL
DELIVERY_API_BASE_URL
```

## Configuração de e-mail

A `api-email` utiliza SMTP. Antes da execução, é necessário configurar:

- endereço do servidor SMTP;
- porta SMTP;
- usuário remetente;
- senha de aplicativo ou credencial SMTP;
- autenticação e TLS.

Credenciais de e-mail não devem ser publicadas no repositório. Em ambientes compartilhados, devem ser fornecidas por variáveis de ambiente, perfil local do Spring ou mecanismo seguro de secrets.

## Regras básicas do ambiente

- Todos os serviços devem utilizar Java 21.
- O MySQL deve estar ativo antes das APIs que utilizam persistência.
- Cada API deve executar em sua porta correspondente.
- Os front-ends devem acessar somente a `api-webStore`.
- A `api-webStore` deve orquestrar as integrações e não deve acessar diretamente os bancos das outras APIs.
- O fluxo só deve avançar quando a operação anterior for concluída com sucesso.
- O estoque é atualizado após a confirmação da compra e a emissão da nota fiscal.
- Pagamentos PIX e boleto precisam ser confirmados antes da continuação do fluxo.
- O cancelamento de PIX ou boleto também deve cancelar a compra associada.
- As credenciais do banco, SMTP e demais integrações não devem ser versionadas.
- Para evitar conflitos de CORS, execute os front-ends em `localhost` ou `127.0.0.1` nas portas configuradas.

## Fluxo resumido da compra

1. O catálogo consulta os produtos pela `api-webStore`.
2. O cliente seleciona os produtos e informa seus dados e endereço.
3. A compra e seus itens são cadastrados.
4. O sistema envia o e-mail de confirmação da compra.
5. O pagamento é criado e confirmado conforme o método escolhido.
6. A compra é confirmada e o e-mail de pagamento é enviado.
7. A nota fiscal é cadastrada e o estoque é atualizado.
8. Os dados da nota fiscal são enviados no corpo do e-mail.
9. Os produtos são disponibilizados para entrega.
10. Os dados da entrega são enviados por e-mail.

Em caso de falha, o fluxo deve ser interrompido na etapa atual e retornar uma mensagem de erro sem executar automaticamente as etapas seguintes.
