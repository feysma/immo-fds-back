package be.feysdigitalservices.immofds.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_withDefaultAdmin_shouldReturnTokens() throws Exception {
        String loginJson = """
                {
                    "email": "admin@immofds.be",
                    "password": "Admin@2026!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("admin@immofds.be"));
    }

    @Test
    void login_withWrongPassword_shouldReturn401() throws Exception {
        String loginJson = """
                {
                    "email": "admin@immofds.be",
                    "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_withoutToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/properties"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_withValidToken_shouldReturn200() throws Exception {
        String token = obtainAccessToken();

        mockMvc.perform(get("/api/v1/admin/properties")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken() throws Exception {
        String loginJson = """
                {
                    "email": "admin@immofds.be",
                    "password": "Admin@2026!"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn();

        String refreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("refreshToken").asText();

        String refreshJson = String.format("""
                {
                    "refreshToken": "%s"
                }
                """, refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    private String obtainAccessToken() throws Exception {
        String loginJson = """
                {
                    "email": "admin@immofds.be",
                    "password": "Admin@2026!"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }
}
