# CPA Fintech

Plataforma de gestión bancaria personal con backend **Spring Boot**, frontend **React + Vite** y base de datos **PostgreSQL**. Incluye módulos de operación diaria (cuentas, clientes, transacciones), parametrización (sucursales, usuarios, roles, productos) y procesos de **cierre de día (EoD)** con **Spring Batch**.

---

## Stack tecnológico

| Capa | Tecnologías |
|------|-------------|
| Backend | Java 21, Spring Boot 3.2, Spring Security, Spring Data JPA, Spring Batch |
| Seguridad | JWT (Auth0), RBAC por roles y funcionalidades |
| Persistencia | PostgreSQL, Flyway, Hibernate, MapStruct |
| Frontend | React 19, Vite 8, React Router, Axios, Bootstrap, Tailwind CSS |
| Calidad | Validación Jakarta (`@Valid`), auditoría con AOP, manejo centralizado de excepciones |

---

## Estructura del repositorio

```
CPAFintech/
├── backend/                          # API Spring Boot (monolito modular)
│   ├── src/main/java/com/fintech/
│   │   ├── audit/                    # Auditoría operacional (AOP)
│   │   ├── config/                   # Seguridad, CORS, excepciones globales
│   │   ├── core/                     # Dominio operativo
│   │   │   ├── accounts/             # Cuentas, restricciones, holds, reportes
│   │   │   ├── customers/            # Clientes
│   │   │   └── transactions/         # Movimientos, transferencias, estados de cuenta
│   │   ├── eod/                      # End of Day (Spring Batch)
│   │   ├── management/               # Parametrización y administración
│   │   │   ├── branches/             # Sucursales
│   │   │   ├── users/                # Usuarios y login
│   │   │   ├── roles/                # Roles y permisos (treeview)
│   │   │   ├── products/             # Productos
│   │   │   ├── subproducts/          # Subproductos e intereses
│   │   │   ├── control_system/       # Control de sistema y fecha de negocio
│   │   │   ├── catalog/              # Catálogos
│   │   │   └── menu/                 # Menú dinámico por permisos
│   │   └── security/                 # JWT y filtros de autenticación
│   ├── src/main/resources/
│   │   ├── application.yml           # Configuración local
│   │   └── db/migration/             # Scripts Flyway (V1, V2, ...)
│   └── pom.xml
│
└── frontend/
    └── cpa-fintech-web/              # SPA React + Vite
        ├── src/
        │   ├── api/                  # Cliente HTTP (Axios + JWT)
        │   ├── components/           # Componentes reutilizables
        │   └── pages/                # Pantallas (core + management)
        ├── package.json
        └── vite.config.js
```

---

## Módulos principales del API

Todos los endpoints REST usan el prefijo base **`/api/v1`**.

| Módulo | Ruta base | Descripción |
|--------|-----------|-------------|
| Usuarios | `/api/v1/users` | Registro, login, consulta |
| Sucursales | `/api/v1/branches` | CRUD de sucursales |
| Roles | `/api/v1/roles` | Roles y asignación de funcionalidades |
| Productos | `/api/v1/products` | Productos financieros |
| Subproductos | `/api/v1/management/subproducts` | Subproductos y configuración |
| Clientes | `/api/v1/customers` | Gestión de clientes |
| Cuentas | `/api/v1/accounts` | Apertura, consulta, holds, restricciones |
| Transacciones | `/api/v1/transactions` | Movimientos y operaciones |
| Sistema | `/api/v1/system` | Control de sistema y fecha de negocio |
| EoD | `/api/v1/eod` | Procesos de cierre de día |
| Menú | `/api/v1/menu` | Menú según permisos del usuario |

---

## Requisitos previos

- **Java 21**
- **Maven 3.8+**
- **Node.js 18+** y **npm**
- **PostgreSQL 14+** (local o contenedor)

---

## Configuración de base de datos

La configuración por defecto del backend está en `backend/src/main/resources/application.yml`:

| Parámetro | Valor por defecto |
|-----------|-------------------|
| URL JDBC | `jdbc:postgresql://localhost:5434/fintech` |
| Usuario | `postgres` |
| Contraseña | `postgres` |
| Puerto PostgreSQL | **5434** (no 5432) |

Al iniciar la aplicación, **Flyway** ejecuta automáticamente las migraciones ubicadas en:

`backend/src/main/resources/db/migration/`

Asegúrate de que PostgreSQL esté corriendo y que la base `fintech` exista antes de levantar el backend.

---

## Ejecución local

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

- API disponible en: **http://localhost:8080**
- Clase principal: `com.fintech.FintechApplication`

### 2. Frontend

```bash
cd frontend/cpa-fintech-web
npm install
npm run dev
```

- UI disponible en: **http://localhost:5173** (puerto por defecto de Vite)
- El frontend consume la API en `http://localhost:8080` (ver `src/api/axiosConfig.js`)

---

## Autenticación

1. El login se realiza contra `POST /api/v1/users/login` con `login`, `password` y `branchId`.
2. La API devuelve un **JWT** que el frontend guarda en `localStorage`.
3. Axios adjunta el token en el header `Authorization: Bearer <token>` en cada petición protegida.

Los permisos se modelan con **roles** y un árbol de **funcionalidades** (`MODULE` → `SUBMODULE` → `ACTION`).

---

## Arquitectura del backend

El backend sigue una organización **modular por dominio** dentro de un único servicio Spring Boot:

- **`core`**: operaciones bancarias del día a día.
- **`management`**: parametrización y administración del sistema.
- **`eod`**: procesos batch de cierre de día.
- **`security` + `config`**: autenticación, autorización y manejo de errores.
- **`audit`**: trazabilidad de acciones sensibles mediante AOP.

Patrón habitual por módulo: `controller` → `service` → `repository` → `domain`, con DTOs y mappers (MapStruct).

---

## Comandos útiles

```bash
# Compilar backend sin tests
cd backend && mvn clean package -DskipTests

# Build de producción del frontend
cd frontend/cpa-fintech-web && npm run build

# Lint del frontend
cd frontend/cpa-fintech-web && npm run lint
```

---

## Notas de desarrollo

- Hibernate está configurado con `ddl-auto: validate`: el esquema lo gestiona **Flyway**, no JPA.
- Spring Batch crea sus tablas (`BATCH_*`) al arrancar; los jobs no se ejecutan automáticamente (`spring.batch.job.enabled: false`).
- Revisa `application.yml` para ajustar credenciales de BD y configuración JWT antes de desplegar en otro entorno.
- No commitear secretos reales (JWT, passwords) en el repositorio; usa variables de entorno o perfiles (`application-dev.yml`, `application-prod.yml`).

---

## Próximos pasos sugeridos

- [ ] Documentar endpoints con OpenAPI / Swagger
- [ ] Agregar `docker-compose.yml` para PostgreSQL + API + frontend
- [ ] Perfiles Spring (`dev`, `prod`) con variables de entorno
- [ ] Tests de integración para login, cuentas y EoD
