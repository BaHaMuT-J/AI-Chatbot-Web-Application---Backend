#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
  export $(grep -v '^#' .env | sed 's/\r//' | xargs)
fi

docker run --name AI-Chatbot -d \
    -p 5432:5432 \
    --restart=always \
    -v ai-chatbot_postgres_data:/var/lib/postgresql/data \
    -e POSTGRES_USER=$POSTGRES_USER \
    -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
    postgres:17.4