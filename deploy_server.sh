#!/bin/bash
set -e

echo "Iniciando despliegue del Servicio"

cd Carrito-CheckoutService/ || { echo "No se encontró el directorio"; exit 1; }

echo "Actualizando código..."
git pull origin master

echo "Construyendo imagen Docker"
docker build -t cart-checkout-service:latest .

echo "Deteniendo contenedor anterior"
if docker ps -a --format '{{.Names}}' | grep -q '^cart-checkout-service$'; then
  docker stop cart-checkout-service || true
  docker rm cart-checkout-service || true
fi

echo "Ejecutando nuevo contenedor"
docker run -d --name cart-checkout-service -p 6000:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:postgresql://ep-lingering-leaf-a8art4uv-pooler.eastus2.azure.neon.tech:5432/neondb?sslmode=require" \
  -e DB_USER="neondb_owner" \
  -e DB_PASS="npg_3IrsdSkcPf5l" \
  cart-checkout-service:latest

echo "Despliegue completado exitosamente"