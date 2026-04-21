#!/bin/bash
# Usage: ./start.sh [PORT]
# Example: ./start.sh 9000
# If PORT is omitted, defaults to 8080.

PORT=${1:-8080}

echo "Starting Stock Market application on port $PORT..."
APP_PORT=$PORT docker compose up --build
