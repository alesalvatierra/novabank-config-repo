# 🏦 NovaBank

> Sistema de gestión bancaria basado en una **Arquitectura de Microservicios Reactivos** desarrollada con Spring Boot y Spring Cloud.

NovaBank fragmenta la lógica de negocio en dominios independientes (`Clientes`, `Cuentas` y `Operaciones`), garantizando la persistencia aislada en PostgreSQL para evitar acoplamiento entre servicios.

La arquitectura incluye descubrimiento dinámico de servicios, configuración centralizada, enrutamiento a través de un API Gateway y un servidor de autenticación independiente para proteger las comunicaciones mediante JWT. Además, incorpora tolerancia a fallos con **Resilience4j**, reintentos, fallback y **caché en memoria** para el servicio de tipos de cambio.

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Descripción |
|---|---|
| **Java 17** | Lenguaje principal del proyecto |
| **Spring Boot 3** | Framework base para la creación de los microservicios |
| **Spring WebFlux** | Programación reactiva no bloqueante |
| **Spring Data R2DBC** | Persistencia reactiva sobre PostgreSQL |
| **Spring Cloud (Eureka & Config)** | Descubrimiento de servicios y configuración externalizada |
| **Spring Cloud Gateway** | Punto único de entrada y enrutamiento dinámico |
| **WebClient** | Comunicación reactiva entre microservicios |
| **Resilience4j** | Implementación de resiliencia con Circuit Breaker, Retry y Fallback |
| **Caffeine Cache** | Caché en memoria para almacenar tasas de cambio |
| **Spring Security & JWT** | Autenticación distribuida y protección de endpoints en el Gateway |
| **PostgreSQL** | Motor de base de datos relacional para la persistencia |
| **JUnit 5, Mockito y Reactor Test** | Frameworks para pruebas unitarias reactivas |
| **Maven** | Gestión de dependencias y construcción multi-módulo |

---

## ✅ Requisitos del Sistema

Para compilar y ejecutar este proyecto distribuido en un entorno local es necesario tener instalados:

- **Java:** JDK 17 o superior
- **Maven:** 3.8 o superior
- **PostgreSQL:** instalado y en ejecución en el puerto `5432`
- **Git:** necesario para que el Config Server lea el repositorio de propiedades

---

## 🌐 Arquitectura

La configuración de la base de datos y de las aplicaciones está centralizada y gestionada por el **Config Server** a través de un repositorio Git. La arquitectura se distribuye en los siguientes puertos locales:

| Servicio | URL |
|---|---|
| **Eureka Server** | http://localhost:8761 |
| **Config Server** | http://localhost:8888 |
| **Auth Server** | http://localhost:9000 |
| **API Gateway** | http://localhost:8080 *(punto de entrada único)* |
| **Cliente Service** | http://localhost:8081 |
| **Cuenta Service** | http://localhost:8082 |
| **Operacion Service** | http://localhost:8083 |
| **Exchange Rate Mock Service** | http://localhost:8085 |

### Diagrama de Flujo

```text
[ API Gateway — Puerto 8080 ]
        Enruta peticiones y valida tokens JWT
                       ⬇
[ Auth Server — Puerto 9000 ]
        Emite los tokens JWT
                       ⬇
[ Infraestructura Spring Cloud ]
        Eureka (Descubrimiento) + Config Server (Propiedades)
                       ⬇
[ Microservicios de Negocio ]
        Cliente-Service · Cuenta-Service · Operacion-Service
                       ⬇
[ Servicio Externo Mock ]
        Exchange-Rate-Mock-Service
                       ⬇
[ Bases de Datos Aisladas ]
        Múltiples bases PostgreSQL por dominio
````

---

## 🚀 Guía de Uso

Al tratarse de una arquitectura de microservicios, **el orden de arranque es crucial**.

### 1. Compilar todo el proyecto

Abre la carpeta raíz del proyecto (donde está el `pom.xml` padre) y ejecuta:

```bash
mvn clean install
```

### 2. Orden de ejecución

Arranca los servicios en este orden exacto para garantizar una comunicación correcta:

1. `EurekaServerApplication` — Espera a que arranque completamente
2. `ConfigServerApplication` — Espera a que arranque completamente
3. `AuthServerApplication`
4. `ClienteServiceApplication`
5. `CuentaServiceApplication`
6. `OperacionServiceApplication`
7. `ExchangeRateMockServiceApplication`
8. `ApiGatewayApplication`

### 3. Ejecutar las pruebas unitarias

```bash
mvn test
```

---

## 🔐 Pruebas Manuales con Postman

Dado que la arquitectura está protegida por el Gateway, el flujo de pruebas debe realizarse autenticándose previamente.

**Paso 1 — Obtener el token JWT:**

```http
POST http://localhost:9000/auth/login?username=admin&password=admin
```

**Paso 2 — Copiar el token** devuelto en la respuesta.

**Paso 3 — Hacer peticiones al Gateway:**

Dirígete a cualquier endpoint de negocio a través del Gateway, por ejemplo:

```http
POST http://localhost:8080/api/clientes
```

**Paso 4 — Autorizar en Postman:**

En la pestaña **Authorization**, selecciona el tipo **Bearer Token** y pega el token copiado.

---

## 📡 Endpoints principales

### Auth Server
```http
POST /auth/login
```

### Cliente Service
```http
GET /api/clientes
GET /api/clientes/{id}
POST /api/clientes
```

### Cuenta Service
```http
GET /api/cuentas/{id}
POST /api/cuentas
PUT /api/cuentas/{id}/saldo?monto=...
POST /api/cuentas/movimientos
GET /api/cuentas/movimientos/stream
```

### Operacion Service
```http
POST /api/operaciones
GET /api/operaciones/{id}
GET /api/operaciones
```

### Exchange Rate Mock Service
```http
GET /api/exchange/rate?from=USD&to=EUR
```

---

## 🧩 Patrones de Diseño Aplicados

| Patrón | Descripción |
|---|---|
| **API Gateway** | Actúa como fachada única del sistema, ocultando la topología de red interna y centralizando la validación de seguridad |
| **Service Discovery** | Permite que los microservicios se encuentren dinámicamente mediante nombres lógicos en Eureka |
| **Circuit Breaker** | Implementado con Resilience4j para evitar caídas en cascada si un servicio dependiente deja de responder |
| **Retry** | Reintenta automáticamente llamadas fallidas al servicio de divisas |
| **Fallback** | Recupera una respuesta alternativa cuando el servicio de divisas no está disponible |
| **DTO (Data Transfer Object)** | Encapsula los datos enviados entre cliente y servidor, aislando las entidades reales |
| **Programación Reactiva** | Uso de `Mono` y `Flux` para flujos no bloqueantes entre servicios |

---

## ♻️ Resiliencia

En `operacion-service` se ha implementado tolerancia a fallos sobre la llamada al microservicio de tipos de cambio:

- **Circuit Breaker**
- **Retry**
- **Fallback**
- **Caché con Caffeine**

Si `exchange-rate-mock-service` no responde, el sistema intenta recuperar la última tasa válida almacenada en caché para no interrumpir completamente el flujo de transferencias.

---

## 📁 Estructura del Proyecto (Multi-módulo)

```text
NovaBank/
├── eureka-server                # Servidor de descubrimiento de servicios
├── config-server                # Configuración centralizada conectada a Git
├── api-gateway                  # Enrutador dinámico y filtro de seguridad JWT
├── auth-server                  # Microservicio de autenticación
├── cliente-service              # Gestión reactiva de clientes
├── cuenta-service               # Gestión reactiva de cuentas y movimientos
├── operacion-service            # Orquestación reactiva de transferencias
└── exchange-rate-mock-service   # Mock reactivo de tipos de cambio
```

---

## 🧪 Pruebas

El proyecto incluye pruebas unitarias sobre la lógica de servicio utilizando:

- **JUnit 5**
- **Mockito**
- **Reactor Test**
- **StepVerifier**

Estas pruebas permiten validar comportamiento reactivo, repositorios simulados y control de errores.

---

## 🔗 Repositorios

📌 **Repositorio principal / configuración utilizada en el proyecto:**  
[https://github.com/alesalvatierra/novabank-config-repo.git](https://github.com/alesalvatierra/novabank-config-repo.git)
