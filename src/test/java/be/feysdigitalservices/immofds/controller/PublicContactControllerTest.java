package be.feysdigitalservices.immofds.controller;

import be.feysdigitalservices.immofds.controller.pub.PublicContactController;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.service.ContactRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactRequestService contactRequestService;

    @Test
    void submitGeneralContact_validRequest_shouldReturn201() throws Exception {
        when(contactRequestService.createGeneralContact(any())).thenReturn(mock(ContactRequestResponse.class));

        String json = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "email": "jean@example.com",
                    "phone": "+32 470 12 34 56",
                    "message": "Bonjour, je souhaite des informations."
                }
                """;

        mockMvc.perform(post("/api/v1/public/contacts/general")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void submitGeneralContact_missingFields_shouldReturn400() throws Exception {
        String json = """
                {
                    "firstName": "",
                    "email": "invalid"
                }
                """;

        mockMvc.perform(post("/api/v1/public/contacts/general")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitSellYourHome_validRequest_shouldReturn201() throws Exception {
        when(contactRequestService.createSellYourHome(any())).thenReturn(mock(ContactRequestResponse.class));

        String json = """
                {
                    "firstName": "Marie",
                    "lastName": "Martin",
                    "email": "marie@example.com",
                    "propertyAddress": "Rue Haute 10, 1000 Bruxelles",
                    "propertyType": "HOUSE",
                    "estimatedPrice": 450000
                }
                """;

        mockMvc.perform(post("/api/v1/public/contacts/sell-your-home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void submitVisitRequest_validRequest_shouldReturn201() throws Exception {
        when(contactRequestService.createVisitRequest(any())).thenReturn(mock(ContactRequestResponse.class));

        String json = """
                {
                    "firstName": "Pierre",
                    "lastName": "Leroy",
                    "email": "pierre@example.com",
                    "propertyReference": "IMM-2026-00001",
                    "message": "Je souhaite visiter ce bien."
                }
                """;

        mockMvc.perform(post("/api/v1/public/contacts/visit-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}
