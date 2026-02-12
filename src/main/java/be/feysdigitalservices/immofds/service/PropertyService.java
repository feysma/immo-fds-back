package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.enums.PropertyStatus;
import be.feysdigitalservices.immofds.dto.request.PropertyCreateRequest;
import be.feysdigitalservices.immofds.dto.request.PropertySearchCriteria;
import be.feysdigitalservices.immofds.dto.request.PropertyUpdateRequest;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import be.feysdigitalservices.immofds.exception.InvalidOperationException;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.PropertyMapper;
import be.feysdigitalservices.immofds.repository.PropertyRepository;
import be.feysdigitalservices.immofds.specification.PropertySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final ReferenceGeneratorService referenceGeneratorService;

    public PropertyService(PropertyRepository propertyRepository, PropertyMapper propertyMapper,
                           ReferenceGeneratorService referenceGeneratorService) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.referenceGeneratorService = referenceGeneratorService;
    }

    public PageResponse<PropertySummaryResponse> searchPublicProperties(PropertySearchCriteria criteria, Pageable pageable) {
        Specification<Property> spec = buildPublicSpecification(criteria);
        Page<Property> page = propertyRepository.findAll(spec, pageable);
        return toPageResponse(page.map(propertyMapper::toSummaryResponse));
    }

    public PropertyDetailResponse getPublicPropertyByReference(String reference) {
        Property property = propertyRepository.findByReferenceAndStatus(reference, PropertyStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Bien", "référence", reference));
        return propertyMapper.toDetailResponse(property);
    }

    public PageResponse<PropertySummaryResponse> searchAdminProperties(PropertySearchCriteria criteria, Pageable pageable) {
        Specification<Property> spec = buildAdminSpecification(criteria);
        Page<Property> page = propertyRepository.findAll(spec, pageable);
        return toPageResponse(page.map(propertyMapper::toSummaryResponse));
    }

    public PropertyDetailResponse getAdminPropertyByReference(String reference) {
        Property property = findByReference(reference);
        return propertyMapper.toDetailResponse(property);
    }

    @Transactional
    public PropertyDetailResponse createProperty(PropertyCreateRequest request) {
        Property property = propertyMapper.toEntity(request);
        property.setReference(referenceGeneratorService.generateReference());
        property.setStatus(PropertyStatus.DRAFT);
        Property saved = propertyRepository.save(property);
        return propertyMapper.toDetailResponse(saved);
    }

    @Transactional
    public PropertyDetailResponse updateProperty(String reference, PropertyUpdateRequest request) {
        Property property = findByReference(reference);
        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setPropertyType(request.propertyType());
        property.setTransactionType(request.transactionType());
        property.setPrice(request.price());
        property.setSurface(request.surface());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setRooms(request.rooms());
        property.setFloors(request.floors());
        property.setConstructionYear(request.constructionYear());
        property.setEnergyRating(request.energyRating());
        property.setGarden(request.garden());
        property.setGarage(request.garage());
        property.setTerrace(request.terrace());
        property.setBasement(request.basement());
        property.setElevator(request.elevator());
        property.setFurnished(request.furnished());
        property.setStreet(request.street());
        property.setNumber(request.number());
        property.setPostalCode(request.postalCode());
        property.setCity(request.city());
        property.setProvince(request.province());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
        Property saved = propertyRepository.save(property);
        return propertyMapper.toDetailResponse(saved);
    }

    @Transactional
    public PropertyDetailResponse updateStatus(String reference, PropertyStatus newStatus) {
        Property property = findByReference(reference);
        validateStatusTransition(property.getStatus(), newStatus);
        property.setStatus(newStatus);
        Property saved = propertyRepository.save(property);
        return propertyMapper.toDetailResponse(saved);
    }

    @Transactional
    public void deleteProperty(String reference) {
        Property property = findByReference(reference);
        property.setStatus(PropertyStatus.ARCHIVED);
        propertyRepository.save(property);
    }

    public Property findByReference(String reference) {
        return propertyRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Bien", "référence", reference));
    }

    private Specification<Property> buildPublicSpecification(PropertySearchCriteria criteria) {
        return Specification.where(PropertySpecification.hasStatus(PropertyStatus.PUBLISHED))
                .and(PropertySpecification.hasPropertyType(criteria.propertyType()))
                .and(PropertySpecification.hasTransactionType(criteria.transactionType()))
                .and(PropertySpecification.hasProvince(criteria.province()))
                .and(PropertySpecification.hasCityLike(criteria.city()))
                .and(PropertySpecification.hasPriceGreaterThanOrEqual(criteria.minPrice()))
                .and(PropertySpecification.hasPriceLessThanOrEqual(criteria.maxPrice()))
                .and(PropertySpecification.hasSurfaceGreaterThanOrEqual(criteria.minSurface()))
                .and(PropertySpecification.hasSurfaceLessThanOrEqual(criteria.maxSurface()))
                .and(PropertySpecification.hasMinBedrooms(criteria.minBedrooms()))
                .and(PropertySpecification.hasEnergyRating(criteria.energyRating()))
                .and(PropertySpecification.hasGarden(criteria.garden()))
                .and(PropertySpecification.hasGarage(criteria.garage()))
                .and(PropertySpecification.hasTerrace(criteria.terrace()))
                .and(PropertySpecification.hasBasement(criteria.basement()))
                .and(PropertySpecification.hasElevator(criteria.elevator()))
                .and(PropertySpecification.hasFurnished(criteria.furnished()));
    }

    private Specification<Property> buildAdminSpecification(PropertySearchCriteria criteria) {
        return Specification.where(PropertySpecification.hasPropertyType(criteria.propertyType()))
                .and(PropertySpecification.hasTransactionType(criteria.transactionType()))
                .and(PropertySpecification.hasProvince(criteria.province()))
                .and(PropertySpecification.hasCityLike(criteria.city()))
                .and(PropertySpecification.hasPriceGreaterThanOrEqual(criteria.minPrice()))
                .and(PropertySpecification.hasPriceLessThanOrEqual(criteria.maxPrice()))
                .and(PropertySpecification.hasSurfaceGreaterThanOrEqual(criteria.minSurface()))
                .and(PropertySpecification.hasSurfaceLessThanOrEqual(criteria.maxSurface()))
                .and(PropertySpecification.hasMinBedrooms(criteria.minBedrooms()))
                .and(PropertySpecification.hasEnergyRating(criteria.energyRating()));
    }

    private void validateStatusTransition(PropertyStatus current, PropertyStatus target) {
        boolean valid = switch (current) {
            case DRAFT -> target == PropertyStatus.PUBLISHED || target == PropertyStatus.ARCHIVED;
            case PUBLISHED -> target == PropertyStatus.SOLD || target == PropertyStatus.RENTED || target == PropertyStatus.ARCHIVED;
            case SOLD, RENTED -> target == PropertyStatus.ARCHIVED;
            case ARCHIVED -> target == PropertyStatus.DRAFT;
        };
        if (!valid) {
            throw new InvalidOperationException(
                    String.format("Transition de statut invalide : %s vers %s", current.getLabel(), target.getLabel()));
        }
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
