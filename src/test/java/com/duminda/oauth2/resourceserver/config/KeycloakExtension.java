package com.duminda.oauth2.resourceserver.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakExtension implements BeforeAllCallback, ParameterResolver {

    Logger logger = LoggerFactory.getLogger(KeycloakExtension.class);
    KeycloakContainer keycloak;

    @Value("${keycloak-docker-image.image}")
    private String keycloakDockerImage;

    @Value("${keycloak-docker-image.version}")
    private String keycloakDockerVersion;

    @Override
    public void beforeAll(ExtensionContext context) {
        // TODO: read from application-test.yml
        this.keycloakDockerImage = "quay.io/keycloak/keycloak";
        this.keycloakDockerVersion = "11.0.2";

        String keycloakDockerImageName = keycloakDockerImage + ":" + keycloakDockerVersion;
        logger.info("Keycloak Docker Image: {}", keycloakDockerImageName);

        keycloak = new KeycloakContainer(keycloakDockerImageName).withRealmImportFile("calamitaid-realm.json");
        keycloak.start();
        System.setProperty("KEYCLOAK_JWK_URI", keycloak.getAuthServerUrl() + "/realms/calamitaid/protocol/openid-connect/certs");
        logger.info("AUTH SERVER URL: {}", keycloak.getAuthServerUrl() + "/realms/calamitaid");
        System.setProperty("KEYCLOAK_ISSUER_URI", keycloak.getAuthServerUrl() + "/realms/calamitaid");
        logger.info("KEYCLOAK_ISSUER_URI: {}", System.getProperty("KEYCLOAK_ISSUER_URI"));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == KeycloakContainer.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return keycloak;
    }
}
