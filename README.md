# Stock Market API

A highly available REST API for managing wallets and stock trading, built with Spring Boot 3, Spring Cloud (Eureka + Gateway), PostgreSQL and Docker.

## Architecture



- **Spring Cloud Gateway** — the single host-facing entry point on the configured port
- **Eureka Server** — service registry; the gateway discovers and routes to healthy app instances
- **Stockmarket App (×2 replicas)** — the business logic; both register with Eureka on startup
- **PostgreSQL** — persistent storage; Flyway handles schema migrations automatically

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) (includes Docker Compose v2)
- No JDK or Maven installation required — everything builds inside Docker

## Starting the Application

### Linux / macOS

```bash
chmod +x start.sh
./start.sh 8080
```

### Windows

```bat
.\start.cmd 8080
```

Replace `8080` with any available port on your machine.

The first run will take a few minutes as Maven downloads dependencies and builds the images. Subsequent runs use the Docker layer cache and start much faster.

Once all services are healthy, the API is available at:

```
http://localhost:<PORT>
```

## Stopping the Application

```bash
docker compose down
```

To also delete all stored data (database volume):

```bash
docker compose down -v
```

## Scaling (optional)

To run more than 2 application replicas:

```bash
APP_PORT=8080 docker compose up --scale stockmarket=4
```

## Verifying High Availability

1. Start the application
2. Find one of the stockmarket container IDs: `docker ps`
3. Kill it: `docker stop <container-id>`
4. The gateway automatically re-routes all traffic to the surviving instance via Eureka
5. No requests are dropped after the brief de-registration period

## Eureka Dashboard

While the stack is running, the Eureka service registry UI is available at:

```
http://localhost:8761
```

You can see all registered instances and their health status.

## API Endpoints

> All requests go through the gateway on `localhost:PORT`.

### Stocks

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/stocks` | List all available stocks |
| `POST` | `/stocks` | Set the list of available stocks |

### Wallets

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/wallets/{walletId}` | Get wallet details |
| `POST` | `/wallets/{walletId}/stocks/{stockName}` | Buy a stock |
| `DELETE` | `/wallets/{walletId}/stocks/{stockName}` | Sell a stock |