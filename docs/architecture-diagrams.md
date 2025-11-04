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

These diagrams can be copied into documentation portals or Azure DevOps wikis to communicate the system from multiple viewpoints.
