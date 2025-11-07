
### 🧭 **Mermaid Diagram — End-to-End Azure Data Flow (Multi-Source Design)**

```mermaid
flowchart LR
    %% === SOURCE LAYER ===
    subgraph SRC["🟢 Data Sources"]
        MF["🏛️ Mainframe Systems<br/>(VSAM • COBOL • Flat Files)"]
        DB["🗄️ RDBMS / On-Prem SQL / Oracle"]
        API["🌐 Partner APIs / Webhooks<br/>(FHIR / REST)"]
        EVT["⚡ Real-time Events<br/>(Claim Updates / Status Changes)"]
        IOT["📡 IoT Devices / Health Sensors"]
        DOC["🗂️ Documents / Scans<br/>(PDF / XML / HL7)"]
        EXT["📰 Third-Party Feeds<br/>(Provider Registry / Demographics)"]
    end

    %% === INGESTION LAYER ===
    subgraph ING["🟣 Ingestion & Pre-Processing"]
        ADF["📦 Azure Data Factory<br/>Batch Orchestration"]
        EH["⚡ Event Hubs<br/>Streaming Ingestion"]
        FN["🧩 Azure Functions<br/>Parsing • Validation • PII Masking"]
        SA["🧮 Stream Analytics<br/>Filtering • Aggregation"]
    end

    %% === STORAGE LAYER ===
    subgraph STG["🟡 Storage Layer (Data Lakehouse)"]
        RAW["🗂️ ADLS Gen2 /raw<br/>Landing Zone (Parquet / JSON)"]
        BRZ["🥉 Delta Bronze<br/>Raw + Metadata"]
        SIL["🥈 Delta Silver<br/>Cleansed + Validated"]
        GLD["🥇 Delta Gold<br/>Curated Business Views"]
    end

    %% === PROCESSING LAYER ===
    subgraph PROC["🧠 Processing & Transformation"]
        DBX["🚀 Databricks<br/>ETL / Machine Learning / Delta Lake"]
        DLT["⚙️ Delta Live Tables<br/>Automated ETL Pipelines"]
    end

    %% === SERVING LAYER ===
    subgraph SRV["🔵 Serving & Consumption"]
        SYN["🧭 Synapse Serverless SQL<br/>Ad-hoc Queries / Reporting"]
        COS["🪐 Cosmos DB<br/>API Serving Layer"]
        ACS["🔍 Cognitive Search<br/>Full-text & Semantic Search"]
        PBI["📊 Power BI / API Layer<br/>Dashboards & Insights"]
    end

    %% === GOVERNANCE LAYER ===
    subgraph GOV["🟠 Governance • Security • Monitoring"]
        PUR["📘 Microsoft Purview<br/>Catalog / Lineage / PII Policy"]
        KV["🔐 Key Vault<br/>Secrets / Keys / Certificates"]
        DEF["🛡️ Defender for Cloud<br/>Threat & Compliance Management"]
        MON["📈 Azure Monitor / Log Analytics<br/>Lag • Cost • Query Latency"]
    end

    %% === DATA FLOW ===
    MF -->|Batch Extracts| ADF
    DB -->|CDC / Snapshots| ADF
    API -->|Webhooks / Event Grid| EH
    EVT -->|Stream Messages| EH
    IOT -->|Telemetry| EH
    DOC -->|Blob Upload| FN
    EXT -->|External Feed Connectors| ADF

    ADF --> RAW
    EH --> SA
    SA --> FN
    FN --> RAW

    RAW --> DBX
    DBX --> BRZ
    DBX --> SIL
    DBX --> GLD
    DBX --> DLT

    DLT --> SYN
    DLT --> COS
    COS --> ACS
    SYN --> PBI
    COS --> PBI

    %% === GOVERNANCE FLOWS ===
    PUR -.-> RAW
    PUR -.-> SIL
    PUR -.-> GLD
    PUR -.-> COS
    KV -.-> DBX
    KV -.-> SYN
    DEF -.-> GOV
    MON -.-> EH
    MON -.-> DBX
    MON -.-> COS
    MON -.-> PBI

    %% === STYLES ===
    style SRC fill:#e9f7ef,stroke:#27ae60,stroke-width:1px
    style ING fill:#f4ecf7,stroke:#8e44ad,stroke-width:1px
    style STG fill:#fef9e7,stroke:#f1c40f,stroke-width:1px
    style PROC fill:#fdf2e9,stroke:#e67e22,stroke-width:1px
    style SRV fill:#ebf5fb,stroke:#2980b9,stroke-width:1px
    style GOV fill:#fff5e6,stroke:#f39c12,stroke-width:1px
```

---

### 🧠 **Step-by-Step Data Movement**

| Step                              | Stage                                        | Description                                                                     | Azure Services                        |
| --------------------------------- | -------------------------------------------- | ------------------------------------------------------------------------------- | ------------------------------------- |
| **1️⃣ Ingest Data**               | From Mainframe, APIs, IoT, and Events        | Batch → ADF, Streaming → Event Hubs, Webhooks → Event Grid                      | Data Factory, Event Hubs, Event Grid  |
| **2️⃣ Pre-Process & Validate**    | Apply filters, masking, and parsing          | Stream Analytics for basic SQL filters; Functions for custom logic              | Stream Analytics, Functions           |
| **3️⃣ Land Raw Data**             | Store unprocessed data in Data Lake          | All data lands in ADLS Gen2 `/raw` with partitions by date                      | ADLS Gen2                             |
| **4️⃣ Process & Transform**       | ETL into structured, governed datasets       | Databricks cleans, deduplicates, joins → creates Delta Lake layers              | Databricks, Delta Live Tables         |
| **5️⃣ Serve for Analytics**       | Query-ready gold datasets                    | Synapse (ad-hoc SQL), Cosmos DB (API apps), Cognitive Search (text)             | Synapse, Cosmos DB, Cognitive Search  |
| **6️⃣ Deliver Business Insights** | Dashboards and KPIs                          | Power BI connected to Synapse/Cosmos for visual analytics                       | Power BI                              |
| **7️⃣ Govern & Monitor**          | Track lineage, secrets, compliance, and cost | Purview (catalog), Key Vault (secrets), Defender (compliance), Monitor (alerts) | Purview, Key Vault, Defender, Monitor |

---

### ⚡ **Performance Checkpoints (SLOs)**

| Metric                                 | Target            | Monitored In               |
| -------------------------------------- | ----------------- | -------------------------- |
| **Streaming Lag**                      | < 60 seconds      | Azure Monitor → Event Hubs |
| **Batch Load Window**                  | < 2 hours nightly | ADF run history            |
| **Delta Compaction Cadence**           | Hourly            | Databricks Job Cluster     |
| **p95 Query Latency (Synapse/Cosmos)** | < 1 second        | Log Analytics              |
| **Index Freshness (Cognitive Search)** | < 5 minutes       | Indexer Health             |
| **Lineage Visibility**                 | 100% traceable    | Purview scans              |
| **Key Rotation**                       | Every 90 days     | Key Vault audit logs       |

---

### 🧩 **Why This Design Works**

✅ **Modular** — new data sources can plug in easily.
✅ **Scalable** — supports TB/day ingestion and real-time events.
✅ **Secure** — full encryption, masking, private endpoints, compliance guardrails.
✅ **Unified Storage** — ADLS Gen2 + Delta Lake ensures reliability and ACID transactions.
✅ **Flexible Consumption** — both SQL (Synapse) and API (Cosmos) layers.
✅ **Governed** — lineage, auditability, and security built-in.

---
