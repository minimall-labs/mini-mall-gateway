# mini-mall-gateway

Standalone gateway repository extracted from `mini-mall`.

## Role

- Unified entry for BFFs and internal clients
- JWT validation and trace propagation
- Route forwarding to domain services
- Swagger aggregation for local development

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

Package once, then run a jar:

```bash
./scripts/start.sh jar
```

Default address:

- `http://127.0.0.1:8080`

## Environment

This repository keeps the same runtime contract as the previous embedded gateway:

- `MINIMALL_JWT_SECRET`
- `MINIMALL_JWT_EXPIRE_SECONDS`
- `NACOS_SERVER_ADDR`
- `SERVER_PORT`

See [.env.example](.env.example) for deployment defaults.

## Health

- `GET /actuator/health`
- `GET /swagger-ui.html`

## Container

Build and run:

```bash
mvn -q -DskipTests package
docker compose up --build
```

The container image expects the jar at `gateway/target/gateway-1.0.0-SNAPSHOT.jar`.

## BFF Integration

The three BFFs continue to point at the gateway through a single base URL:

- `mini-mall-consumer` -> `MINIMALL_GATEWAY_BASE_URL=http://127.0.0.1:8080`
- `mini-mall-workbench` -> `MINIMALL_GATEWAY_BASE_URL=http://127.0.0.1:8080`
- `mini-mall-open` -> `MINIMALL_GATEWAY_BASE_URL=http://127.0.0.1:8080`

That keeps the BFF contract stable even if the gateway later moves behind an edge proxy or cluster service name.
