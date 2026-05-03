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
/bin/bash start.sh 8080
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
| `GET` | `/wallets/{walletId}/stocks/{stockName}` | Get quantity of a stock in a wallet |
| `POST` | `/wallets/{walletId}/stocks/{stockName}` | Buy or sell a stock |

### Logs

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/log` | List buy/sell operations |

## Example Payloads and Responses

The examples below assume the API is running on `http://localhost:8080`.

### Set Available Stocks

Request:

```bash
curl -X POST http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "stocks": [
      { "name": "AAPL", "quantity": 100 },
      { "name": "GOOG", "quantity": 50 }
    ]
  }'
```

Payload:

```json
{
  "stocks": [
    {
      "name": "AAPL",
      "quantity": 100
    },
    {
      "name": "GOOG",
      "quantity": 50
    }
  ]
}
```

Successful response:

```http
HTTP/1.1 200 OK
```

The response body is empty.

### List Available Stocks

Request:

```bash
curl http://localhost:8080/stocks
```

Successful response:

```json
{
  "stocks": [
    {
      "name": "AAPL",
      "quantity": 100
    },
    {
      "name": "GOOG",
      "quantity": 50
    }
  ]
}
```

When no stocks are available:

```json
{
  "stocks": []
}
```

### Buy a Stock

Request:

```bash
curl -X POST http://localhost:8080/wallets/wallet1/stocks/AAPL \
  -H "Content-Type: application/json" \
  -d '{ "type": "buy" }'
```

Payload:

```json
{
  "type": "buy"
}
```

Successful response:

```http
HTTP/1.1 200 OK
```

The response body is empty.

### Sell a Stock

Request:

```bash
curl -X POST http://localhost:8080/wallets/wallet1/stocks/AAPL \
  -H "Content-Type: application/json" \
  -d '{ "type": "sell" }'
```

Payload:

```json
{
  "type": "sell"
}
```

Successful response:

```http
HTTP/1.1 200 OK
```

The response body is empty.

### Get Wallet Details

Request:

```bash
curl http://localhost:8080/wallets/wallet1
```

Successful response:

```json
{
  "id": "wallet1",
  "stocks": [
    {
      "name": "AAPL",
      "quantity": 10
    },
    {
      "name": "GOOG",
      "quantity": 5
    }
  ]
}
```

When the wallet has no stocks:

```json
{
  "id": "wallet1",
  "stocks": []
}
```

### Get Stock Quantity in Wallet

Request:

```bash
curl http://localhost:8080/wallets/wallet1/stocks/AAPL
```

Successful response:

```text
7
```

### Get Operation Log

Request:

```bash
curl http://localhost:8080/log
```

Successful response:

```json
{
  "log": [
    {
      "type": "buy",
      "wallet_id": "wallet1",
      "stock_name": "AAPL"
    },
    {
      "type": "sell",
      "wallet_id": "wallet2",
      "stock_name": "GOOG"
    }
  ]
}
```

When there are no log entries:

```json
{
  "log": []
}
```

### Error Responses

Unknown stock:

```http
HTTP/1.1 404 Not Found
Content-Type: text/plain

Stock not found: AAPL
```

Insufficient stock:

```http
HTTP/1.1 400 Bad Request
Content-Type: text/plain

No stock
```
