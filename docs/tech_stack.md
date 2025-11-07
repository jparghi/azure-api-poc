# Technology Stack Overview

## Front-End
- **Angular**: Builds the client-facing experience and integrates observability hooks for capturing user interactions.
- **Observability Tooling**: Emits telemetry and audit events from the UI to support end-to-end monitoring.

## Back-End
- **Java**: Implements RESTful services, microservices, and integration logic.
- **Azure Functions**: Delivers serverless, event-driven capabilities for background processing.
- **Docker & Azure Kubernetes Service (AKS)**: Containerizes and orchestrates microservices for scalable deployments.
- **Azure Active Directory (AD) / B2C**: Manages authentication and authorization for users and external identities.
- **Azure Key Vault**: Protects secrets, certificates, and keys used by services.
- **Observability Services**: Produces logs, metrics, and traces to ensure auditability and operational insight.

## Cross-Cutting Architecture
- **Azure App Services**: Hosts Angular front-ends and Java APIs when not containerized.
- **Azure API Management**: Provides API gateway capabilities, security enforcement, and routing.
- **Azure SQL**: Serves as the relational database backing business workflows across the stack.
- **CI/CD Pipelines**: Azure DevOps (preferred) orchestrates build, test, security scan, deploy, and monitor stages; GitHub Actions or Jenkins act as alternatives when required.
- **Azure Monitor & Application Insights**: Centralize performance monitoring, logging, and traceability.
- **DevSecOps Practices**: Align solution delivery with compliance, security, and governance requirements across all tiers.

## Preferred vs. Alternate Tooling
- **Preferred CI/CD Platform**: Azure DevOps.
- **Alternate CI/CD Options**: GitHub Actions or Jenkins when Azure DevOps is not available.

---

## 📊 Technology Stack Diagram 

```mermaid
flowchart TB

%% FRONT-END
subgraph Front-End
  ANG["Angular"]
  OBS_UI["Observability Tooling"]
  ANG --> OBS_UI
end

%% BACK-END
subgraph Back-End
  JAVA["Java Services"]
  FUNC["Azure Functions"]
  DOCKER["Docker & AKS"]
  AUTH["Azure AD / B2C"]
  VAULT["Azure Key Vault"]
  OBS_BE["Observability Services"]
  JAVA --> FUNC
  FUNC --> DOCKER
  JAVA --> AUTH
  JAVA --> VAULT
  JAVA --> OBS_BE
end

%% CROSS-CUTTING
subgraph Cross-Cutting
  APP["Azure App Services"]
  API_MGMT["Azure API Management"]
  SQL["Azure SQL"]
  CICD["CI/CD Pipelines"]
  MONITOR["Azure Monitor & App Insights"]
  DEVSECOPS["DevSecOps Practices"]
  APP --> ANG
  APP --> JAVA
  JAVA --> API_MGMT
  API_MGMT --> SQL
  CICD --> JAVA
  CICD --> ANG
  MONITOR --> OBS_BE
  MONITOR --> OBS_UI
  DEVSECOPS --> CICD
end

%% CI/CD TOOLING OPTIONS
subgraph CI/CD Tooling
  AZD["Azure DevOps (Preferred)"]
  GHA["GitHub Actions (Alt)"]
  JENKINS["Jenkins (Alt)"]
  CICD --> AZD
  CICD --> GHA
  CICD --> JENKINS
end
