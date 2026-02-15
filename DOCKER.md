# Docker – Commandes utiles

## Build des images

```bash
# Build toutes les images
docker compose build

# Build une image spécifique
docker compose build app
docker compose build liquibase

# Build sans cache (utile après un fix)
docker compose build --no-cache app
docker compose build --no-cache liquibase
```

## Lancer les containers

```bash
# Lancer tous les services (postgres → liquibase → app)
docker compose up

# Build + lancer en une commande
docker compose up --build

# En arrière-plan
docker compose up -d
```

## Arrêter les containers

```bash
# Arrêter
docker compose down

# Arrêter et supprimer les volumes (repart de zéro pour la DB)
docker compose down -v
```

## Logs

```bash
docker compose logs app
docker compose logs liquibase
docker compose logs postgres
```

## Accès à la DB

```bash
docker exec immofds-db psql -U immofds -d immofds
```

## URLs

- API : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui.html
- API Docs : http://localhost:8080/api-docs
