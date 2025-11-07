# Documentation Index

Welcome to the knowledge hub for the Azure API-first proof of concept. This README highlights the most relevant references inside the `docs/` folder so you can quickly discover architecture diagrams, roadmap plans, and Angular-specific guidance.

## 📚 How the documentation is organized

| Folder/File                                                          | Focus | When to use it |
|----------------------------------------------------------------------| --- | --- |
| [`architecture-diagrams.md`](architecture/architecture-diagrams.md) | C4-style and DDD views rendered with Mermaid. | Share the current platform topology with stakeholders or onboard new engineers to the system shape. |
| [`frontend/`](frontend/)                                             | Angular SPA deep dives and Azure integration diagrams. | Align frontend development with Azure AD, API Management, and deployment best practices. |
| [`tech_stack.md`](tech_stack.md)                                     | High-level technology choices across the stack. | Provide quick context on why Angular, Spring Boot, Azure services, and CI/CD tooling were selected. |
| [`roadmap.md`](roadmap.md)                                           | 12-month platform roadmap in timeline form. | Communicate strategic milestones and quarterly goals with leadership. |
| [`enhancement-roi.md`](enhancement-roi.md)                           | Business rationale for upcoming enhancements. | Justify investments such as Service Bus or Helm packaging with quantifiable ROI. |
| [`swagger-ui.md`](swagger-ui.md)                                     | Accessing generated API docs locally. | Enable developers and testers to browse the OpenAPI contract and try endpoints. |

## 🧭 Architecture overview
- Start with the **system context** and **container** views in [`architecture-diagrams.md`](architecture-diagrams.md) to understand how the Angular SPA, Spring Boot API, Azure AD, and API Management interact.
- Use the **bounded context** diagrams in the same document when discussing DDD boundaries or ownership with product partners.
- All diagrams are Mermaid-based so you can edit them in-line and render previews in most IDEs or documentation portals.

## 🅰️ Angular and frontend resources
- The [`frontend/Angular SPA in azure context.md`](frontend/Angular%20SPA%20in%20azure%20context.md) diagram illustrates how the SPA authenticates with Azure AD/B2C, travels through API Management, and lands on backend services.
- [`frontend/understanding_angular_spa.md`](frontend/understanding_angular_spa.md) walks through bootstrap logic, runtime configuration toggles for Azure AD, routing, and how observability hooks surface telemetry.
- Leverage these notes when coordinating with DevOps teams about Static Web Apps, App Service hosting, or MSAL configuration updates.

## 🔄 Roadmap and investment planning
- Review [`roadmap.md`](roadmap.md) for quarterly objectives across compliance, security, observability, developer experience, and cost optimization.
- Pair the roadmap with [`enhancement-roi.md`](enhancement-roi.md) to prioritize backlog items based on business value and operational impact.

## 🧪 Working with the API contract
- Follow the steps in [`swagger-ui.md`](swagger-ui.md) to run the Spring Boot backend locally and explore the Swagger UI at <http://localhost:8080/swagger-ui/index.html>.
- The document also outlines how to toggle Azure AD authentication for local development scenarios.

## 🤝 Contributing updates
- When adding new documents, link them from this README so future contributors can locate them quickly.
- Prefer Mermaid for architecture diagrams to keep version control diffs readable.
- Keep product terminology consistent with the roadmap and ROI documents to avoid mismatched vocabulary across artifacts.
