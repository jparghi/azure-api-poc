## 📘 **Azure Data Pipeline Keyword Reference (Explained)**

| Keyword / Service                                 | What It Is                                                                      | Purpose in Our Pipeline                                                                                   |
| ------------------------------------------------- | ------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **Mainframe Data (VSAM / MQ / Flat Files)**       | Legacy data source (COBOL files, message queues)                                | Original source of health insurance data; exported nightly or streamed to Azure.                          |
| **Event Hubs**                                    | Azure’s real-time **data ingestion** service (like Kafka).                      | Captures and streams live data feeds (e.g., claim updates, member events) into Azure.                     |
| **Stream Analytics**                              | Real-time **data processing engine** using SQL-like syntax.                     | Applies filters, joins, and rules to Event Hub messages before saving to storage.                         |
| **Azure Functions**                               | Serverless code execution (small functions).                                    | Handles custom logic like parsing mainframe data, PII masking, or HL7 healthcare data transformation.     |
| **ADLS (Azure Data Lake Storage)**                | Scalable, secure **object storage** for raw and processed data.                 | Stores all data zones: `/raw`, `/bronze`, `/silver`, `/gold`. Foundation of the pipeline.                 |
| **ADLS Gen2**                                     | Enhanced version of Data Lake with **hierarchical namespace** (folders + ACLs). | Enables fine-grained access control and compatibility with big-data tools like Databricks and Synapse.    |
| **Databricks**                                    | Distributed **data engineering platform** built on Apache Spark.                | Transforms large datasets, performs cleansing, joins, and creates **Delta Lake** tables for analytics.    |
| **Delta Lake**                                    | Storage format on top of Parquet that supports **ACID transactions**.           | Ensures data consistency and versioning across your `/bronze`, `/silver`, `/gold` data layers.            |
| **Bronze / Silver / Gold Layers**                 | Logical stages of data refinement.                                              | **Bronze:** raw ingested → **Silver:** cleaned & standardized → **Gold:** business-ready curated views.   |
| **Synapse Analytics (Serverless SQL)**            | Azure’s **on-demand query service** for big data (no dedicated cluster).        | Lets analysts query data in ADLS directly using SQL, paying per TB scanned.                               |
| **Cosmos DB**                                     | Globally distributed, low-latency **NoSQL database**.                           | Hosts processed data (e.g., claims, members) for fast API access and feeds Cognitive Search for indexing. |
| **Cognitive Search (Azure AI Search)**            | AI-powered full-text and semantic **search engine**.                            | Builds searchable indexes from Cosmos DB data, enabling keyword search (e.g., “find member policy”).      |
| **Purview (Microsoft Purview)**                   | Azure’s **data catalog and governance** service.                                | Tracks data lineage (from mainframe → ADLS → Databricks → Cosmos), classifies PII, and audits compliance. |
| **Azure Data Factory (ADF)**                      | Orchestrates **batch data movement and ETL workflows**.                         | Moves nightly mainframe extracts to ADLS and triggers Databricks jobs.                                    |
| **Azure Synapse Pipelines**                       | Data Factory’s orchestration engine inside Synapse.                             | Used if you consolidate pipeline orchestration within the Synapse workspace.                              |
| **Azure Key Vault**                               | Secure secrets and keys storage.                                                | Manages credentials for Databricks, Synapse, and Cosmos DB securely (no passwords in code).               |
| **Defender for Cloud**                            | Azure’s continuous **security assessment** service.                             | Monitors data assets for vulnerabilities and ensures compliance (HIPAA, SOC2).                            |
| **Azure Monitor / Log Analytics**                 | Centralized **monitoring & alerting** service.                                  | Tracks ingestion lag, job failures, and query latency metrics for all pipeline components.                |
| **Synapse Serverless SQL**                        | Query engine that reads directly from ADLS using `OPENROWSET`.                  | Enables **ad-hoc data exploration** without spinning up expensive compute clusters.                       |
| **Cognitive Services / Cognitive Search Indexer** | Azure AI models for text analytics and search.                                  | Used to **extract insights** and keep search indexes fresh (via Cosmos change feed).                      |
| **Cosmos Change Feed**                            | Event stream of all changes in a Cosmos DB container.                           | Feeds **Cognitive Search indexers** for near-real-time search updates.                                    |
| **Data Contracts**                                | Schema & data format definitions agreed between producer/consumer.              | Ensures consistent structure (e.g., Parquet columns: id, source, body, etc.).                             |
| **Parquet Format**                                | Columnar, compressed file format for analytics workloads.                       | Efficient for large data storage and fast queries (used in ADLS).                                         |
| **Z-ORDER / OPTIMIZE (Databricks)**               | Delta Lake optimization techniques.                                             | Improve read performance by clustering frequently filtered columns (e.g., `source`, `topic`).             |
| **Azure Synapse Dedicated SQL Pool**              | High-performance, scalable **data warehouse**.                                  | Optional for aggregated data marts when low-latency queries are needed.                                   |
| **Azure Machine Learning (AML)**                  | Azure’s **ML training and inference** platform.                                 | (Optional future phase) For fraud detection, risk scoring, or claims anomaly detection.                   |
| **Azure Fabric Lakehouse**                        | Unified data + analytics environment integrating ADLS, Power BI, and AI.        | (Optional future upgrade) Replaces Databricks/Synapse with one governed, real-time workspace.             |
| **Delta Live Tables**                             | Databricks framework for **automated ETL pipelines** using Delta Lake.          | Simplifies building and maintaining your bronze/silver/gold transformations.                              |
| **Azure Front Door**                              | Global, secure **entry point for web apps and APIs**.                           | Provides routing, caching, and security for data-serving APIs (Cosmos or Synapse).                        |
| **HIPAA / PHIPA Compliance**                      | Health data protection laws (US/Canada).                                        | Drives encryption, auditing, PII masking, and zero-trust architecture decisions.                          |

---

## 🧭 **How These Fit Together (Quick Summary)**

| Layer                     | Key Services                                    | Function                                                        |
| ------------------------- | ----------------------------------------------- | --------------------------------------------------------------- |
| **Ingestion**             | Event Hubs, Data Factory, Functions             | Bring mainframe data into Azure (real-time + batch).            |
| **Storage**               | ADLS Gen2, Delta Lake                           | Persist and organize data for durability and analytics.         |
| **Processing**            | Stream Analytics, Databricks                    | Clean, enrich, and transform raw data into structured datasets. |
| **Serving**               | Synapse Serverless, Cosmos DB, Cognitive Search | Make data queryable via SQL, API, and search.                   |
| **Governance & Security** | Purview, Key Vault, Defender, Monitor           | Enforce compliance, monitor performance, and manage keys.       |

---

## 💡 **How to Use This Table**

* Treat this as your **cheat sheet** when reading or explaining any Azure architecture.
* Whenever a new service name appears in future docs, check here to understand its **role + purpose + interaction**.
* You can even convert this into a **“tooltips glossary”** inside your Confluence or CodeX doc viewer.

---