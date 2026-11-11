# Sistema de Ponto Assíncrono (MVP)

Registro de ponto orientado a eventos (EDA). A API recebe a batida, enfileira e responde na hora; workers persistem e notificam de forma assíncrona via ActiveMQ (AMQP 1.0).

Documentação de arquitetura e requisitos: [docs/MVP_Arquitetura_Sistema_Ponto_V7.pdf](docs/MVP_Arquitetura_Sistema_Ponto_V7.pdf) (fonte editável: [DOCX V7](docs/MVP_Arquitetura_Sistema_Ponto_V7_Completo.docx)).

## Fluxo

```
POST /pontos ─► apiProducer ─► fila.ponto.processamento ─► persistenceConsumer ─► PostgreSQL
                (202 Accepted)                                       │
                                                                     ▼
                                    notificationConsumer ◄─ fila.ponto.notificacao
                                    (log de comprovação)
```

1. `apiProducer` gera `mensagemId` + `timestampRegistro` (UTC, ISO 8601) e publica o `RegistrarPontoCommand` na Fila 1.
2. `persistenceConsumer` valida o funcionário, aplica o debounce de 60s, grava em `registro_ponto` e publica o `PontoRegistradoEvent` na Fila 2.
3. `notificationConsumer` lê a Fila 2 e registra a comprovação (simula o e-mail).

## Stack

| Serviço | Pasta | Stack | Papel |
|---|---|---|---|
| API Producer | [apps/apiProducer](apps/apiProducer) | Python 3.12, FastAPI, Qpid Proton | Recebe a batida e enfileira na Fila 1 |
| Persistence Consumer | [apps/persistenceConsumer](apps/persistenceConsumer) | Java 21, Spring Boot 4, Spring Data JPA, Qpid JMS (AMQP 1.0) | Lê Fila 1, salva no banco, envia Fila 2 |
| Notification Consumer | [apps/notificationConsumer](apps/notificationConsumer) | Python 3.12, Qpid Proton | Lê Fila 2 e notifica |
| Broker | [infra](infra) | ActiveMQ Classic 6.1 (AMQP 1.0) | Filas de mensageria |
| Banco | [infra/db/init](infra/db/init) | PostgreSQL 16 | Schema + seed criados na primeira subida |

## Estrutura

```
sistema-ponto/
├── docs/                        # Documentação (DOCX/PDF de arquitetura)
├── infra/                       # docker-compose.yml (ActiveMQ, PostgreSQL) + db/init (schema/seed)
└── apps/
    ├── apiProducer/             # Python/FastAPI
    ├── persistenceConsumer/     # Java/Spring Boot
    └── notificationConsumer/    # Python
```

## Pré-requisitos

- Docker + Docker Compose
- Para rodar fora do Docker: Java 21 (o `mvnw` já vem no projeto) e Python 3.12

## Ambientes

A configuração vem só de variáveis de ambiente (12-Factor). Cada ambiente tem seu `.env` (não versionado), criado a partir do molde `.env.<ambiente>.example`. DEV e QA usam portas diferentes e podem rodar na mesma máquina.

| | DEV | QA |
|---|---|---|
| AMQP / Console ActiveMQ | 5672 / 8161 | 5673 / 8162 |
| PostgreSQL | 5433 | 5432 |
| persistenceConsumer | 8080 | 8081 |
| apiProducer | 8082 | 8083 |

## Como rodar

### Tudo no Docker

```bash
cd infra
cp .env.dev.example .env.dev        # preencha as senhas
docker compose --env-file .env.dev --profile app up -d --build
```

Sem `--profile app` sobem só ActiveMQ e PostgreSQL (útil para rodar os serviços pela IDE/terminal). Para QA, troque `dev` por `qa`.

### Serviços locais (DEV)

Com a infra no ar (`docker compose --env-file .env.dev up -d`):

```bash
# persistenceConsumer
cd apps/persistenceConsumer
cp .env.dev.example .env.dev        # mesmas credenciais de infra/.env.dev
./mvnw spring-boot:run

# notificationConsumer
cd apps/notificationConsumer
cp .env.dev.example .env.dev
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python notification_handler.py

# apiProducer
cd apps/apiProducer
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
ACTIVEMQ_USER=admin ACTIVEMQ_PASS=admin uvicorn producer:app --port 8082
```

## Testando

```bash
curl -X POST http://localhost:8082/pontos \
  -H "Content-Type: application/json" \
  -d '{"idFuncionario": "98765"}'
```

Resposta `202 Accepted` com a mensagem enfileirada. O funcionário `98765` (João da Silva) vem no seed. Acompanhe:

- logs do `notificationConsumer` com a comprovação do ponto;
- tabela `registro_ponto` no PostgreSQL;
- console do ActiveMQ em http://localhost:8161 (DEV).

Uma segunda batida do mesmo ID em menos de 60s é ignorada (debounce).

## Observações

- Usuário/senha do PostgreSQL só valem na primeira subida (volume vazio). Para trocar, recrie os volumes com `docker compose --env-file .env.dev --profile app down -v` (apaga os dados do ambiente).
- O schema é criado pelo `infra/db/init`; o `persistenceConsumer` só valida as entidades (`ddl-auto: validate`).
