package com.duminda.oauth2.resourceserver;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.duminda.oauth2.resourceserver.config.KeycloakExtension;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(KeycloakExtension.class)
@SpringBootTest(properties = {"spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI}", "keycloak-jwkSetUri=${KEYCLOAK_JWK_URI}"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("oauth resource server tests")
public class OAuthResourceServerTests {

    private static final Logger logger = LoggerFactory.getLogger(OAuthResourceServerTests.class);

    @Autowired
    private MockMvc mvc;

    private final KeycloakContainer keycloakContainer;

    public OAuthResourceServerTests(KeycloakContainer keycloakContainer) {
        this.keycloakContainer = keycloakContainer;
    }


    @Test
    @DisplayName("valid access token is accepted")
    public void validOAuthJwtShouldReturnUsername() throws Exception {

        // get access token from token endpoint
        String tokenEndpoint = keycloakContainer.getAuthServerUrl() + "/realms/calamitaid/protocol/openid-connect/token";
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("pvt-access", "6cf9e105-f5cb-477d-9b65-45b40d1df8f8");
        var map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", "calmuser");
        map.add("password", "calm123");
        var token = restTemplate.postForObject(tokenEndpoint, new HttpEntity<>(map, headers), KeycloakToken.class);
        assertThat(token).isNotNull();

        String accessToken = token.getAccessToken();

        mvc.perform(MockMvcRequestBuilders.get("/protected")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private static class KeycloakToken {
        private String accessToken;

        @JsonCreator
        KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

}
