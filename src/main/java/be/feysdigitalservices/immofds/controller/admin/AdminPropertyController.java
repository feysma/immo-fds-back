package be.feysdigitalservices.immofds.controller.admin;

import be.feysdigitalservices.immofds.domain.enums.*;
import be.feysdigitalservices.immofds.dto.request.PropertyCreateRequest;
import be.feysdigitalservices.immofds.dto.request.PropertySearchCriteria;
import be.feysdigitalservices.immofds.dto.request.PropertyStatusUpdateRequest;
import be.feysdigitalservices.immofds.dto.request.PropertyUpdateRequest;
import be.feysdigitalservices.immofds.dto.response.MessageResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import be.feysdigitalservices.immofds.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin/properties")
@Tag(name = "Admin - Biens", description = "Gestion des biens immobiliers")
public class AdminPropertyController {

    private final PropertyService propertyService;

    public AdminPropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    @Operation(summary = "Rechercher des biens (admin)")
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PropertySearchCriteria criteria = new PropertySearchCriteria(
                propertyType, transactionType, province, city,
                minPrice, maxPrice, minSurface, maxSurface, minBedrooms,
                energyRating, null, null, null, null, null, null);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(propertyService.searchAdminProperties(criteria, pageable));
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Détail d'un bien par référence (admin)")
    public ResponseEntity<PropertyDetailResponse> getProperty(@PathVariable String reference) {
        return ResponseEntity.ok(propertyService.getAdminPropertyByReference(reference));
    }

    @PostMapping
    @Operation(summary = "Créer un bien")
    public ResponseEntity<PropertyDetailResponse> createProperty(
            @Valid @RequestBody PropertyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.createProperty(request));
    }

    @PutMapping("/{reference}")
    @Operation(summary = "Modifier un bien")
    public ResponseEntity<PropertyDetailResponse> updateProperty(
            @PathVariable String reference,
            @Valid @RequestBody PropertyUpdateRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(reference, request));
    }

    @PatchMapping("/{reference}/status")
    @Operation(summary = "Modifier le statut d'un bien")
    public ResponseEntity<PropertyDetailResponse> updateStatus(
            @PathVariable String reference,
            @Valid @RequestBody PropertyStatusUpdateRequest request) {
        return ResponseEntity.ok(propertyService.updateStatus(reference, request.status()));
    }

    @DeleteMapping("/{reference}")
    @Operation(summary = "Archiver un bien")
    public ResponseEntity<MessageResponse> deleteProperty(@PathVariable String reference) {
        propertyService.deleteProperty(reference);
        return ResponseEntity.ok(new MessageResponse("Bien archivé avec succès"));
    }
}
