package be.feysdigitalservices.immofds.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PropertyIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicSearch_shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/public/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void createAndSearchProperty_fullLifecycle() throws Exception {
        String token = obtainAccessToken();

        // Create property
        String createJson = """
                {
                    "title": "Appartement lumineux",
                    "description": "Bel appartement au centre",
                    "propertyType": "APARTMENT",
                    "transactionType": "SALE",
                    "price": 250000,
                    "surface": 85.5,
                    "bedrooms": 2,
                    "bathrooms": 1,
                    "rooms": 4,
                    "floors": 1,
                    "constructionYear": 2010,
                    "energyRating": "B",
                    "garden": false,
                    "garage": false,
                    "terrace": true,
                    "basement": false,
                    "elevator": true,
                    "furnished": false,
                    "street": "Avenue Louise",
                    "number": "100",
                    "postalCode": "1050",
                    "city": "Ixelles",
                    "province": "BRUXELLES_CAPITALE"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/admin/properties")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").exists())
                .andExpect(jsonPath("$.status").value("Brouillon"))
                .andReturn();

        String reference = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("reference").asText();

        // Admin can see the property
        mockMvc.perform(get("/api/v1/admin/properties/" + reference)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Appartement lumineux"));

        // Public cannot see DRAFT property
        mockMvc.perform(get("/api/v1/public/properties/" + reference))
                .andExpect(status().isNotFound());

        // Publish the property
        String publishJson = """
                {
                    "status": "PUBLISHED"
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/properties/" + reference + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publishJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Publié"));

        // Public can now see the property
        mockMvc.perform(get("/api/v1/public/properties/" + reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Appartement lumineux"));

        // Search should find it
        mockMvc.perform(get("/api/v1/public/properties")
                        .param("propertyType", "APARTMENT")
                        .param("transactionType", "SALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void contactForm_fullLifecycle() throws Exception {
        String token = obtainAccessToken();

        // Submit a general contact
        String contactJson = """
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "test@example.com",
                    "phone": "+32 470 00 00 00",
                    "message": "Ceci est un test."
                }
                """;

        mockMvc.perform(post("/api/v1/public/contacts/general")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andExpect(status().isCreated());

        // Admin lists contacts
        mockMvc.perform(get("/api/v1/admin/contacts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").isNumber());
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
