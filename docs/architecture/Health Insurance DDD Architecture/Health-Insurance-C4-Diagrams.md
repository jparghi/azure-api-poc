# Health Insurance Platform C4 Views

The following views capture the Health Insurance platform through the C4 lens, from the system context down to the Spring Boot API components. Each Mermaid diagram applies a rich theme so it can be embedded directly into other Markdown or documentation portals.

## Level 1 — System Context

```mermaid
%%{init: {"theme": "forest", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#0F766E", "primaryBorderColor": "#115E59", "primaryTextColor": "#F0FDFA", "secondaryColor": "#ECFEFF", "tertiaryColor": "#CCFBF1"}}}%%
flowchart TB
  subgraph Platform[Health Insurance Platform]
    Portal["Member & Provider Portal\nAngular 17"]
    ClaimsAPI["Claims Processing API\nSpring Boot"]
    PolicyDB[("Policy & Claims DB\nAzure SQL")] 
    Analytics[("Analytics Warehouse\nAzure Synapse")] 
  end

  Member(["Member\nManages coverage & claims"])
  Provider(["Provider\nSubmits encounters"])
  Agent(["Customer Service Agent\nSupports members"])
  AzureAD[["Azure Active Directory\nIssues JWT tokens"]]
  APIM[["Azure API Management\nInbound policies"]]
  Insights[["Application Insights\nTelemetry & tracing"]]

  Member -- "Uses" --> Portal
  Provider -- "Uses" --> Portal
  Agent -- "Uses" --> Portal
  Portal -- "Obtains tokens" --> AzureAD
  Portal -- "Invokes" --> APIM
  APIM -- "Routes" --> ClaimsAPI
  ClaimsAPI -- "Validates JWT" --> AzureAD
  ClaimsAPI -- "Persists" --> PolicyDB
  ClaimsAPI -- "Publishes" --> Analytics
  ClaimsAPI -- "Emits telemetry" --> Insights
```

### Reading the system context

1. **Start with personas.** Members, providers, and support agents all interact with the Angular portal over HTTPS.
2. **Follow authentication.** The portal secures requests by fetching JWTs from Azure Active Directory before calling downstream services.
3. **Observe governance.** Azure API Management enforces policies, throttling, and routing before forwarding requests to the Spring Boot API.
4. **Track data flows.** Core policy and claim records live in Azure SQL, while curated analytics feeds flow into Azure Synapse.
5. **Monitor operations.** Application Insights captures end-to-end telemetry to power diagnostics and SRE workflows.

## Level 2 — Container View

```mermaid
%%{init: {"theme": "neutral", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#1D4ED8", "primaryTextColor": "#F8FAFC", "lineColor": "#1E293B", "secondaryColor": "#E0F2FE", "tertiaryColor": "#F1F5F9"}}}%%
flowchart TB
  subgraph Platform[Health Insurance Platform]
    Portal["Angular SPA\nNode.js + Angular"]
    subgraph AKS[AKS Cluster]
      Gateway["APIM Self-Hosted Gateway\nAzure API Management"]
      ClaimsService["Claims Service\nSpring Boot"]
      NotificationSvc["Notification Service\nAzure Functions"]
      Otel["OpenTelemetry Collector\nAzure Monitor"]
    end
    SqlDB[("Azure SQL Database\nPolicy & Claims")] 
    Blob[("Azure Blob Storage\nDocument Archive")]
  end

  Synapse[["Azure Synapse\nAnalytical models"]]
  KeyVault[["Azure Key Vault\nSecrets & certificates"]]
  GitHub[["GitHub Actions\nCI/CD pipelines"]]
  ServiceBus[["Azure Service Bus\nAsynchronous messaging"]]

  Portal -- "HTTPS" --> Gateway
  Gateway -- "Routes REST" --> ClaimsService
  ClaimsService -- "CRUD" --> SqlDB
  ClaimsService -- "Stores artifacts" --> Blob
  ClaimsService -- "Publishes events" --> ServiceBus
  ServiceBus -- "Triggers" --> NotificationSvc
  ClaimsService -- "Fetches secrets" --> KeyVault
  ClaimsService -- "Exports telemetry" --> Otel
  Otel -- "Ships traces" --> Synapse
  GitHub -- "Deploys manifests" --> AKS
  NotificationSvc -- "Sends alerts" --> Portal
```

### Reading the container view

1. **Identify boundaries.** The SPA, AKS cluster, Azure SQL, and Blob Storage define the platform perimeter.
2. **Examine workload placement.** API management and core claims processing run in AKS, while event-driven notifications execute in Azure Functions.
3. **Trace persistence.** Claims data persists to Azure SQL, and claim attachments are archived in Blob Storage for compliance.
4. **Understand integrations.** Service Bus decouples claims events from downstream notifications and analytics workloads.
5. **Secure and observe.** Key Vault centralizes secrets, and the OpenTelemetry collector relays metrics and traces to Synapse-backed analytics.

## Level 3 — Component View (Claims API)

```mermaid
%%{init: {"theme": "dark", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#0EA5E9", "primaryTextColor": "#F8FAFC", "primaryBorderColor": "#0284C7", "lineColor": "#E2E8F0", "secondaryColor": "#075985", "tertiaryColor": "#1E293B"}}}%%
flowchart TB
  subgraph ClaimsAPI[Claims Processing API]
    ClaimController["ClaimController\nSpring Web"]
    MemberController["MemberController\nSpring Web"]
    ClaimService["ClaimService\nSpring Service"]
    MemberService["MemberService\nSpring Service"]
    ClaimRepository["ClaimRepository\nSpring Data JPA"]
    MemberRepository["MemberRepository\nSpring Data JPA"]
    WorkflowOrchestrator["WorkflowOrchestrator\nCamunda"]
    SecurityConfig["SecurityConfig\nSpring Security"]
    ClaimMapper["ClaimMapper\nMapStruct"]
    AuditInterceptor["AuditInterceptor\nSpring MVC"]
  end

  SqlDB[("Azure SQL\nClaims & Members schema")]
  AzureAD[["Azure AD\nIdentity platform"]]
  ServiceBus[["Azure Service Bus\nEvents"]]

  ClaimController -- "Delegates" --> ClaimService
  MemberController -- "Delegates" --> MemberService
  ClaimService -- "Uses" --> ClaimRepository
  MemberService -- "Uses" --> MemberRepository
  ClaimService -- "Initiates" --> WorkflowOrchestrator
  ClaimRepository -- "Reads/Writes" --> SqlDB
  MemberRepository -- "Reads/Writes" --> SqlDB
  WorkflowOrchestrator -- "Emits events" --> ServiceBus
  SecurityConfig -- "Validates JWT" --> AzureAD
  AuditInterceptor -- "Captures metadata" --> ClaimService
  ClaimService -- "Appends audit trail" --> ServiceBus
  ClaimMapper -- "Maps DTOs ↔ aggregates" --> ClaimService
```

### Reading the component view

1. **Locate entry points.** `ClaimController` and `MemberController` expose REST endpoints for claims submission and member profile management.
2. **Follow service orchestration.** Services encapsulate domain logic and orchestrate workflows through Camunda-based state machines.
3. **Inspect persistence.** Repositories translate aggregates into SQL records while safeguarding transactional boundaries.
4. **Highlight cross-cutting concerns.** `SecurityConfig` enforces JWT validation, `AuditInterceptor` captures request metadata, and Service Bus receives domain events for downstream processing.
5. **Clarify mapping.** MapStruct centralizes conversions between DTOs, entities, and domain aggregates.

## DDD Context Map

```mermaid
%%{init: {"theme": "base", "themeVariables": {"fontFamily": "Inter,Segoe UI,Arial", "primaryColor": "#4C1D95", "primaryTextColor": "#F5F3FF", "secondaryColor": "#DDD6FE", "tertiaryColor": "#EDE9FE", "lineColor": "#4C1D95"}}}%%
flowchart TD
  subgraph Enrollment[Enrollment Context]
    direction TB
    Eligibility[Eligibility Engine]
    PolicyBinder[Policy Binder]
  end

  subgraph Claims[Claims Management Context]
    direction TB
    ClaimsCore[Claims Core Domain]
    ClaimsWorkflow[Workflow Engine]
    ClaimsLedger[Claims Ledger]
  end

  subgraph Support[Support & Engagement Context]
    direction TB
    PortalExperience[Portal Experience]
    NotificationHub[Notification Hub]
  end

  AzureAD[[Azure AD]]
  DataLake[(Enterprise Data Lake)]

  Eligibility -- "Publishes" --> PolicyBinder
  PolicyBinder -- "Customer master (Upstream)" --> ClaimsCore
  ClaimsCore -- "Domain Events" --> ClaimsWorkflow
  ClaimsWorkflow -- "Commands" --> NotificationHub
  ClaimsLedger -- "Analytics Feed" --> DataLake
  PortalExperience -- "Conformist" --> ClaimsCore
  AzureAD -- "Identity federation" --> PortalExperience

  classDef boundedContext fill:#DDD6FE,stroke:#4C1D95,stroke-width:2px,color:#1F2937;
  class Enrollment,Claims,Support boundedContext;
  classDef integration fill:#C7D2FE,stroke:#1E3A8A,color:#1E3A8A;
  class AzureAD,DataLake integration;
  classDef domain fill:#EDE9FE,stroke:#4C1D95,color:#1F2937;
  class Eligibility,PolicyBinder,ClaimsCore,ClaimsWorkflow,ClaimsLedger,PortalExperience,NotificationHub domain;
```

### Reading the context map

1. **Bounded contexts.** Enrollment, Claims Management, and Support delineate distinct domain capabilities with minimal coupling.
2. **Upstream/downstream relationships.** Enrollment publishes policy data that the Claims context consumes in a conformist manner to ensure consistency.
3. **Event-driven choreography.** Claims domain events trigger workflow orchestration and downstream notifications.
4. **Shared data products.** The Claims ledger exports analytical feeds to the enterprise data lake without leaking core schemas.
5. **Identity alignment.** Azure AD centralizes identity for the portal, enabling consistent access controls across contexts.
