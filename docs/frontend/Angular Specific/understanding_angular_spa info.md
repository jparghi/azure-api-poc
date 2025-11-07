## 🧩 **1️⃣ Bootstrapping (main.ts & config fetch)**

* The app is **standalone** — no `AppModule`; everything starts in `main.ts`.
* Before `bootstrapApplication(AppComponent)`, it calls your backend endpoint:

  ```
  GET /config/azure-ad
  ```

  which returns JSON like `{ "enabled": true, "clientId": "...", "authority": "...", "scope": "..." }`.
* Depending on that flag:

    * If **Azure AD enabled** → dynamically add **MSAL providers** (`MsalService`, `MsalGuard`, `MsalInterceptor`).
    * If disabled → app runs in open mode (no token handling).

🧠 *Why:* lets you toggle AAD auth at runtime without rebuilding the SPA — perfect for POC environments.

---

## 🧭 **2️⃣ Application Shell & Routing**

* **`AppComponent`** is just the shell → header (product title) + `<router-outlet>`.
* **`app.routes.ts`** defines your routes:

  ```ts
  [
    { path: '', component: DashboardComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: '' }
  ]
  ```
* Everything visible renders inside `<router-outlet>`.

---

## 🔐 **3️⃣ Authentication Guard (`authGuard`)**

* Uses the injected `AZURE_AD_CONFIG` token to see if AAD is active.
* If **disabled** → allows navigation.
* If **enabled** → delegates to `MsalGuard` → triggers redirect login flow.
* This means your SPA works **both secured and unsecured** based on backend config.

---

## 📊 **4️⃣ Dashboard Feature Component**

* **`DashboardComponent`**

    * Imports `CommonModule`, `DatePipe`, maybe `HttpClientModule`.
    * On `ngOnInit`:

        * Calls `ApiService.getUsers()` → `/v1/users`
        * Calls `ApiService.getAudit()` → `/v1/audit`
        * Calls `ApiService.getHealth()` → `/actuator/health`
    * Displays results in three cards:

        * **Service Health** (OK / DOWN + timestamp)
        * **Users Table**
        * **Audit Log Table**
* **Template:**
  Uses `*ngFor`, async pipes, and loading placeholders for good UX.

---

## ⚙️ **5️⃣ Data Access Layer (`ApiService` & interceptor)**

* `ApiService`

    * Builds base URL from `environment.apiBaseUrl`.
    * Exposes observables or methods returning `Observable<User[]>`, etc.
* `apiInterceptor`

    * Appends header:

      ```
      X-APIM-Base-Url: localStorage.getItem('apimBaseUrl')
      ```
    * Used so APIM routing can rewrite calls if needed.

🧠 *Why:* isolates all HTTP logic and keeps components clean.

---

## 🎨 **6️⃣ Styling & Layout**

* **Global styles** in `src/styles.css` define:

    * Fonts, colors, spacing, card shadows, table borders.
* **Dashboard CSS** only handles layout grid + table typography.
* No heavy UI libs (Material/Bootstrap) → fast, lightweight look.

---

## ⚙️ **7️⃣ Environment Configuration**

* `src/environments/environment.ts` and `environment.prod.ts`

  ```
  export const environment = {
    apiBaseUrl: '/api',
    production: false
  };
  ```
* When building for prod, CI/CD can override this to hit your live API Gateway.

---

## 🔗 **8️⃣ How It Fits Into the Azure POC**

| Layer                    | Role                                  |
| ------------------------ | ------------------------------------- |
| Angular SPA              | Front-end dashboard (runs in browser) |
| Azure AD / MSAL          | Auth (optional)                       |
| Azure APIM               | Gateway for `/api/*`                  |
| Spring Boot API          | Backend microservice                  |
| Azure SQL / App Insights | Data + metrics                        |

Workflow:

```
User → Angular SPA → (MSAL) → Azure AD → JWT → API via APIM → Spring Boot → SQL
```

---
