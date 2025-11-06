# Blue Cross – Health Insurance DDD Architecture

## 🧩 Domain-Driven Design Overview

This system models Blue Cross California’s health insurance operations using Domain-Driven Design (DDD).  
Each bounded context aligns with a business capability and owns its data, logic, and deployment pipeline.

**Bounded Contexts**
- MemberContext — enrollment, demographics, coverage.
- ProviderContext — doctor & hospital contracts.
- EligibilityContext — coverage validation.
- ClaimsContext — adjudication and benefit calculation.
- PaymentContext — disbursement & EOB creation.
- AuditContext — compliance trail.
- NotificationContext — alerts & communications.

## 🧭 Context Map

```mermaid
graph LR
    %%{init: {'theme': 'neutral'}}%%
    subgraph MemberContext
    M1[Member Service]
    end

    subgraph ProviderContext
    P1[Provider Directory]
    end

    subgraph EligibilityContext
    E1[Eligibility Checker]
    end

    subgraph ClaimsContext
    C1[Claim Intake]
    C2[Adjudication Engine]
    end

    subgraph PaymentContext
    PAY[Payment Processor]
    end

    subgraph AuditContext
    A1[Audit Trail]
    end

    subgraph NotificationContext
    N1[Notification Service]
    end

    subgraph LegacySystems
    L1[Legacy Eligibility DB]
    end

    E1 -- ACL Adapter --> L1
    C1 --> E1
    C1 --> P1
    C2 --> PAY
    PAY --> A1
    PAY --> N1
    C2 --> A1
    C2 --> N1
```

## ⚙️ Event Flow

```mermaid
sequenceDiagram
    %%{init: {'theme': 'neutral'}}%%
    participant Member as MemberContext
    participant Claims as ClaimsContext
    participant Eligibility as EligibilityContext
    participant Provider as ProviderContext
    participant Payment as PaymentContext
    participant Audit as AuditContext
    participant Notification as NotificationContext

    Member->>Claims: ClaimSubmittedEvent
    Claims->>Eligibility: ValidateEligibility()
    Eligibility-->>Claims: EligibilityValidatedEvent
    Claims->>Provider: VerifyContract()
    Provider-->>Claims: ProviderValidatedEvent
    Claims->>Claims: AdjudicateClaim()
    Claims-->>Payment: ClaimApprovedEvent
    Payment->>Payment: CalculateAndDisburse()
    Payment-->>Notification: PaymentIssuedEvent
    Payment-->>Audit: LogAuditTrail()
    Notification-->>Member: EOBGeneratedNotification
```
