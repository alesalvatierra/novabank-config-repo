# 🏦 NovaBank

> Sistema de gestión bancaria basado en una **Arquitectura de Microservicios Síncronos** desarrollada con Spring Boot y Spring Cloud.

NovaBank fragmenta toda la lógica de negocio en dominios independientes (Clientes, Cuentas y Operaciones), garantizando la persistencia de datos aislada en PostgreSQL para evitar el acoplamiento entre servicios.

La arquitectura incluye descubrimiento de servicios dinámico, configuración centralizada, enrutamiento a través de un API Gateway y un servidor de autorización independiente para blindar las comunicaciones mediante seguridad OAuth2/JWT. Además, implementa tolerancia a fallos con el patrón Circuit Breaker.

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Descripción |
|---|---|
| **Java 17** | Lenguaje principal del proyecto |
| **Spring Boot 3** | Framework base para la creación de los microservicios |
| **Spring Cloud (Eureka & Config)** | Descubrimiento de servicios y configuración externalizada |
| **Spring Cloud Gateway** | Punto único de entrada y enrutamiento dinámico |
| **Spring Cloud OpenFeign** | Comunicación síncrona declarativa entre microservicios |
| **Resilience4j** | Implementación de resiliencia (Circuit Breaker y Fallbacks) |
| **Spring Security & JWT** | Autenticación distribuida y protección de endpoints en el Gateway |
| **Spring Data JPA / Hibernate** | ORM para el mapeo de entidades en bases de datos aisladas |
| **PostgreSQL** | Motor de base de datos relacional para la persistencia |
| **JUnit 5 & Mockito** | Frameworks para pruebas unitarias |
| **Maven** | Gestión de dependencias y construcción multi-módulo |

---

## ✅ Requisitos del Sistema

Para compilar y ejecutar este proyecto distribuido en un entorno local es estrictamente necesario tener instalados:

- **Java:** JDK 17 o superior
- **Maven:** 3.8 o superior
- **PostgreSQL:** Instalado y en ejecución en el puerto `5432`
- **Git:** Necesario para que el Config Server lea el repositorio de propiedades locales

---

## 🌐 Arquitectura

La configuración de la base de datos y de las aplicaciones está centralizada y gestionada por el **Config Server** a través de un repositorio Git. La arquitectura se distribuye en los siguientes puertos locales:

| Servicio | URL |
|---|---|
| **Eureka Server** | http://localhost:8761 |
| **Config Server** | http://localhost:8888 |
| **Auth Server** | http://localhost:9000 |
| **API Gateway** | http://localhost:8080 *(punto de entrada único)* |
| **Microservicios de negocio** | Puertos dinámicos (ej. 8081, 8082, 8083) |

### Diagrama de Flujo

```
[ API Gateway — Puerto 8080 ]
        Enruta peticiones y valida Tokens JWT
                       ⬇
[ Auth Server — Puerto 9000 ]
        Emite los Tokens JWT (Provider)
                       ⬇
[ Infraestructura Spring Cloud ]
        Eureka (Descubrimiento) + Config Server (Propiedades)
                       ⬇
[ Microservicios de Negocio ]
        Cliente-Service · Cuenta-Service · Operacion-Service
                       ⬇
[ Bases de Datos Aisladas ]
        Múltiples esquemas PostgreSQL por dominio
```

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
3. `ApiGatewayApplication` y `AuthServerApplication` — Pueden arrancar en paralelo
4. Servicios de negocio: `ClienteServiceApplication`, `CuentaServiceApplication`, `OperacionServiceApplication`

### 3. Ejecutar las pruebas unitarias

```bash
mvn test
```

---

## 🔐 Pruebas Manuales con Postman

Dado que la arquitectura está protegida por el Gateway, el flujo de pruebas debe realizarse autenticándose previamente:

**Paso 1 — Obtener el Token JWT:**

```
POST http://localhost:9000/auth/login?username=admin&password=admin
```

**Paso 2 — Copiar el token** alfanumérico devuelto en la respuesta.

**Paso 3 — Hacer peticiones al Gateway:**

Dirígete a cualquier endpoint de negocio a través del Gateway, por ejemplo:

```
POST http://localhost:8080/api/clientes
```

**Paso 4 — Autorizar en Postman:**

En la pestaña **Authorization**, selecciona el tipo **Bearer Token** y pega el token copiado. ¡Ya puedes operar sobre toda la red de microservicios!

---

## 🧩 Patrones de Diseño Aplicados

| Patrón | Descripción |
|---|---|
| **API Gateway** | Actúa como fachada única del sistema, ocultando la topología de red interna y centralizando la validación de seguridad |
| **Service Discovery** | Permite que los microservicios se encuentren dinámicamente mediante nombres lógicos (Eureka) sin usar IPs fijas |
| **Circuit Breaker** | Implementado con Resilience4j para evitar caídas en cascada si un servicio dependiente deja de responder, ejecutando un plan de Fallback |
| **DTO (Data Transfer Object)** | Encapsula los datos enviados entre el cliente y el servidor, y entre los propios microservicios (Feign), aislando las entidades JPA reales |

---

## 📁 Estructura del Proyecto (Multi-módulo)

```
NovaBank/
├── eureka-server        # Servidor de descubrimiento de Netflix
├── config-server        # Configuración centralizada conectada a Git
├── api-gateway          # Enrutador dinámico y filtro de seguridad JWT
├── auth-server          # Microservicio independiente de control de acceso
├── cliente-service      # Bounded Context para la gestión de usuarios
├── cuenta-service       # Bounded Context con resiliencia para saldos y movimientos
└── operacion-service    # Bounded Context orquestador de transacciones (OpenFeign)
```

---

## 🔗 Repositorio

📌 [https://github.com/alesalvatierra/novabank-config-repo.git](https://github.com/alesalvatierra/NovaBank.git)
