# Customer API

Microsserviço de cadastro e gestão de clientes com Clean Architecture, DDD tático e hexagonal.

## Stack
- Java 21
- Spring Boot 3.3.4
- PostgreSQL 16 + Flyway
- Docker / Testcontainers
- Cucumber + JUnit 5

## Executar local
```bash
# Subir banco (caso não use Testcontainers)
docker-compose up -d
# Rodar aplicação
./mvnw spring-boot:run
```

## Endpoints principais
### Criar cliente
POST /api/v1/customers
Body:
```json
{
  "name": "Maria Silva",
  "cpf": "52998224725",
  "email": "maria@test.com",
  "phone": "11987654321"
}
```
Responses: 201 Created (Location header), 400 erro de validação/regra.

### Listar clientes (paginações + filtro)
GET /api/v1/customers?search=joao&page=0&size=20
Response (DTO estável PageResponse):
```json
{
  "content": [
    {
      "id": "6f0e...",
      "name": "Joao Santos",
      "cpf": "52998224725",
      "email": "joao@test.com",
      "phone": "11999999999",
      "createdAt": "2025-11-21T12:34:56",
      "updatedAt": "2025-11-21T12:34:56"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Buscar por ID
GET /api/v1/customers/{id} -> 200 ou 404 (soft delete respeitado).

### Atualizar
PUT /api/v1/customers/{id} -> 204

### Excluir (soft delete)
DELETE /api/v1/customers/{id} -> 204; subsequente GET retorna 404.

## Regras de negócio
- CPF e Email únicos (create e update validam mudanças).
- Soft delete: registros inativos retornam 404 em findById e não aparecem nas listas.
- Filtro de busca (search) faz LIKE case-insensitive em name ou email.

## Estrutura de código (hexagonal/clean)
- domain: entidades e objetos de valor (Customer, Cpf).
- application: casos de uso e DTOs.
- adapter inbound: REST controllers.
- adapter outbound: persistência (JPA + EntityManager para paginação).

## Testes
- Unitários (services, valor Cpf).
- Integração (MockMvc + Testcontainers).
- E2E (Cucumber + RestAssured).

Para rodar testes:
```bash
./mvnw test
```
Relatório Cucumber em `target/cucumber-reports/cucumber.html`.

## DTO de Paginação
`PageResponse<T>` substitui `Page<T>` na serialização para evitar warning e garantir estabilidade:
Campos: content, page, size, totalElements, totalPages, first, last.

## Mensageria (Eventos de Domínio)

Eventos publicados quando `messaging.enabled=true`:

`CustomerEvent` (versão 1)
```
{
  "eventId": "<uuid>",
  "correlationId": "<uuid>",
  "id": "<customerId>",
  "type": "CUSTOMER_CREATED | CUSTOMER_UPDATED | CUSTOMER_DELETED",
  "occurredAt": "2025-11-24T14:41:00Z",
  "version": 1,
  "payload": {
    "name": "Maria Silva",
    "cpf": "52998224725",
    "email": "maria@test.com",
    "phone": "11987654321",
    "deleted": false
  }
}
```

### Habilitar
Por padrão `messaging.enabled=false` em `application.yml`. Para ativar:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--messaging.enabled=true"
```
Ou via variável de ambiente:
```bash
MESSAGING_ENABLED=true mvn spring-boot:run
```

### Subir Kafka local (KRaft)
```bash
docker compose up -d kafka
```
Ver tópicos:
```bash
docker exec -it customer-api-kafka-1 bash -c "kafka-topics --bootstrap-server localhost:9092 --list"
```

### Teste de publicação
Executar caso de uso de criação e observar logs `Published event type=CUSTOMER_CREATED ...`.

### Design
- Publisher condicional (`KafkaDomainEventPublisher` vs `NoOpDomainEventPublisher`).
- Futuros campos: trace/correlation externo (usar header X-Correlation-ID). 
- Evolução de schema: incrementar `version` e manter compatibilidade.

### Próximos passos mensageria
- Outbox pattern (garantia de entrega) usando tabela outbox + scheduler.
- Dead-letter topic para falhas.
- Consumer de projeção (cache / busca). 
- Metrics (Micrometer) por tipo de evento.

## Próximos passos sugeridos
- Internacionalizar mensagens (i18n).
- Index em colunas name/email para busca.
- Cobertura de testes para cenários de update com alteração simultânea de CPF e email.
