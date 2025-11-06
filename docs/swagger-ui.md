# Swagger UI Access

The backend includes Springdoc's Swagger UI starter (`springdoc-openapi-starter-webmvc-ui`), which automatically exposes the API documentation when the application is running.

## Local Access

1. Start the backend from the `backend/` directory:
   ```bash
   mvn spring-boot:run
   ```
2. Open your browser to <http://localhost:8080/swagger-ui/index.html> to view the interactive documentation.

## Authentication Considerations

* Azure AD security permits anonymous access to the OpenAPI description (`/v3/api-docs/**`) and Swagger UI assets (`/swagger-ui/**`).
* Azure AD is enabled by default. For local development you can disable it by setting `AZURE_ACTIVEDIRECTORY_ENABLED=false` when starting the backend, for example:
  ```bash
  mvn spring-boot:run -DAZURE_ACTIVEDIRECTORY_ENABLED=false
  ```
  With Azure AD disabled, every endpoint—including Swagger UI—is publicly accessible while you iterate locally.
