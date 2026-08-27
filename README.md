# mini-mall-gateway

Standalone API Gateway for the mini-mall platform.

## Role

Northbound entry for all clients (Web / H5 / App / Merchant / Open API):

```text
Clients → CDN/WAF/LB → Gateway → BFF → Domain Services
```

Gateway responsibilities:

- Route `/api/consumer/**` → Consumer BFF
- Route `/api/workbench/**` → Merchant BFF
- Route `/open/v1/**` → Open API BFF
- Route `/api/auth/**`, `/api/products/**`, … → domain services (BFF southbound)
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

## BFF southbound

Each BFF calls domain services through this gateway (`MINIMALL_GATEWAY_BASE_URL=http://127.0.0.1:8080`).

Merchant domain APIs from the browser use:

```text
GET /api/workbench/domain/api/products/1001
  → Gateway → Merchant BFF → Gateway → product-service
```
