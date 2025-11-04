# Architecture Diagrams

This document captures C4-style architecture views and Domain-Driven Design (DDD) bounded contexts for the Azure API-first microservices proof of concept. All diagrams are rendered with Mermaid so they can evolve alongside the codebase.

## Level 1 — System Context

```mermaid
%%{init: {"theme": "forest", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#0F766E", "primaryBorderColor": "#115E59", "primaryTextColor": "#F0FDFA", "secondaryColor": "#ECFEFF", "tertiaryColor": "#CCFBF1"}}}%%
flowchart TB
  subgraph Platform[API-First Platform]
    SPA["Angular Frontend\nAngular 17"]
    API["Spring Boot API\nJava 17"]
    SQL[("Azure SQL\nManaged DB")]
    H2[("H2\nIn-memory DB")]
  end

  Admin(["Admin User\nManages accounts & insights"])
  User(["Standard User\nReviews profile & activity"])
  AzureAD[["Azure Active Directory\nIssues JWT tokens"]]
  APIM[["Azure API Management\nPolicies & throttling"]]
  Insights[["Azure Application Insights\nTelemetry & observability"]]

  Admin -- "Uses" --> SPA
  User -- "Uses" --> SPA
  SPA -- "Obtains tokens" --> AzureAD
  SPA -- "Invokes" --> APIM
  APIM -- "Routes" --> API
  API -- "Validates tokens" --> AzureAD
  API -- "Publishes telemetry" --> Insights
  API -- "Reads/Writes" --> SQL
  API -- "Reads/Writes (local)" --> H2
```

### How to read this diagram

1. **Identify the primary actors.** Start with the two user personas on the left—Admin and Standard User. Each persona interacts with the Angular single-page application (SPA) over HTTPS.
2. **Follow authentication flows.** The SPA requests tokens from Azure Active Directory (Azure AD), which issues JWTs that will later be validated by the API.
3. **Trace API traffic.** User traffic enters Azure API Management (APIM), which enforces policies and throttling before routing requests to the Spring Boot API.
4. **Inspect data persistence.** The API reads and writes business data to Azure SQL in production and to an in-memory H2 database for local development.
5. **Observe telemetry.** Every API interaction emits telemetry to Azure Application Insights so that engineering and operations teams can monitor the system.

## Level 2 — Container View

```mermaid
%%{init: {"theme": "neutral", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#1D4ED8", "primaryTextColor": "#F8FAFC", "lineColor": "#1E293B", "secondaryColor": "#E0F2FE", "tertiaryColor": "#F1F5F9"}}}%%
flowchart TB
  subgraph Platform[Azure API-First Platform]
    SPA["Angular SPA\nNode + Angular"]
    subgraph AKS[AKS Cluster]
      APIMGW["APIM Self-Hosted Gateway\nAzure APIM"]
      Service["Spring Boot Service\nJava 17 + Spring"]
      Otel["OpenTelemetry Collector\nAzure Monitor"]
    end
    SQL[("Azure SQL Database\nPaaS")]
  end

  AppInsights[["Application Insights\nAzure Monitor"]]
  KeyVault[["Azure Key Vault\nSecrets"]]
  GitHub[["GitHub Actions\nCI/CD"]]
  Admin(["Admin\nManages users/audit dashboards"])
  EndUser(["End User\nViews profile & metrics"])

  Admin -- "HTTPS" --> SPA
  EndUser -- "HTTPS" --> SPA
  SPA -- "JWT bearer" --> APIMGW
  APIMGW -- "Routes REST" --> Service
  Service -- "CRUD" --> SQL
  Service -- "Fetches secrets" --> KeyVault
  Service -- "Exports telemetry" --> Otel
  Otel -- "Sends traces/metrics" --> AppInsights
  GitHub -- "Deploys manifests" --> AKS
```

### How to read this diagram

1. **Start at the platform boundary.** The SPA, AKS cluster, and Azure SQL database make up the Azure API-first platform. The SPA communicates with the backend services through standard HTTPS calls.
2. **Examine cluster responsibilities.** Inside the AKS cluster, the APIM self-hosted gateway receives requests, the Spring Boot service processes them, and the OpenTelemetry collector ships telemetry to Azure Monitor.
3. **Review external dependencies.** Application Insights captures traces and metrics, Azure Key Vault stores secrets, and GitHub Actions deploys Kubernetes manifests.
4. **Understand user interaction.** Both the admin and end user reach the SPA over HTTPS, which in turn uses JWT bearer tokens to call the gateway.
5. **Follow data and secret flows.** The Spring Boot service persists state in Azure SQL and securely retrieves configuration from Key Vault.

## Level 3 — Component View (Spring Boot API)

```mermaid
%%{init: {"theme": "dark", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#0EA5E9", "primaryTextColor": "#F8FAFC", "primaryBorderColor": "#0284C7", "lineColor": "#E2E8F0", "secondaryColor": "#075985", "tertiaryColor": "#1E293B"}}}%%
flowchart TB
  subgraph API[Spring Boot API]
    Controller["UserController\nSpring Web"]
    AuditController["AuditController\nSpring Web"]
    Service["UserService\nSpring Service"]
    AuditService["AuditService\nSpring Service"]
    Repository["UserRepository\nSpring Data JPA"]
    AuditRepository["AuditRepository\nSpring Data JPA"]
    Security["SecurityConfig\nSpring Security"]
    Mapper["UserMapper\nMapStruct"]
    Interceptor["AuditLogInterceptor\nSpring MVC"]
  end

  SQL[("Azure SQL\nUser & audit schema")]
  AzureAD[["Azure AD\nIdentity platform"]]

  Controller -- "Delegates" --> Service
  AuditController -- "Fetches audit stream" --> AuditService
  Service -- "Uses" --> Repository
  AuditService -- "Uses" --> AuditRepository
  Repository -- "Reads/Writes" --> SQL
  AuditRepository -- "Reads/Writes" --> SQL
  Security -- "Validates JWTs" --> AzureAD
  Interceptor -- "Persists request metadata" --> AuditService
  Service -- "Appends audit events" --> AuditService
  Mapper -- "Maps DTOs ↔ aggregates" --> Service
```

### How to read this diagram

1. **Locate entry points.** User-facing requests arrive at `UserController` or `AuditController`, implemented with Spring Web.
2. **Follow service orchestration.** Each controller delegates to its respective service layer (`UserService` or `AuditService`), which contains the core business logic.
3. **Trace persistence operations.** Services rely on repositories backed by Spring Data JPA to interact with Azure SQL, ensuring consistent data access patterns.
4. **Note cross-cutting concerns.** `SecurityConfig` validates JWT tokens against Azure AD, while `AuditLogInterceptor` records request metadata for observability.
5. **Understand data transformations.** `UserMapper` uses MapStruct to convert between transport-layer DTOs and domain aggregates, keeping mapping logic centralized.

## DDD Context Map

```mermaid
%%{init: {"theme": "base", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#4C1D95", "primaryTextColor": "#F5F3FF", "secondaryColor": "#DDD6FE", "tertiaryColor": "#EDE9FE", "lineColor": "#4C1D95"}}}%%
flowchart TD
  subgraph Identity[Identity & Access Context]
    direction TB
    AAD[AAD Integration Context]
    AuthPolicy[Authorization Policy]
  end

  subgraph UserMgmt[User Management Context]
    direction TB
    Aggregate[User Aggregate]
    ServiceLayer[User Service]
    Repo[User Repository]
  end

  subgraph Audit[Audit & Observability Context]
    direction TB
    AuditStream[Audit Stream]
    AuditStore[Audit Repository]
    Metrics[Telemetry Export]
  end

  AAD -- "Publishes" --> TokenClaims[(JWT Claims)]
  TokenClaims -- "Conformist" --> Aggregate
  Aggregate -- "Domain Events" --> AuditStream
  AuditStream --> AuditStore
  AuditStream --> Metrics
  ServiceLayer --> Repo
  Repo -- "Shared Kernel (User Schema)" --> AuditStore

  classDef boundedContext fill:#DDD6FE,stroke:#4C1D95,stroke-width:2px,color:#1F2937;
  class Identity,UserMgmt,Audit boundedContext;
  classDef integration fill:#C7D2FE,stroke:#1E3A8A,color:#1E3A8A;
  class AAD,TokenClaims integration;
  classDef domain fill:#EDE9FE,stroke:#4C1D95,color:#1F2937;
  class Aggregate,ServiceLayer,Repo,AuditStream,AuditStore,Metrics,AuthPolicy domain;
```

### How to read this diagram

1. **Identify bounded contexts.** Three contexts—Identity & Access, User Management, and Audit & Observability—each own their domain models and responsibilities.
2. **Study integrations.** The Identity context publishes JWT claims that the User Management context consumes in a conformist relationship, aligning authorization requirements.
3. **Trace domain events.** The User Aggregate emits domain events to the Audit Stream, which both stores audit records and exports telemetry.
4. **Observe shared data contracts.** The User Repository and Audit Store share a kernel around the user schema to guarantee consistent representations of user data across contexts.
5. **Assess policy placement.** Authorization policies reside in the Identity context, making it clear where access control rules are defined.

## Use Case Sequence — "Create User"

```mermaid
%%{init: {"theme": "neutral", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#0F172A", "primaryTextColor": "#F8FAFC", "secondaryColor": "#CBD5F5", "tertiaryColor": "#E2E8F0", "lineColor": "#0F172A"}}}%%
sequenceDiagram
  autonumber
  participant Admin as Admin User
  participant SPA as Angular Frontend
  participant APIM as APIM Gateway
  participant API as Spring Boot API
  participant Service as UserService
  participant Repo as UserRepository
  participant Audit as AuditService
  participant SQL as Azure SQL
  participant Insights as App Insights

  Admin->>SPA: Submit "Create User" form
  SPA->>SPA: Validate form & acquire access token
  SPA->>APIM: POST /api/v1/users (JWT)
  APIM->>API: Forward request
  API->>Service: mapToCommand(payload)
  Service->>Repo: save(UserAggregate)
  Repo->>SQL: INSERT user
  Service->>Audit: recordCreated(userId)
  Audit->>SQL: INSERT audit entry
  Audit->>Insights: TrackEvent(UserCreated)
  API-->>SPA: 201 Created + payload
  SPA-->>Admin: Show success toast
```

### How to read this diagram

1. **Initiate the workflow.** The admin user submits the "Create User" form from the Angular frontend, which validates input and acquires an access token.
2. **Follow the API call.** The SPA sends an authenticated POST request through APIM, which forwards it to the Spring Boot API.
3. **Trace command handling.** The API maps the payload into a command object and invokes `UserService`, which persists the `UserAggregate` via `UserRepository`.
4. **Record auditing actions.** `UserService` triggers `AuditService` to write an audit entry and emit a telemetry event to Application Insights.
5. **Return the response.** The API responds with `201 Created`, the SPA relays success feedback to the admin, and telemetry confirms the operation completed.

These diagrams can be copied into documentation portals or Azure DevOps wikis to communicate the system from multiple viewpoints.
