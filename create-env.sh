#!/bin/bash

echo "DB_URL=jdbc:postgresql://localhost:5432/oficina" > .env
echo "DB_USERNAME=user" >> .env
echo "DB_PASSWORD=password" >> .env

echo "AUTH_ISSUER_URI=http://localhost:8081" >> .env
echo "AUTH_AUDIENCE=oficina" >> .env
