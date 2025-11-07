Perfect 👏 — here’s your **Mermaid diagram** showing the full **Angular SPA + Azure API + Authentication flow**, exactly matching your POC architecture.

---

### 🧩 **Mermaid Diagram: Angular SPA in Azure Context**

```mermaid
flowchart LR
    subgraph UserBrowser["🌐 User Browser"]
        A1["Angular SPA<br/>(index.html & JS)"]
    end

    subgraph AzureFront["☁️ Azure Frontend Layer"]
        SWA["Azure Static Web App / App Service"]
        AD["Azure Active Directory / B2C<br/>(MSAL Login)"]
    end

    subgraph AzureGateway["🧭 Azure API Gateway"]
        APIM["Azure API Management<br/>(Gateway + Policies)"]
    end

    subgraph Backend["⚙️ Backend Services"]
        API["Spring Boot REST API<br/>(Java 17 + Spring Boot)"]
        SQL["Azure SQL Database"]
        INSIGHTS["Azure Application Insights<br/>(Telemetry + Logs)"]
    end

    UserBrowser -->|Loads SPA Assets| SWA
    SWA -->|Fetch /config/azure-ad| API
    A1 -->|If enabled → Login via MSAL| AD
    A1 -->|Calls API with JWT Token| APIM
    APIM -->|Validates Token & Routes Request| API
    API -->|Queries & Writes Data| SQL
    API -->|Sends Telemetry| INSIGHTS
    A1 -->|Displays Users, Health & Audit Logs| UserBrowser

```

---

### 🧠 **How to Read This**

1️⃣ User opens your **Angular SPA** (hosted on Static Web Apps or App Service).
2️⃣ SPA fetches `/config/azure-ad` from backend to see if AAD is enabled.
3️⃣ If yes → SPA uses **MSAL** to redirect user to **Azure AD** for sign-in.
4️⃣ Upon success → gets **JWT token** for API access.
5️⃣ SPA calls your **Spring Boot API** through **Azure API Management**, passing the JWT.
6️⃣ **APIM** validates and routes the request.
7️⃣ **Spring Boot API** accesses **Azure SQL** and sends **logs/metrics** to **Application Insights**.
8️⃣ SPA displays updated data (users, audits, health status).

---

Would you like me to generate a **second version of this diagram** that’s **animated** (Framer Motion + Mermaid style, for CodeX demo)**—so each step fades in sequentially during your presentation?
