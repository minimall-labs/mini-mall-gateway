# mini-mall-gateway

MiniMall 平台 **北向 API 网关**（Spring Cloud Gateway）。

**平台架构总览**：[mini-mall-services/docs/architecture.md](https://github.com/minimall-labs/mini-mall-services/blob/main/docs/architecture.md)

## Role

Northbound entry for all clients (Web / H5 / App / Merchant / Open API):

```text
Clients → CDN/WAF/LB → Gateway (:8080) → BFF → Domain Services
```

Gateway responsibilities:

- Route `/api/consumer/**` → Consumer BFF (`:8090`)
- Route `/api/workbench/**` → Merchant BFF (`:8091`)
- Route `/open/v1/**` → Open API BFF (`:8092`)
- Route `/api/auth/**`, `/api/products/**`, … → domain services (Merchant BFF southbound + Swagger)
- JWT validation, trace propagation, Swagger aggregation (local dev)

## Structure

```text
mini-mall-gateway/
├── common/              # shared trace / jwt / response helpers
├── gateway/             # Spring Cloud Gateway app
├── scripts/start.sh     # local start helper
├── Dockerfile           # container image
└── docker-compose.yml   # local container entry
```

## Run

Prerequisites: [mini-mall-services](https://github.com/minimall-labs/mini-mall-services) (Nacos + domain services).

```bash
mvn -q -DskipTests package
mvn -pl gateway spring-boot:run
```

Start BFFs before exercising northbound routes:

| BFF | Port | Repo |
|-----|------|------|
| Consumer | 8090 | mini-mall-consumer/server |
| Merchant | 8091 | mini-mall-workbench/server |
| Open | 8092 | mini-mall-open/server |

Default address: `http://127.0.0.1:8080`

## Environment

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8080` | Gateway listen port |
| `NACOS_SERVER_ADDR` | `127.0.0.1:8848` | Service discovery for domain services |
| `CONSUMER_BFF_URI` | `http://127.0.0.1:8090` | Consumer BFF upstream |
| `MERCHANT_BFF_URI` | `http://127.0.0.1:8091` | Merchant BFF upstream |
| `OPEN_BFF_URI` | `http://127.0.0.1:8092` | Open API BFF upstream |
| `MINIMALL_JWT_SECRET` | (see `.env.example`) | JWT signing key |
| `MINIMALL_JWT_EXPIRE_SECONDS` | `86400` | Token TTL |

See [.env.example](.env.example).

## Health

- `GET /actuator/health`
- `GET /swagger-ui.html`

## Container

```bash
mvn -q -DskipTests package
docker compose up --build
```

## BFF southbound (by BFF)

| BFF | Southbound | Notes |
|-----|------------|-------|
| **Consumer** | Nacos → direct HTTP | Does **not** call Gateway for aggregation |
| **Merchant** | Gateway → `lb://*-service` | Browser uses domain proxy below |
| **Open** | Gateway (planned) | Domain read APIs TBD |

Merchant domain APIs from the browser:

```text
GET /api/workbench/domain/api/products/100012043901
  → Gateway → Merchant BFF → Gateway → product-service
```

## Related repos

- Domain services: [mini-mall-services](https://github.com/minimall-labs/mini-mall-services)
- Consumer BFF: [mini-mall-consumer](https://github.com/minimall-labs/mini-mall-consumer)
- Merchant BFF: [mini-mall-workbench](https://github.com/minimall-labs/mini-mall-workbench)
- Open BFF: [mini-mall-open](https://github.com/minimall-labs/mini-mall-open)
