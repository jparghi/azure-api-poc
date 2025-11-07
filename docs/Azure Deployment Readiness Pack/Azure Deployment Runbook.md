## 📘 Azure Deployment Runbook**

### 🧭 **Purpose**

Provide **step-by-step, environment-aware deployment instructions** for the Azure API-First Platform.
Covers: provisioning, deployment, verification, rollback, and post-deployment validation.

---

## 🧩 **1. Deployment Overview**

| Item                    | Description                                                        |
| ----------------------- | ------------------------------------------------------------------ |
| **Deployment Type**     | Infrastructure + Application (IaC + Spring Boot API + Angular SPA) |
| **Target Environments** | Dev → UAT → Prod                                                   |
| **Method**              | Terraform + Bicep + GitHub Actions (CI/CD)                         |
| **Authentication**      | Managed Identity / Service Principal                               |
| **Approvals**           | Change Control / CAB ticket #                                      |
| **Maintenance Window**  | TBD (Q4 2025 go-live)                                              |

---

## 🏗️ **2. Pre-Deployment Checklist**

| Check                   | Description                                 | Status |
| ----------------------- | ------------------------------------------- | ------ |
| 🔹 Repo Synced          | Latest main branch pulled + tagged          | ✅      |
| 🔹 Terraform State Lock | Backend set (Azure Storage + State file)    | ✅      |
| 🔹 Key Vault Secrets    | Connection strings & certs updated          | ✅      |
| 🔹 SQL Backup           | Point-in-time backup executed               | ✅      |
| 🔹 Maintenance Banner   | Portal/API under maintenance notice enabled | ⏳      |
| 🔹 CAB Approval         | Verified ticket & sign-off                  | ✅      |

---

## ⚙️ **3. Infrastructure Deployment**

### Step 1 – Login to Azure

```bash
az login --service-principal -u <clientId> -p <secret> --tenant <tenantId>
az account set --subscription "<SUBSCRIPTION_ID>"
```

### Step 2 – Terraform Init / Plan / Apply

```bash
cd infra/terraform
terraform init -backend-config="storage_account_name=tfstateprod"
terraform plan -var-file="env/dev.tfvars"
terraform apply -auto-approve -var-file="env/dev.tfvars"
```

Expected output: no errors → resources created in target RG.

### Step 3 – Verify Infra

```bash
az resource list -g rg-api-dev --output table
```

Check for: AKS, App Service, SQL, Key Vault, App Insights, APIM.

---

## 🚀 **4. Application Deployment**

### Backend (Spring Boot API)

```bash
mvn clean package -DskipTests
az webapp deploy --resource-group rg-api-dev --name api-demo --src-path target/api.jar
```

Verify:

```bash
curl https://api-demo.azurewebsites.net/actuator/health
```

→ `{"status":"UP"}`

### Frontend (Angular SPA)

```bash
ng build --configuration=production
az storage blob upload-batch \
  -s dist/frontend \
  -d '$web' \
  --account-name <storage_account_name>
```

Verify URL from Static Web App or App Service.

---

## 🔍 **5. Post-Deployment Validation**

| Test       | Command / Action             | Expected Result        |
| ---------- | ---------------------------- | ---------------------- |
| API Health | `/actuator/health`           | Status = UP            |
| Auth Flow  | MSAL login redirect          | Token acquired         |
| Database   | `SELECT COUNT(*)` users      | Data present           |
| Telemetry  | App Insights → Live Metrics  | Logs flowing           |
| APIM       | Call through gateway         | 200 OK + JWT validated |
| Cost Check | Azure Portal → Cost Analysis | Normal spend           |

---

## 🔁 **6. Rollback Procedure**

| Scenario               | Action                                              |
| ---------------------- | --------------------------------------------------- |
| Terraform Failure      | `terraform destroy -auto-approve` (Dev only)        |
| App Deployment Failure | Redeploy previous artifact from GitHub release      |
| Config Error           | Revert Key Vault version or restore App Config slot |
| Data Issue             | Restore SQL backup (Geo-restore / PITR)             |
| Auth Failure           | Disable AAD integration via `/config/azure-ad` flag |

Rollback validation: confirm previous build returns 200 OK.

---

## 🧠 **7. Monitoring & Alerting Setup**

* **App Insights Dashboards:** request rate, failures, dependency duration.
* **Azure Monitor Alerts:**

    * API Latency > 1 s for 5 min → notify SRE.
    * 5xx Errors > 20/min → trigger PagerDuty.
* **Log Analytics Queries:** Kusto queries for error trends.

---

## 🧩 **8. DR & Backup Validation**

| Resource     | Backup Type            | Verification                |
| ------------ | ---------------------- | --------------------------- |
| Azure SQL    | PITR + Geo-replication | `az sql db restore` dry-run |
| AKS          | Cluster state snapshot | Velero backup logs          |
| Key Vault    | Secret version history | Verified                    |
| Blob Storage | RA-GZRS redundancy     | Enabled                     |

---

## 🧰 **9. Automation (Pipeline Reference)**

**GitHub Actions Workflow:** `.github/workflows/deploy.yaml`

```yaml
on:
  push:
    branches: [ main ]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Terraform Apply
        run: terraform apply -auto-approve -var-file=env/${{ env.ENV }}.tfvars
      - name: Deploy Spring Boot API
        run: az webapp deploy --src-path target/api.jar ...
      - name: Deploy Angular SPA
        run: az storage blob upload-batch -s dist/frontend -d '$web'
```

---

## 📦 **10. Post-Run Documentation**

| Artifact               | Location                        | Owner  |
| ---------------------- | ------------------------------- | ------ |
| Terraform State File   | Azure Storage tfstate container | DevOps |
| Deployment Logs        | GitHub Actions Artifacts        | DevOps |
| Validation Screenshots | `/docs/deployment/validation/`  | QA     |
| Incident Notes         | `/ops/runbooks/`                | SRE    |

---

## ✅ **11. Sign-off**

| Role             | Responsibility              | Sign-off |
| ---------------- | --------------------------- | -------- |
| Cloud Architect  | Infra & Topology verified   | ☐        |
| DevOps Lead      | CI/CD execution verified    | ☐        |
| Security Officer | Secrets & policy verified   | ☐        |
| QA Manager       | Validation results approved | ☐        |

---
