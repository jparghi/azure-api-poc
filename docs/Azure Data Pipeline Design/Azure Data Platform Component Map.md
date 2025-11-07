### 🧩 **Mermaid Diagram — Azure Data Platform Component Map**

```mermaid
flowchart LR
    %% Ingestion Layer
    subgraph ING["🟢 Ingestion Layer"]
        EH["⚡ Event Hubs<br/>Real-time ingestion"]
        DF["📦 Data Factory<br/>Batch ingestion & orchestration"]
        FN["🧩 Azure Functions<br/>Custom parsing / validation"]
    end

    %% Storage Layer
    subgraph STG["🟣 Storage Layer"]
        ADLS["🗂️ ADLS Gen2<br/>Data Lake (raw/bronze/silver/gold)"]
        DL["🧱 Delta Lake<br/>ACID transactions & versioning"]
    end

    %% Processing Layer
    subgraph PROC["🧠 Processing Layer"]
        SA["🧮 Stream Analytics<br/>SQL filtering / windowing"]
        DBX["🚀 Databricks<br/>Spark transformations"]
        DLT["⚙️ Delta Live Tables<br/>Automated ETL pipelines"]
    end

    %% Serving Layer
    subgraph SRV["🔵 Serving Layer"]
        SYN["🧭 Synapse Serverless SQL<br/>Ad-hoc queries"]
        COS["🪐 Cosmos DB<br/>Low-latency NoSQL store"]
        ACS["🔍 Cognitive Search<br/>Full-text / semantic search"]
    end

    %% Governance Layer
    subgraph GOV["🟠 Governance, Security & Monitoring"]
        PUR["📘 Purview<br/>Data catalog & lineage"]
        KV["🔐 Key Vault<br/>Secrets & key management"]
        DEF["🛡️ Defender for Cloud<br/>Threat protection & compliance"]
        MON["📊 Monitor / Log Analytics<br/>Metrics & alerts"]
    end

    %% Flows
    EH --> SA
    FN --> ADLS
    DF --> ADLS
    SA --> ADLS
    ADLS --> DBX
    DBX --> DLT
    DLT --> SYN
    DLT --> COS
    COS --> ACS

    %% Governance
    PUR --> ADLS
    PUR --> DBX
    PUR --> COS
    KV --> DBX
    KV --> SYN
    DEF --> GOV
    MON --> GOV

    style ING fill:#e9f7ef,stroke:#27ae60,stroke-width:1px
    style STG fill:#f4ecf7,stroke:#8e44ad,stroke-width:1px
    style PROC fill:#fef9e7,stroke:#f1c40f,stroke-width:1px
    style SRV fill:#ebf5fb,stroke:#2980b9,stroke-width:1px
    style GOV fill:#fff5e6,stroke:#f39c12,stroke-width:1px
```

---

### 🧠 **How to Read It**

1️⃣ **Ingestion Layer** → Event Hubs, Data Factory, and Functions bring data from the mainframe in both streaming and batch modes.  
2️⃣ **Storage Layer** → ADLS Gen2 acts as the central data lake, storing Parquet and Delta Lake versions of all datasets.  
3️⃣ **Processing Layer** → Stream Analytics filters quick signals, and Databricks transforms the large data into curated Delta tables.  
4️⃣ **Serving Layer** → Synapse, Cosmos DB, and Cognitive Search expose data via SQL, APIs, and full-text search.  
5️⃣ **Governance Layer** → Purview tracks data lineage, Key Vault secures secrets, Defender enforces compliance, and Monitor tracks uptime, lag, and cost.  

---