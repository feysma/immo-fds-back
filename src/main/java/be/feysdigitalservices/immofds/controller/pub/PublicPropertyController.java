package be.feysdigitalservices.immofds.controller.pub;

import be.feysdigitalservices.immofds.domain.entity.PropertyImage;
import be.feysdigitalservices.immofds.domain.enums.*;
import be.feysdigitalservices.immofds.dto.request.PropertySearchCriteria;
import be.feysdigitalservices.immofds.dto.response.EnumValueResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import be.feysdigitalservices.immofds.service.PropertyImageService;
import be.feysdigitalservices.immofds.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/public/properties")
@Tag(name = "Public - Biens", description = "Endpoints publics pour la recherche de biens")
public class PublicPropertyController {

    private final PropertyService propertyService;
    private final PropertyImageService propertyImageService;

    public PublicPropertyController(PropertyService propertyService, PropertyImageService propertyImageService) {
        this.propertyService = propertyService;
        this.propertyImageService = propertyImageService;
    }

    @GetMapping
    @Operation(summary = "Rechercher des biens avec filtres")
    public ResponseEntity<PageResponse<PropertySummaryResponse>> searchProperties(
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) Province province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minSurface,
            @RequestParam(required = false) Double maxSurface,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) EnergyRating energyRating,
            @RequestParam(required = false) Boolean garden,
            @RequestParam(required = false) Boolean garage,
            @RequestParam(required = false) Boolean terrace,
            @RequestParam(required = false) Boolean basement,
            @RequestParam(required = false) Boolean elevator,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PropertySearchCriteria criteria = new PropertySearchCriteria(
                propertyType, transactionType, province, city,
                minPrice, maxPrice, minSurface, maxSurface, minBedrooms,
                energyRating, garden, garage, terrace, basement, elevator, furnished);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(propertyService.searchPublicProperties(criteria, pageable));
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Détail d'un bien par référence")
    public ResponseEntity<PropertyDetailResponse> getPropertyByReference(@PathVariable String reference) {
        return ResponseEntity.ok(propertyService.getPublicPropertyByReference(reference));
    }

    @GetMapping("/{reference}/images/{imageId}")
    @Operation(summary = "Télécharger une image d'un bien")
    public ResponseEntity<byte[]> getImage(@PathVariable String reference, @PathVariable Long imageId) {
        PropertyImage image = propertyImageService.getImage(imageId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
                .body(image.getData());
    }

    @GetMapping("/types")
    @Operation(summary = "Liste des types de biens")
    public ResponseEntity<List<EnumValueResponse>> getPropertyTypes() {
        List<EnumValueResponse> types = Arrays.stream(PropertyType.values())
                .map(t -> new EnumValueResponse(t.name(), t.getLabel()))
                .toList();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/provinces")
    @Operation(summary = "Liste des provinces belges")
    public ResponseEntity<List<EnumValueResponse>> getProvinces() {
        List<EnumValueResponse> provinces = Arrays.stream(Province.values())
                .map(p -> new EnumValueResponse(p.name(), p.getLabel()))
                .toList();
        return ResponseEntity.ok(provinces);
    }
}
