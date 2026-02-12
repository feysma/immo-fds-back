package be.feysdigitalservices.immofds.controller.admin;

import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.domain.enums.ContactType;
import be.feysdigitalservices.immofds.dto.request.ContactNotesUpdateRequest;
import be.feysdigitalservices.immofds.dto.request.ContactStatusUpdateRequest;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.dto.response.MessageResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.service.ContactRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/contacts")
@Tag(name = "Admin - Contacts", description = "Gestion des demandes de contact")
public class AdminContactController {

    private final ContactRequestService contactRequestService;

    public AdminContactController(ContactRequestService contactRequestService) {
        this.contactRequestService = contactRequestService;
    }

    @GetMapping
    @Operation(summary = "Lister les demandes de contact")
    public ResponseEntity<PageResponse<ContactRequestResponse>> getContacts(
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(required = false) ContactType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(contactRequestService.getAllContacts(status, type, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une demande de contact")
    public ResponseEntity<ContactRequestResponse> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(contactRequestService.getContactById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Modifier le statut d'une demande de contact")
    public ResponseEntity<ContactRequestResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ContactStatusUpdateRequest request) {
        return ResponseEntity.ok(contactRequestService.updateStatus(id, request.status()));
    }

    @PatchMapping("/{id}/notes")
    @Operation(summary = "Modifier les notes admin d'une demande de contact")
    public ResponseEntity<ContactRequestResponse> updateNotes(
            @PathVariable Long id,
            @RequestBody ContactNotesUpdateRequest request) {
        return ResponseEntity.ok(contactRequestService.updateNotes(id, request.adminNotes()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une demande de contact")
    public ResponseEntity<MessageResponse> deleteContact(@PathVariable Long id) {
        contactRequestService.deleteContact(id);
        return ResponseEntity.ok(new MessageResponse("Demande de contact supprimée avec succès"));
    }
}
