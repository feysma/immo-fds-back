# ImmoFDS Backend – Guide agent

## Contexte business

Application de gestion immobilière belge. Le back expose une API REST consommée par un front Angular.

Deux types d'utilisateurs :
- **Public** : visiteurs anonymes (consultation de biens, formulaires de contact)
- **Admin** : agents immobiliers (ADMIN) et super administrateurs (SUPER_ADMIN)

---

## Stack technique

| Élément | Valeur |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.2 |
| Base de données | PostgreSQL 17 |
| Migrations | Liquibase (conteneur dédié) |
| Mapping | MapStruct 1.6.3 |
| Auth | JWT (JJWT 0.12.6) + Refresh Token en DB |
| Doc API | SpringDoc OpenAPI → `/swagger-ui.html` |
| Tests | JUnit 5 + Testcontainers (PostgreSQL) |

---

## Structure des packages

```
be.feysdigitalservices.immofds/
├── config/          # SecurityConfig, WebConfig (CORS), JwtConfig, OpenApiConfig
├── controller/
│   ├── pub/         # Endpoints publics (pas de JWT)
│   └── admin/       # Endpoints protégés (JWT requis)
├── domain/
│   ├── entity/      # Entités JPA
│   └── enums/       # 8 enums métier
├── dto/
│   ├── request/     # DTOs entrants
│   └── response/    # DTOs sortants
├── exception/       # Exceptions métier + GlobalExceptionHandler
├── mapper/          # 3 interfaces MapStruct
├── repository/      # Spring Data JPA
├── security/        # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
├── service/         # Logique métier
├── specification/   # JPA Specifications (filtres dynamiques)
└── validation/      # Validateurs custom (@BelgianPostalCode, @ValidImage)
```

---

## API REST

### Routes publiques (pas de JWT)

| Méthode | Path | Description |
|---|---|---|
| GET | `/api/v1/public/properties` | Recherche de biens avec filtres |
| GET | `/api/v1/public/properties/{reference}` | Détail d'un bien |
| GET | `/api/v1/public/properties/{reference}/images/{imageId}` | Image d'un bien (cache 7j) |
| GET | `/api/v1/public/properties/types` | Liste des types de bien (`EnumValueResponse`) |
| GET | `/api/v1/public/properties/provinces` | Liste des provinces belges (`EnumValueResponse`) |
| POST | `/api/v1/public/contacts/general` | Formulaire contact général |
| POST | `/api/v1/public/contacts/sell-your-home` | Formulaire vente |
| POST | `/api/v1/public/contacts/visit-request` | Demande de visite |
| POST | `/api/v1/auth/login` | Connexion → accessToken + refreshToken |
| POST | `/api/v1/auth/refresh` | Renouvellement du token |
| POST | `/api/v1/auth/logout` | Invalidation du refreshToken |

### Routes admin (JWT requis)

| Méthode | Path | Rôle minimum |
|---|---|---|
| GET/POST | `/api/v1/admin/properties` | ADMIN |
| GET/PUT/PATCH/DELETE | `/api/v1/admin/properties/{reference}` | ADMIN |
| GET/POST/PUT/PATCH/DELETE | `/api/v1/admin/properties/{reference}/images/**` | ADMIN |
| GET/PATCH/DELETE | `/api/v1/admin/contacts/**` | ADMIN |
| GET/POST/PUT/DELETE | `/api/v1/admin/users/**` | **SUPER_ADMIN uniquement** |

---

## Entités principales

### Property
Champs clés : `reference` (unique, 20 chars), `propertyType`, `transactionType`, `status`, `price`, `surface`, `bedrooms`, `province`, coordonnées géo, `images` (OneToMany).

### PropertyImage
Données binaires stockées en DB (`BYTEA`). Champ `data` en lazy loading. Supporte réordonnancement et image principale.

### ContactRequest
Formulaire de contact polymorphe (3 types). Champs spécifiques selon `contactType` (ex : `estimatedPrice` uniquement pour `SELL_YOUR_HOME`).

### User / RefreshToken
Auth JWT stateless. Le `refreshToken` est persisté en DB avec une date d'expiration (7 jours par défaut).

---

## Enums

Tous les enums ont un champ `label` (français) et une méthode `getLabel()` — utilisée uniquement pour les endpoints `/types` et `/provinces` qui retournent `EnumValueResponse { value, label }`.

**Dans les réponses JSON, les enums sont toujours sérialisés par leur `.name()` (clé enum), jamais par leur label.**

| Enum | Valeurs |
|---|---|
| `PropertyStatus` | `DRAFT`, `PUBLISHED`, `SOLD`, `RENTED`, `ARCHIVED` |
| `PropertyType` | `HOUSE`, `APARTMENT`, `STUDIO`, `LOFT`, `OFFICE`, `RETAIL_SPACE`, `WAREHOUSE`, `LAND`, `GARAGE`, `PARKING_SPOT` |
| `TransactionType` | `SALE`, `RENT` |
| `Province` | `BRUXELLES_CAPITALE`, `BRABANT_WALLON`, `BRABANT_FLAMAND`, `ANVERS`, `LIMBOURG`, `LIEGE`, `NAMUR`, `HAINAUT`, `LUXEMBOURG`, `FLANDRE_OCCIDENTALE`, `FLANDRE_ORIENTALE` |
| `EnergyRating` | `A_PLUS_PLUS`, `A_PLUS`, `A`, `B`, `C`, `D`, `E`, `F`, `G` |
| `ContactStatus` | `NEW`, `IN_PROGRESS`, `CLOSED` |
| `ContactType` | `SELL_YOUR_HOME`, `GENERAL_CONTACT`, `VISIT_REQUEST` |
| `UserRole` | `ADMIN`, `SUPER_ADMIN` |

---

## Mappers MapStruct

Les 3 mappers (`PropertyMapper`, `ContactRequestMapper`, `UserMapper`) convertissent les enums via `.name()` dans les DTOs de réponse. Ne jamais les remplacer par `.getLabel()`.

---

## Sécurité

- **SecurityConfig** : deux `SecurityFilterChain` (`@Order(1)` public, `@Order(2)` admin)
- **CORS** : configuré dans `SecurityConfig` via `CorsConfigurationSource` (indispensable pour que les preflight OPTIONS passent avant la security chain)
- **JWT** : HS256, expiration 15 min, refresh 7 jours
- **Rôles Spring Security** : préfixés `ROLE_` → `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`
- **Mot de passe** : BCrypt

### Compte admin par défaut
- Email : `admin@immofds.be`
- Mot de passe : `Admin@2026!`

---

## Docker

Trois conteneurs, démarrent dans cet ordre :

```
postgres (healthy) → liquibase (completed) → app
```

| Conteneur | Image | Rôle |
|---|---|---|
| `immofds-db` | `postgres:17-alpine` | Base de données, port `5433:5432` |
| `immofds-liquibase` | `Dockerfile.liquibase` | Applique les migrations Liquibase, s'arrête ensuite |
| `immofds-app` | `Dockerfile` | API Spring Boot, port `8080:8080` |

**Commandes utiles** → voir `DOCKER.md`

### Variables d'environnement clés

| Variable | Défaut | Utilisation |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/immofds` | Connexion DB |
| `JWT_SECRET` | `default-dev-secret-key-...` | Signature JWT (min 256 bits) |
| `JWT_EXPIRATION_MS` | `900000` | Expiration access token (15 min) |
| `POSTGRES_PORT` | `5433` | Port exposé PostgreSQL |

---

## Liquibase

Les changelogs sont dans `src/main/resources/db/changelog/changelogs/`. Liquibase est **désactivé dans Spring Boot** (`spring.liquibase.enabled: false`) — il tourne uniquement via le conteneur dédié.

Pour les tests, Liquibase est réactivé via `application-test.yml`.

---

## Points d'attention

- `jpa.hibernate.ddl-auto: validate` → Hibernate valide le schéma mais ne le modifie jamais. Toute modification de schéma doit passer par un nouveau changelog Liquibase.
- Images stockées en base (BYTEA), pas sur le filesystem.
- Le port PostgreSQL par défaut est `5433` (pas `5432`) car `5432` et `5434–5533` sont réservés sur la machine de développement Windows.
- Les tests d'intégration utilisent Testcontainers (PostgreSQL éphémère).
