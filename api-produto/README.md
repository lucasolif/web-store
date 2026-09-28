# API Produto — API de produtos e estoque

API REST acadêmica desenvolvida com Java, Spring Boot, Spring Data JPA e MySQL. O projeto permite manter produtos com imagens e registrar entradas e saídas de estoque com histórico, validação de saldo e controle transacional.

## Tecnologias

- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA / Hibernate
- Jakarta Bean Validation
- MySQL 8.4
- Maven

## Organização do código

```text
br.edu.utfpr.apiproduto
├── produto
│   ├── api
│   │   ├── request
│   │   └── response
│   ├── application
│   ├── domain
│   └── infrastructure
│       ├── mapper
│       ├── persistence
│       └── storage
├── estoque
│   ├── api
│   │   ├── request
│   │   └── response
│   ├── application
│   ├── domain
│   └── infrastructure
│       ├── mapper
│       └── persistence
├── infrastructure
│   ├── exception
│   └── web
└── shared
    ├── exception
    └── response
```

A divisão segue a referência fornecida, com nomes em português:

- `produto` e `estoque`: contextos funcionais com API, aplicação, domínio e infraestrutura próprios;
- `shared`: contratos HTTP e exceções reutilizados pelos contextos;
- `infrastructure`: tratamento global de erros e rastreabilidade HTTP por correlation ID.

Os nomes de classes, records, enums e interfaces estão em inglês, por exemplo: `Product`, `ProductRepository`, `CreateProductService`, `ProductMapper`, `ProductResponse` e `ProductController`. Métodos, atributos, parâmetros, variáveis e constantes próprias da aplicação permanecem em português.

A camada `application` possui um Service por caso de uso. Produto é dividido em `CreateProductService`, `FindProductService`, `ListProductsService`, `UpdateProductService` e `DeleteProductService`; movimentação de estoque é dividida em `CreateStockMovementService`, `FindStockMovementService` e `ListStockMovementsService`. Com isso, cada classe possui uma responsabilidade transacional específica e depende apenas dos componentes necessários para sua operação.

Os repositórios são somente interfaces que estendem `JpaRepository`. Não há implementações manuais, pois o Spring Data JPA as gera em tempo de execução.

## Regras implementadas

- Todo produto começa com estoque zero. O saldo só pode mudar por uma movimentação.
- `INBOUND` soma a quantidade ao estoque.
- `OUTBOUND` subtrai a quantidade e é rejeitada quando o saldo é insuficiente.
- Cada movimentação registra saldo anterior, saldo posterior, tipo, quantidade, data e observação.
- O identificador numérico (`id`) é também o código público do produto.
- A imagem do produto é opcional no cadastro e na alteração. Quando enviada, recebe um nome UUID e o banco armazena somente o caminho público.
- Atualizar sem enviar a parte `image` preserva a imagem atual; enviar uma nova imagem substitui a anterior após a confirmação da transação.
- São aceitas imagens JPEG, PNG e WebP de até 5 MiB. O tipo declarado e a assinatura binária do arquivo são validados.
- Imagens substituídas ou pertencentes a produtos excluídos são removidas somente após o commit no banco; em caso de rollback, a nova imagem é descartada.
- O produto é bloqueado para escrita durante a movimentação (`PESSIMISTIC_WRITE`), evitando perda de atualização em operações concorrentes.
- O saldo também possui versionamento otimista com `@Version` como proteção adicional.
- Um produto com histórico não pode ser excluído fisicamente, para preservar a rastreabilidade.
- As movimentações são imutáveis pela API: não existem endpoints de alteração ou exclusão do histórico.

## Execução

### Pré-requisitos

- JDK 21 ou superior
- Maven 3.6.3 ou superior
- Docker com Docker Compose, ou uma instalação local do MySQL

### 1. Iniciar o MySQL com Docker

```bash
docker compose up -d
```

O arquivo `compose.yaml` cria:

- banco: `api_produto`
- usuário: `apiproduto`
- senha: `apiproduto`
- porta: `3306`

### 2. Iniciar a API

```bash
mvn spring-boot:run
```

A aplicação será disponibilizada em `http://localhost:8080`.

Caso o MySQL já esteja instalado, as configurações podem ser sobrescritas por variáveis de ambiente:

| Variável | Valor padrão |
|---|---|
| `BANCO_URL` | `jdbc:mysql://localhost:3306/api_produto?...` |
| `BANCO_USUARIO` | `apiproduto` |
| `BANCO_SENHA` | `apiproduto` |
| `PORTA_SERVIDOR` | `8080` |
| `PRODUCT_IMAGE_DIRECTORY` | `uploads/products` |
| `PRODUCT_IMAGE_MAX_SIZE` | `5MB` |
| `PRODUCT_IMAGE_MAX_REQUEST_SIZE` | `6MB` |
| `CORS_ALLOWED_ORIGINS` | `null` (abertura local por `file://`) |
| `CORS_ALLOWED_ORIGIN_PATTERNS` | `http://localhost:*,http://127.0.0.1:*` |

O diretório de imagens deve ser persistente e gravável pela aplicação. Em contêineres, monte `PRODUCT_IMAGE_DIRECTORY` em um volume. `PRODUCT_IMAGE_MAX_SIZE` é aplicado tanto pelo parser multipart quanto pela validação da aplicação.

Por padrão, o CORS permite apenas páginas locais, adequadas ao front-end acadêmico executado em outra porta. Em uma implantação real, configure `CORS_ALLOWED_ORIGINS` ou `CORS_ALLOWED_ORIGIN_PATTERNS` com os domínios específicos do ambiente.

O Hibernate está configurado com `ddl-auto: update`, adequado ao escopo acadêmico, portanto cria e atualiza as tabelas automaticamente. Em produção, recomenda-se substituir isso por migrações versionadas com Flyway ou Liquibase.

## Endpoints

| Método | Rota | Finalidade | Resposta esperada |
|---|---|---|---|
| `POST` | `/api/products` | Criar produto | `201 Created` |
| `GET` | `/api/products` | Listar produtos paginados | `200 OK` |
| `GET` | `/api/products/{id}` | Buscar produto | `200 OK` |
| `PUT` | `/api/products/{id}` | Atualizar produto | `200 OK` |
| `DELETE` | `/api/products/{id}` | Excluir produto sem histórico | `204 No Content` |
| `GET` | `/api/product-images/{fileName}` | Obter o conteúdo de uma imagem | `200 OK` |
| `POST` | `/api/products/{id}/stock-movements` | Registrar entrada ou saída | `201 Created` |
| `GET` | `/api/products/{id}/stock-movements` | Listar histórico do produto | `200 OK` |
| `GET` | `/api/stock-movements/{id}` | Buscar movimentação | `200 OK` |

Nas listagens, use `pagina` e `tamanho`. A numeração pública começa em `1`, e o tamanho é limitado a 100 itens por página.

Exemplo:

```http
GET /api/products?page=1&size=20
```

## Exemplos JSON

### Criar produto sem imagem

```json
{
  "name": "Teclado mecânico",
  "description": "Teclado ABNT2 com switches azuis",
  "price": 249.90
}
```

O código do produto não é recebido no cadastro: o identificador numérico `id`, gerado automaticamente pelo banco, é utilizado como código público. O estoque inicial também não integra o cadastro; para estabelecer saldo, deve-se registrar uma entrada.

### Criar produto com imagem

Cadastro e alteração com imagem usam `multipart/form-data`. A parte `product` deve ser um JSON com `Content-Type: application/json`, e a parte `image` deve conter um arquivo JPEG, PNG ou WebP.

```bash
curl --request POST http://localhost:8080/api/products \
  --form 'product={"name":"Teclado mecânico","description":"ABNT2 com RGB","price":249.90};type=application/json' \
  --form 'image=@teclado.png;type=image/png'
```

Para substituir a imagem, envie o mesmo formato em `PUT /api/products/{id}`. Uma alteração enviada como JSON, ou como multipart sem a parte `image`, mantém a imagem atual.

As respostas de criação, alteração, consulta individual e listagem incluem `imagePath` quando o produto possui imagem:

```json
{
  "data": {
    "id": 1,
    "name": "Teclado mecânico",
    "description": "ABNT2 com RGB",
    "price": 249.90,
    "stockQuantity": 0,
    "imagePath": "/api/product-images/123e4567-e89b-12d3-a456-426614174000.png",
    "createdAt": "2026-09-24T20:30:00",
    "updatedAt": "2026-09-24T20:30:00"
  }
}
```

`imagePath` é uma URL relativa à própria API. Por exemplo, para uma API em `http://localhost:8080`, a imagem acima estará disponível em `http://localhost:8080/api/product-images/123e4567-e89b-12d3-a456-426614174000.png`.

### Registrar entrada de estoque

```json
{
  "type": "INBOUND",
  "quantity": 25,
  "notes": "Compra do fornecedor"
}
```

### Registrar saída de estoque

```json
{
  "type": "OUTBOUND",
  "quantity": 3,
  "notes": "Venda"
}
```

Os únicos valores aceitos em `type` são `INBOUND` e `OUTBOUND`.

### Resposta de uma movimentação

```json
{
  "data": {
    "id": 1,
    "productId": 1,
    "type": "INBOUND",
    "quantity": 25,
    "previousBalance": 0,
    "resultingBalance": 25,
    "notes": "Compra do fornecedor",
    "occurredAt": "2026-09-20T20:30:00"
  }
}
```

Respostas individuais de sucesso usam sempre o envelope `data`. Listagens retornam `data` como lista e os metadados no objeto `pagination`. Operações concluídas sem conteúdo retornam `204 No Content`, sem corpo. Todas as respostas incluem o cabeçalho `X-Correlation-Id`; caso o cliente envie um identificador válido nesse cabeçalho, ele será preservado.

Exemplo de listagem:

```json
{
  "data": [],
  "pagination": {
    "currentPage": 1,
    "totalPages": 0,
    "pageSize": 20,
    "totalRecords": 0,
    "recordsOnPage": 0,
    "hasNextPage": false,
    "hasPreviousPage": false
  }
}
```

## Tratamento de erros

Os erros têm formato JSON consistente. Validações incluem também a lista de campos inválidos:

```json
{
  "timestamp": "2026-09-21T15:30:00Z",
  "code": "VALIDATION_ERROR",
  "message": "Existem campos inválidos na requisição.",
  "path": "/api/products",
  "correlationId": "b15b238f-b6f5-4ec0-89aa-35fbf9a1e677",
  "violations": [
    {
      "field": "name",
      "message": "O nome é obrigatório"
    }
  ]
}
```

Status relevantes:

- `400 Bad Request`: JSON, campos, parâmetros ou imagem inválidos (`VALIDATION_ERROR`, `MALFORMED_REQUEST_BODY`, `INVALID_PARAMETER`, `INVALID_PRODUCT_IMAGE_CONTENT`, `UNSUPPORTED_PRODUCT_IMAGE_TYPE`);
- `413 Payload Too Large`: imagem maior que o limite multipart (`PRODUCT_IMAGE_TOO_LARGE`);
- `404 Not Found`: produto, movimentação ou rota inexistente (`PRODUCT_NOT_FOUND`, `STOCK_MOVEMENT_NOT_FOUND`, `RESOURCE_NOT_FOUND`);
- `409 Conflict`: produto com histórico, integridade ou concorrência (`PRODUCT_HAS_STOCK_MOVEMENTS`, `DATA_INTEGRITY_VIOLATION`, `CONCURRENT_MODIFICATION`);
- `405 Method Not Allowed`: método HTTP incompatível com a rota (`METHOD_NOT_ALLOWED`);
- `415 Unsupported Media Type`: tipo de conteúdo não suportado (`UNSUPPORTED_MEDIA_TYPE`);
- `422 Unprocessable Entity`: regra de negócio, como saldo insuficiente (`BUSINESS_RULE_VIOLATION`);
- `500 Internal Server Error`: falha não tratada, sem exposição de detalhes técnicos (`INTERNAL_SERVER_ERROR`).
