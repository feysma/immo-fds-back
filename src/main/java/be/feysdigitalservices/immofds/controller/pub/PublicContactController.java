package be.feysdigitalservices.immofds.controller.pub;

import be.feysdigitalservices.immofds.dto.request.GeneralContactRequest;
import be.feysdigitalservices.immofds.dto.request.SellYourHomeRequest;
import be.feysdigitalservices.immofds.dto.request.VisitRequestDto;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.service.ContactRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/contacts")
@Tag(name = "Public - Contacts", description = "Formulaires de contact publics")
public class PublicContactController {

    private final ContactRequestService contactRequestService;

    public PublicContactController(ContactRequestService contactRequestService) {
        this.contactRequestService = contactRequestService;
    }

    @PostMapping("/general")
    @Operation(summary = "Envoyer un formulaire de contact général")
    public ResponseEntity<ContactRequestResponse> submitGeneralContact(
            @Valid @RequestBody GeneralContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactRequestService.createGeneralContact(request));
    }

    @PostMapping("/sell-your-home")
    @Operation(summary = "Envoyer un formulaire 'Vendre votre bien'")
    public ResponseEntity<ContactRequestResponse> submitSellYourHome(
            @Valid @RequestBody SellYourHomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactRequestService.createSellYourHome(request));
    }

    @PostMapping("/visit-request")
    @Operation(summary = "Envoyer une demande de visite")
    public ResponseEntity<ContactRequestResponse> submitVisitRequest(
            @Valid @RequestBody VisitRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactRequestService.createVisitRequest(request));
    }
}
