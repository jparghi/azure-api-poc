package com.example.api.web;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class AzureAdConfigController {

    private final Environment environment;

    public AzureAdConfigController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/azure-ad")
    public ResponseEntity<AzureAdConfigResponse> getAzureAdConfiguration() {
        boolean aadEnabled = environment.getProperty("azure.activedirectory.enabled", Boolean.class, true);
        String clientId = environment.getProperty("azure.activedirectory.credential.client-id", "");
        String tenantId = environment.getProperty("azure.activedirectory.profile.tenant-id", "");
        String appIdUri = environment.getProperty("azure.activedirectory.app-id-uri", "");

        boolean hasClientId = isUsableValue(clientId) && !"client-id".equalsIgnoreCase(clientId);
        boolean hasTenantId = isUsableValue(tenantId) && !"tenant-id".equalsIgnoreCase(tenantId);
        boolean hasAppIdUri = isUsableValue(appIdUri);

        String authority = buildAuthority(hasTenantId ? tenantId : "common");
        String apiScope = buildApiScope(appIdUri);

        boolean enabledForFrontend = aadEnabled && hasClientId && hasTenantId && hasAppIdUri && StringUtils.hasText(apiScope);

        AzureAdConfigResponse response = new AzureAdConfigResponse(
                enabledForFrontend,
                clientId,
                authority,
                apiScope
        );
        return ResponseEntity.ok(response);
    }

    private static boolean isUsableValue(String value) {
        return StringUtils.hasText(value);
    }

    private static String buildAuthority(String tenantId) {
        return "https://login.microsoftonline.com/" + tenantId;
    }

    private static String buildApiScope(String appIdUri) {
        if (!StringUtils.hasText(appIdUri)) {
            return "";
        }
        return appIdUri.endsWith("/.default") ? appIdUri : appIdUri + "/.default";
    }

    public record AzureAdConfigResponse(boolean enabled, String clientId, String authority, String apiScope) {
    }
}

