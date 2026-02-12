package be.feysdigitalservices.immofds.controller.admin;

import be.feysdigitalservices.immofds.dto.request.ImageReorderRequest;
import be.feysdigitalservices.immofds.dto.response.MessageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyImageResponse;
import be.feysdigitalservices.immofds.service.PropertyImageService;
import be.feysdigitalservices.immofds.validation.ValidImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/properties/{reference}/images")
@Tag(name = "Admin - Images", description = "Gestion des images des biens")
public class AdminPropertyImageController {

    private final PropertyImageService imageService;

    public AdminPropertyImageController(PropertyImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    @Operation(summary = "Lister les images d'un bien")
    public ResponseEntity<List<PropertyImageResponse>> getImages(@PathVariable String reference) {
        return ResponseEntity.ok(imageService.getImagesByProperty(reference));
    }

    @PostMapping
    @Operation(summary = "Uploader une image")
    public ResponseEntity<PropertyImageResponse> uploadImage(
            @PathVariable String reference,
            @RequestParam("file") @ValidImage MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.uploadImage(reference, file, isPrimary));
    }

    @PutMapping("/reorder")
    @Operation(summary = "Réordonner les images")
    public ResponseEntity<MessageResponse> reorderImages(
            @PathVariable String reference,
            @Valid @RequestBody ImageReorderRequest request) {
        imageService.reorderImages(reference, request);
        return ResponseEntity.ok(new MessageResponse("Images réordonnées avec succès"));
    }

    @PatchMapping("/{imageId}/primary")
    @Operation(summary = "Définir une image comme principale")
    public ResponseEntity<MessageResponse> setPrimaryImage(
            @PathVariable String reference,
            @PathVariable Long imageId) {
        imageService.setPrimaryImage(reference, imageId);
        return ResponseEntity.ok(new MessageResponse("Image principale mise à jour"));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Supprimer une image")
    public ResponseEntity<MessageResponse> deleteImage(
            @PathVariable String reference,
            @PathVariable Long imageId) {
        imageService.deleteImage(reference, imageId);
        return ResponseEntity.ok(new MessageResponse("Image supprimée avec succès"));
    }
}
