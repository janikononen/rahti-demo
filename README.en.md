# rahti-demo

🇬🇧 **English** | 🇫🇮 [Suomeksi](README.md)

## Introduction

The purpose of this repository is to clarify deployment practices and resolve common uncertainties related to the CSC Rahti container platform. This project provides practical examples of a Dockerfile, Java layered architecture, necessary dependencies, and npm scripts located at the project root to simplify local development and deployment. The repository is based on the [Haaga-Helia Rahti deployment guidelines](https://github.com/software-development-project-1/software-development-project-1.github.io/blob/main/material/backend-deployment.md).

## Docker

You need Docker installed on your machine to build a container image from the Dockerfile. The container image serves as the blueprint/recipe for running containers. You can download and install Docker from the [official Docker website](https://docs.docker.com/desktop/).

You do not need to execute Docker commands manually in your terminal, as the npm scripts in the project root handle these commands for local development and testing.

> **Note:** On Windows, Docker Desktop must be running in order to execute Docker commands.

## Local Development Environment and Testing

Docker and containers allow you to test the application locally. The test environment is designed to mirror the Rahti production environment and consists of two containers:

- **PostgreSQL container** (database)
- **Java container** (backend service)

The backend container image is built using the [demo/Dockerfile](demo/Dockerfile). The PostgreSQL database image is pulled directly from Docker Hub.

The local environment is set up in the following steps:

1. A shared Docker virtual network (`demo-net`) is created.
2. The database container (`postgres-db`) is started on the `demo-net` network with database environment variables configured (`-e` / _environment variables_).
3. The backend container is started on the same `demo-net` network, allowing the backend and database to communicate via their network container names. The required database connection environment variables are passed to the backend.

### npm Scripts

#### build:app:image

Builds the backend application Docker image from the Dockerfile for local testing or deployment.

```bash
npm run build:app:image
```

#### docker:network:local

Creates the `demo-net` Docker virtual network so the database and application containers can communicate using container names.

```bash
npm run docker:network:local
```

#### docker:postgresql:local

Starts a PostgreSQL 16 container in the background on the `demo-net` network for local testing.

```bash
npm run docker:postgresql:local
```

#### docker:app:local

Starts the backend container on the `demo-net` network, sets the necessary environment variables, and forwards port `8080` to the host machine.

```bash
npm run docker:app:local
```

#### dev

Starts the entire local development and testing environment by sequentially creating the network, launching the database, and starting the backend application.

```bash
npm run dev
```

#### clean:local

Stops and removes the local `postgres-db` container and deletes the `demo-net` network, cleaning up all test resources.

```bash
npm run clean:local
```

## Rahti and Pukki

### Overview and Architecture

In the production environment, application hosting is based on CSC's Rahti container platform and the Pukki database service.

![Rahti Architecture](assets/rahti-architecture.jpg)

#### Operating Principle:

1. **Source Code & Dockerfile (GitHub):** The GitHub repository and the relative path to the Dockerfile (`demo/Dockerfile`) are specified in Rahti.
2. **Automated Build (Rahti):** Rahti fetches the code from the repository and automatically builds the runnable Docker image.
3. **Configuration via Environment Variables:** The backend container is launched in Rahti with the required environment variables (such as database credentials and active Spring profile).
4. **Pukki Database:** The database is hosted on CSC's managed PostgreSQL service (Pukki). Allowed access URLs/IPs (_Allowed URL / IP_) are defined in Pukki so only the backend running in Rahti can access the database.

---

## Environment Variables

Environment variables separate application configuration and secrets from the source code (following the _12-Factor App_ methodology). This allows the same Docker image to run across different environments without code modifications or rebuilding.

### Configurations and Profiles

The application defines different profiles for various environments:

- **`dev` (Development environment - [demo/src/main/resources/application-dev.yaml](demo/src/main/resources/application-dev.yaml)):**
  - Uses an in-memory H2 database (`jdbc:h2:mem:devdb`).
  - Does not require an external database server or passwords.
  - Activated by default.

- **`prod` (Production and containerized testing - [demo/src/main/resources/application-prod.yaml](demo/src/main/resources/application-prod.yaml)):**
  - Uses the PostgreSQL driver and dynamically reads connection parameters from environment variables.

### Used Environment Variables

| Environment Variable     | Description                           | Example (Local / Docker)  | Example (Rahti & Pukki) |
| :----------------------- | :------------------------------------ | :------------------------ | :---------------------- |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile                 | `prod`                    | `prod`                  |
| `DB_HOST`                | Database server host / hostname       | `postgres-db` (container) | `pukki-db-host.csc.fi`  |
| `DB_PORT`                | Database port                         | `5432`                    | `5432`                  |
| `DB_NAME`                | Database name                         | `demodb`                  | `demodb`                |
| `DB_USERNAME`            | Database username                     | `postgres`                | `pukki_username`        |
| `DB_PASSWORD`            | Database password                     | `secret`                  | _(Secret password)_     |

### Why Use Environment Variables?

1. **Security:** Passwords, credentials, and production hostnames are never committed to GitHub or hardcoded into Dockerfiles.
2. **Portability:** The exact same Docker image runs on local developer machines, staging servers, and Rahti simply by passing different environment variables.
3. **Maintainability:** When database credentials or endpoints change, the application does not need to be recompiled or rebuilt—updating the container's environment variables is sufficient.
