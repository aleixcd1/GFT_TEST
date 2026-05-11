# Prices API

REST API para consultar el precio aplicable de un producto para una cadena y una fecha concreta.

El proyecto usa arquitectura hexagonal, contrato OpenAPI y generacion automatica de interfaces y DTOs durante la compilacion.

## Funcionamiento

La API recibe `productId`, `brandId` y `applicationDate`. Busca los precios cuyo rango de fechas contiene la fecha solicitada y, si hay mas de uno, devuelve el de mayor prioridad.

Los datos de ejemplo estan cargados desde `src/main/resources/data.sql` y cubren el producto `35455`, cadena `1`, durante junio de 2020. Una consulta para otra fecha, por ejemplo `2026-05-09T10:36:30.040`, devuelve `404 Not Found`.

## Tecnologias

- Java 23
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 en memoria para el perfil local
- Lombok 1.18.46
- OpenAPI Generator 7.3.0
- SpringDoc OpenAPI 2.6.0
- JUnit 5, Mockito y MockMvc
- Maven Wrapper
- Docker

## Estructura

```text
src/main/java/com/example/prices
|-- domain
|   |-- model
|   |-- port
|   |   |-- in
|   |   `-- out
|   |-- service
|   `-- exception
|-- application
`-- infrastructure
    |-- config
    |-- controller
    |-- exception
    `-- persistence
```

## Requisitos

- JDK 23 o superior para ejecutar comandos Maven locales
- Maven Wrapper incluido en el repositorio
- Docker opcional

En Windows se recomienda usar `mvnw.cmd`. En Linux/macOS, `./mvnw`.

## Compilar y Probar

```powershell
.\mvnw.cmd clean test
```

Compilar sin tests:

```powershell
.\mvnw.cmd clean package -DskipTests
```

La compilacion genera codigo desde `src/main/resources/api-spec.yaml` en:

```text
target/generated-sources/openapi
```

## Ejecutar en Local

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicacion queda disponible en:

```text
http://localhost:8080
```

El perfil activo por defecto es `local`, configurado con H2 en memoria.

## Swagger

Con la aplicacion levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console: `http://localhost:8080/h2-console` solo con el perfil `local`

## Endpoint

### POST `/prices`

Consulta el precio aplicable.

Request:

```json
{
  "productId": 35455,
  "brandId": 1,
  "applicationDate": "2020-06-14T10:00:00"
}
```

Las fechas se manejan como `LocalDateTime`, sin zona horaria.

Response `200 OK`:

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.5,
  "currency": "EUR"
}
```

Response `404 Not Found`:

```json
{
  "message": "No price found for product 35455, brand 1, date 2026-05-09T10:36:30.040",
  "timestamp": "2026-05-09T12:44:38.123"
}
```

Response `400 Bad Request`:

```json
{
  "message": "productId: must not be null",
  "timestamp": "2026-05-09T12:44:38.123"
}
```

Todos los errores documentados por OpenAPI devuelven JSON con esta estructura:

```json
{
  "message": "Error description",
  "timestamp": "2026-05-09T12:44:38.123"
}
```

## Ejemplos cURL

Precio encontrado:

```bash
curl -X POST http://localhost:8080/prices \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 35455,
    "brandId": 1,
    "applicationDate": "2020-06-14T16:00:00"
  }'
```

Precio no encontrado:

```bash
curl -X POST http://localhost:8080/prices \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 35455,
    "brandId": 1,
    "applicationDate": "2026-05-09T10:36:30.040"
  }'
```

## Datos de Prueba

| brandId | startDate           | endDate             | priceList | productId | priority | price | currency |
|--------:|---------------------|---------------------|----------:|----------:|---------:|------:|----------|
| 1       | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 | 1         | 35455     | 0        | 35.50 | EUR      |
| 1       | 2020-06-14T15:00:00 | 2020-06-14T18:30:00 | 2         | 35455     | 1        | 25.45 | EUR      |
| 1       | 2020-06-15T00:00:00 | 2020-06-15T11:00:00 | 3         | 35455     | 1        | 30.50 | EUR      |
| 1       | 2020-06-15T16:00:00 | 2020-12-31T23:59:59 | 4         | 35455     | 1        | 38.95 | EUR      |

## Casos Cubiertos por Tests

`PriceControllerE2ETest` valida los cinco casos principales:

| Caso | Fecha de consulta      | Tarifa esperada | Precio |
|-----:|------------------------|----------------:|-------:|
| 1    | 2020-06-14T10:00:00    | 1               | 35.50  |
| 2    | 2020-06-14T16:00:00    | 2               | 25.45  |
| 3    | 2020-06-14T21:00:00    | 1               | 35.50  |
| 4    | 2020-06-15T10:00:00    | 3               | 30.50  |
| 5    | 2020-06-16T21:00:00    | 4               | 38.95  |

Tambien hay tests unitarios para el servicio de dominio, el caso de uso y respuestas de error.

## Docker

Hay dos formas de ejecutar la aplicacion con Docker.

### Imagen standalone

Construye la imagen y ejecuta la API con el perfil por defecto `local`. En este modo usa H2 en memoria dentro del contenedor.

```powershell
docker build -t prices-api .
docker run --rm -p 8080:8080 prices-api
```

No hace falta ejecutar `mvnw package` antes: el `Dockerfile` compila el proyecto dentro de una etapa de build y copia el jar a una imagen runtime.
El primer build puede tardar porque descarga Maven, dependencias y la imagen base.

### Docker Compose con PostgreSQL

Levanta la API y PostgreSQL juntos:

```powershell
docker compose up --build
```

`docker-compose.yml` levanta dos servicios:

- `app`: API en `http://localhost:8080`, usando el perfil `dev`.
- `postgres`: base de datos PostgreSQL en `localhost:5432`.

El perfil `dev` toma la conexion de estas variables, con valores por defecto para uso local:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=prices_dev
DB_USER=postgres
DB_PASSWORD=postgres
```

Dentro de Docker Compose, `DB_HOST` se configura como `postgres`, que es el nombre del servicio de base de datos.

Para parar y eliminar los contenedores:

```powershell
docker compose down
```

## Perfiles

- `local`: perfil por defecto, H2 en memoria.
- `dev`: PostgreSQL. Usa variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD`; si no se informan, apunta a `localhost:5432/prices_dev`.
- `prod`: PostgreSQL por variables `DB_URL`, `DB_USER` y `DB_PASSWORD`; no inicializa `data.sql`.

Ejecutar con otro perfil:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Para ejecutar `dev` fuera de Docker necesitas tener PostgreSQL disponible. Puedes levantar solo la base de datos con:

```powershell
docker compose up postgres
```

## Decisiones de Diseno

- API-first: `api-spec.yaml` es el contrato de entrada.
- DTOs e interfaz REST generados por OpenAPI Generator.
- Dominio aislado de Spring y de persistencia.
- Seleccion de precio por prioridad en `PriceService`.
- Manejo global de errores con `@RestControllerAdvice`.
- Errores HTTP en JSON para respetar el contrato OpenAPI.
