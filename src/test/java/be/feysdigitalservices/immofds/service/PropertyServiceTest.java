package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.TestDataFactory;
import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.enums.PropertyStatus;
import be.feysdigitalservices.immofds.dto.request.PropertyCreateRequest;
import be.feysdigitalservices.immofds.dto.request.PropertySearchCriteria;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import be.feysdigitalservices.immofds.exception.InvalidOperationException;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.PropertyMapper;
import be.feysdigitalservices.immofds.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private ReferenceGeneratorService referenceGeneratorService;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void searchPublicProperties_shouldReturnPageResponse() {
        Property property = TestDataFactory.createProperty();
        Page<Property> page = new PageImpl<>(List.of(property));
        PropertySummaryResponse summary = new PropertySummaryResponse(
                "IMM-2026-00001", "Belle maison", "Maison", "Vente", "Publié",
                property.getPrice(), 150.0, 3, 2, "Bruxelles", "Bruxelles-Capitale", "B", null, null, null, "2026-01-01T00:00:00");

        when(propertyRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
        when(propertyMapper.toSummaryResponse(property)).thenReturn(summary);

        PropertySearchCriteria criteria = new PropertySearchCriteria(
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        PageResponse<PropertySummaryResponse> result = propertyService.searchPublicProperties(
                criteria, PageRequest.of(0, 12));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).reference()).isEqualTo("IMM-2026-00001");
    }

    @Test
    void getPublicPropertyByReference_shouldReturnDetailResponse() {
        Property property = TestDataFactory.createProperty();
        PropertyDetailResponse detail = mock(PropertyDetailResponse.class);

        when(propertyRepository.findByReferenceAndStatus("IMM-2026-00001", PropertyStatus.PUBLISHED))
                .thenReturn(Optional.of(property));
        when(propertyMapper.toDetailResponse(property)).thenReturn(detail);

        PropertyDetailResponse result = propertyService.getPublicPropertyByReference("IMM-2026-00001");

        assertThat(result).isNotNull();
    }

    @Test
    void getPublicPropertyByReference_notFound_shouldThrow() {
        when(propertyRepository.findByReferenceAndStatus("NOTFOUND", PropertyStatus.PUBLISHED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.getPublicPropertyByReference("NOTFOUND"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createProperty_shouldSetReferenceAndDraftStatus() {
        PropertyCreateRequest request = TestDataFactory.createPropertyRequest();
        Property property = TestDataFactory.createProperty();
        property.setStatus(PropertyStatus.DRAFT);
        PropertyDetailResponse detail = mock(PropertyDetailResponse.class);

        when(propertyMapper.toEntity(request)).thenReturn(property);
        when(referenceGeneratorService.generateReference()).thenReturn("IMM-2026-00001");
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyMapper.toDetailResponse(property)).thenReturn(detail);

        PropertyDetailResponse result = propertyService.createProperty(request);

        assertThat(result).isNotNull();
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void updateStatus_validTransition_shouldSucceed() {
        Property property = TestDataFactory.createProperty();
        property.setStatus(PropertyStatus.DRAFT);
        PropertyDetailResponse detail = mock(PropertyDetailResponse.class);

        when(propertyRepository.findByReference("IMM-2026-00001")).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyMapper.toDetailResponse(property)).thenReturn(detail);

        propertyService.updateStatus("IMM-2026-00001", PropertyStatus.PUBLISHED);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.PUBLISHED);
    }

    @Test
    void updateStatus_invalidTransition_shouldThrow() {
        Property property = TestDataFactory.createProperty();
        property.setStatus(PropertyStatus.SOLD);

        when(propertyRepository.findByReference("IMM-2026-00001")).thenReturn(Optional.of(property));

        assertThatThrownBy(() -> propertyService.updateStatus("IMM-2026-00001", PropertyStatus.PUBLISHED))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Transition de statut invalide");
    }

    @Test
    void deleteProperty_shouldArchive() {
        Property property = TestDataFactory.createProperty();

        when(propertyRepository.findByReference("IMM-2026-00001")).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        propertyService.deleteProperty("IMM-2026-00001");

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.ARCHIVED);
    }
}
