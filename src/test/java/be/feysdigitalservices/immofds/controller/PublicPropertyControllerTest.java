package be.feysdigitalservices.immofds.controller;

import be.feysdigitalservices.immofds.config.SecurityConfig;
import be.feysdigitalservices.immofds.controller.pub.PublicPropertyController;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import be.feysdigitalservices.immofds.security.JwtAuthenticationFilter;
import be.feysdigitalservices.immofds.service.PropertyImageService;
import be.feysdigitalservices.immofds.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PublicPropertyController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class PublicPropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private PropertyImageService propertyImageService;

    @Test
    void searchProperties_shouldReturn200() throws Exception {
        PropertySummaryResponse summary = new PropertySummaryResponse(
                "IMM-2026-00001", "Belle maison", "Maison", "Vente", "Publié",
                new BigDecimal("350000"), 150.0, 3, 2, "Bruxelles", "Bruxelles-Capitale",
                "B", null, "2026-01-01T00:00:00");
        PageResponse<PropertySummaryResponse> pageResponse = new PageResponse<>(
                List.of(summary), 0, 12, 1, 1, true);

        when(propertyService.searchPublicProperties(any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/public/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("IMM-2026-00001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPropertyByReference_shouldReturn200() throws Exception {
        PropertyDetailResponse detail = new PropertyDetailResponse(
                "IMM-2026-00001", "Belle maison", "Description", "Maison", "Vente", "Publié",
                new BigDecimal("350000"), 150.0, 3, 2, 7, 2, 2005, "B",
                true, true, false, true, false, false,
                "Rue de la Loi", "42", "1000", "Bruxelles", "Bruxelles-Capitale",
                50.8503, 4.3517, List.of(), "2026-01-01T00:00:00", "2026-01-01T00:00:00");

        when(propertyService.getPublicPropertyByReference("IMM-2026-00001")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/public/properties/IMM-2026-00001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("IMM-2026-00001"))
                .andExpect(jsonPath("$.title").value("Belle maison"));
    }

    @Test
    void getPropertyTypes_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/public/properties/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProvinces_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/public/properties/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
