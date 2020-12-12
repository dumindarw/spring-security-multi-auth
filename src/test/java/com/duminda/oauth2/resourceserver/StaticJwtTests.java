package com.duminda.oauth2.resourceserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("static jwt authentication")
public class StaticJwtTests {

    @Autowired
    private MockMvc mvc;

    @Value("${static-jwt.valid-token}")
    private String staticJwt;

    @Value("${static-jwt.expired-token}")
    private String expiredJwt;

    @Test
    @DisplayName("statically signed JWT is valid")
    public void validStaticJwtShouldReturnUsername() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/protected")
                .header("Authorization", "Bearer " + staticJwt))
                .andExpect(status().isOk());
                //.andExpect(content().json("{'whoami': 'staticUser'}"));
    }

    /*@Test
    @DisplayName("expired statically signed JWT is invalid")
    public void invalidStaticJwtShouldReturnUnauthorized() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/whoami")
                .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }*/

}
