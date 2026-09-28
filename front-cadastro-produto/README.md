# Front-end — Gestão de Produtos WebStore

Aplicação Angular para cadastro, manutenção de produtos e movimentação de estoque por meio da `api-webStore`.

## Tecnologias

- Angular 21 com componentes standalone;
- TypeScript com verificação estrita;
- Angular Router;
- formulários reativos;
- Bootstrap 5 e Bootstrap Icons para a identidade visual existente.

## Funcionalidades

### Produtos

- listagem paginada;
- busca por ID, nome ou descrição;
- cadastro com imagem opcional;
- alteração com preservação ou substituição da imagem;
- exclusão com confirmação;
- estados de carregamento, vazio e erro;
- integração centralizada com a `api-webStore` em `http://localhost:8080/api`.

### Estoque

- carregamento dos produtos cadastrados;
- seleção de entrada (`INBOUND`) ou saída (`OUTBOUND`);
- quantidade decimal compatível com `BigDecimal` do backend;
- exibição do saldo atual e projeção do novo saldo;
- bloqueio preventivo de saída acima do estoque disponível;
- atualização do saldo após a confirmação.

## Executar em desenvolvimento

Com a `api-webStore` disponível em `http://localhost:8080`, execute:

```bash
npm install
npm start
```

Acesse `http://localhost:4200`.

## Build de produção

```bash
npm run build
npm run serve:dist
```

O servidor de distribuição utiliza `http://127.0.0.1:5500` e possui fallback para as rotas da SPA.

## Rotas

| Página | Rota |
|---|---|
| Gestão de produtos | `/produtos` |
| Movimentação de estoque | `/estoque` |

## Integração de estoque

O front-end envia a movimentação para:

```http
POST /api/products/{id}/stock-movements
Content-Type: application/json
```

Exemplo:

```json
{
  "quantity": 2.50,
  "operation": "OUTBOUND"
}
```

Operações suportadas:

- `INBOUND`: adiciona a quantidade ao estoque;
- `OUTBOUND`: retira a quantidade do estoque.
