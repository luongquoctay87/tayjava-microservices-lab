# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-26-gateway-rate-limit` |
| Lesson | 26 — Rate limiting and API protection at the Gateway |
| Parent | `lesson-25-gateway-routing` |
| What changed | Gateway **RequestRateLimiter** (Redis token bucket) on catalogue and `POST /api/v1/orders/*/pay`. `RateLimitKeyConfig` keys by IP (ignores client `X-Forwarded-For` when `trusted-proxy-count=0`) or authenticated principal. Compose **redis** + short Redis timeouts (fail-open, no long hang). Order service adds pay stub → `FULFILLING`. |

## Ports

| Service | Port |
|---|---|
| Gateway | 8080 |
| Course | 8081 |
| Enrollment | 8082 |
| BFF | 8083 |
| Order | 8084 |
| Redis | internal only |

## Run

```bash
docker compose up --build
# burstCapacity=3 on pay → 4th call returns 429
for i in 1 2 3 4; do curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/v1/orders/1/pay; done
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
