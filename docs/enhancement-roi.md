# Enhancement Rationale and ROI

## Add Service Bus for Async Events
**Why it's needed:**
- Decouples write operations from downstream consumers, enabling audit, analytics, and integration teams to react to user lifecycle changes without coupling to the REST API.
- Provides durable buffering to absorb bursts in traffic and protects the core API from back-pressure created by slow consumers.
- Aligns the solution with Azure-native messaging patterns that are easier to operate in regulated environments.

**Return on investment:**
- Shortens the time-to-market for new features that need user change notifications by reusing the same queue instead of building ad-hoc integrations.
- Reduces production incidents caused by tightly coupled synchronous integrations, translating into fewer on-call escalations and lower support costs.
- Enables near-real-time telemetry that feeds adoption dashboards and compliance evidence, improving stakeholder visibility.

## Add Helm Chart for Manifests
**Why it's needed:**
- Consolidates numerous Kubernetes YAML manifests into a reusable package that captures configuration as values and enforces consistency across environments.
- Simplifies GitOps automation by providing a versioned artifact that can be promoted between development, staging, and production without manual edits.
- Makes it easier for platform teams to extend the deployment with additional components (e.g., HPA, PodDisruptionBudgets) through templating.

**Return on investment:**
- Cuts the time required to spin up new environments by automating manifest rendering, leading to faster experimentation and demos.
- Reduces configuration drift and associated outages, limiting the amount of rework during audits or troubleshooting sessions.
- Improves collaboration between development and platform teams because settings are parameterized and documented in a single chart.

## Integrate APIM for Rate Limiting
**Why it's needed:**
- Enforces fair usage controls at the gateway layer, protecting the backend from abusive or misconfigured clients.
- Leverages Azure API Management policies so throttling rules can be tuned without redeploying application code.
- Provides standardized rate-limit headers that clients can use to implement graceful degradation strategies.

**Return on investment:**
- Prevents costly scale-out events by smoothing traffic spikes, optimizing cloud spend.
- Enhances API productization by enabling differentiated tiers (e.g., free vs. premium) that monetize higher limits.
- Improves user satisfaction by exposing transparent quota information, reducing support tickets about throttling behaviour.
