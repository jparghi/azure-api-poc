
## 🧩 `codex_instructions_poc_azure_api_first.md`

### 🏗️ Title

**POC – Azure API-First Microservices Platform with Observability & CI/CD on AKS**

### 🧭 Description

Generate a complete repository demonstrating a **secure, observable, API-first Java + Angular architecture** deployed to **Azure Kubernetes Service (AKS)** using **GitHub Actions CI/CD**, **Azure APIM**, and **App Insights**.
This POC aligns 100 % with the **Tech Mahindra Application Architect** JD and highlights full-stack design, cloud architecture, DevSecOps, and observability.

---

### 🗂️ Repository Layout

```
azure-api-first-poc/
├── backend/
│   ├── src/main/java/com/example/api/
│   │   ├── controller/UserController.java
│   │   ├── service/UserService.java
│   │   ├── model/User.java
│   │   ├── repository/UserRepository.java
│   │   └── config/SecurityConfig.java
│   ├── resources/application.yml
│   ├── Dockerfile
│   ├── pom.xml
├── frontend/
│   ├── src/app/
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
├── manifests/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── secret.yaml
│   └── configmap.yaml
├── ci-cd/
│   └── github-actions-aks.yml
├── openapi/
│   └── openapi.yaml
└── README.md
```

---

### ⚙️ CodeX Instructions

#### 1️⃣ Initialize Repository

```bash
repo_name="azure-api-first-poc"
description="Azure API-First Java + Angular microservices with CI/CD, AKS, APIM, and observability."
codex create-repo $repo_name --description "$description"
```

---

#### 2️⃣ Backend (Spring Boot)

**Tech:** Java 17 · Spring Boot 3.x · Azure SQL · Micrometer
**Endpoints:**

* `/api/v1/users` – CRUD
* `/api/v1/audit` – audit log stream
* `/actuator/*` – health & metrics

**Security:**

* Azure AD OAuth2 JWT
* Roles: `ADMIN`, `USER`

**Observability:**

* Micrometer → Prometheus
* App Insights exporter
* `AuditLogInterceptor` logging every request

**Dockerfile (backend):**

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

**Build & push image:**

```bash
docker build -t ghcr.io/<user>/backend:latest .
docker push ghcr.io/<user>/backend:latest
```

---

#### 3️⃣ Frontend (Angular 17)

**Features:**

* Login with Azure AD → fetch JWT
* Display users, audit logs, API health

**Dockerfile (frontend):**

```dockerfile
FROM node:20 as build
WORKDIR /app
COPY . .
RUN npm install && npm run build --prod
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
```

---

#### 4️⃣ Kubernetes Deployment (`/manifests`)

| File                | Purpose                         |
| ------------------- | ------------------------------- |
| **deployment.yaml** | Deploy backend + frontend pods  |
| **service.yaml**    | Expose ClusterIP + LoadBalancer |
| **ingress.yaml**    | NGINX ingress + TLS cert        |
| **secret.yaml**     | Azure SQL conn + JWT secrets    |
| **configmap.yaml**  | App config & APIM base URL      |

**Namespace:** `api-first-demo`

```bash
kubectl apply -f manifests/
kubectl get pods -n api-first-demo
```

---

#### 5️⃣ CI/CD (GitHub Actions)

File: `.github/workflows/github-actions-aks.yml`

```yaml
name: Build and Deploy to AKS
on:
  push:
    branches: [ main ]

jobs:
  build-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build backend
        run: mvn -B package --file backend/pom.xml
      - name: Build Docker images
        run: |
          docker build -t ghcr.io/${{ github.actor }}/backend:latest ./backend
          docker build -t ghcr.io/${{ github.actor }}/frontend:latest ./frontend
      - name: Push to GHCR
        run: |
          echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker push ghcr.io/${{ github.actor }}/backend:latest
          docker push ghcr.io/${{ github.actor }}/frontend:latest
      - name: Deploy to AKS
        uses: azure/aks-set-context@v3
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
          resource-group: ${{ secrets.AKS_RESOURCE_GROUP }}
          cluster-name: ${{ secrets.AKS_CLUSTER_NAME }}
      - run: kubectl apply -f manifests/
```

Secrets required:
`AZURE_CREDENTIALS`, `AKS_RESOURCE_GROUP`, `AKS_CLUSTER_NAME`, `ACR_LOGIN_SERVER`

---

#### 6️⃣ OpenAPI Spec (`openapi/openapi.yaml`)

```yaml
openapi: 3.0.1
info:
  title: Azure API-First Demo
  version: 1.0.0
paths:
  /users:
    get: { summary: "List users", security: [{ bearerAuth: [] }] }
    post: { summary: "Create user" }
  /audit:
    get: { summary: "List audit events" }
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

---

#### 7️⃣ README.md Template

````markdown
# Azure API-First Microservices POC (AKS + GitHub Actions)

## 🧱 Architecture
```mermaid
flowchart LR
  A[Angular UI] --> B[APIM Gateway]
  B --> C[Spring Boot API]
  C --> D[(Azure SQL)]
  C --> E[App Insights]
````

## ⚙️ Tech Stack

Angular 17 · Java 17 · Spring Boot 3 · Azure SQL · Azure AD JWT · AKS · APIM · App Insights · GitHub Actions

## 🚀 Steps

1. Clone & build backend
2. Push Docker images
3. `kubectl apply -f manifests/`
4. Access public IP from `kubectl get svc`
5. Monitor logs in Azure Portal → Application Insights

## 🔍 Verify

* `GET /api/v1/users` → 200 OK
* `GET /api/v1/audit` → audit trail
* App Insights metrics visible

## 🧠 Enhancements

* Add Service Bus for async events
* Add Helm chart for manifests
* Integrate APIM for rate limiting

```
