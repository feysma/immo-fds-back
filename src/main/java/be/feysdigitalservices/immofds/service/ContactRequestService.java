package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.domain.entity.ContactNote;
import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.domain.entity.User;
import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.domain.enums.ContactType;
import be.feysdigitalservices.immofds.dto.request.GeneralContactRequest;
import be.feysdigitalservices.immofds.dto.request.SellYourHomeRequest;
import be.feysdigitalservices.immofds.dto.request.VisitRequestDto;
import be.feysdigitalservices.immofds.dto.response.ContactNoteResponse;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.ContactNoteMapper;
import be.feysdigitalservices.immofds.mapper.ContactRequestMapper;
import be.feysdigitalservices.immofds.repository.ContactNoteRepository;
import be.feysdigitalservices.immofds.repository.ContactRequestRepository;
import be.feysdigitalservices.immofds.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final ContactNoteRepository contactNoteRepository;
    private final UserRepository userRepository;
    private final ContactRequestMapper contactRequestMapper;
    private final ContactNoteMapper contactNoteMapper;

    public ContactRequestService(ContactRequestRepository contactRequestRepository,
                                 ContactNoteRepository contactNoteRepository,
                                 UserRepository userRepository,
                                 ContactRequestMapper contactRequestMapper,
                                 ContactNoteMapper contactNoteMapper) {
        this.contactRequestRepository = contactRequestRepository;
        this.contactNoteRepository = contactNoteRepository;
        this.userRepository = userRepository;
        this.contactRequestMapper = contactRequestMapper;
        this.contactNoteMapper = contactNoteMapper;
    }

    @Transactional
    public ContactRequestResponse createGeneralContact(GeneralContactRequest request) {
        ContactRequest entity = contactRequestMapper.toEntity(request);
        ContactRequest saved = contactRequestRepository.save(entity);
        return contactRequestMapper.toResponse(saved);
    }

    @Transactional
    public ContactRequestResponse createSellYourHome(SellYourHomeRequest request) {
        ContactRequest entity = contactRequestMapper.toEntity(request);
        ContactRequest saved = contactRequestRepository.save(entity);
        return contactRequestMapper.toResponse(saved);
    }

    @Transactional
    public ContactRequestResponse createVisitRequest(VisitRequestDto request) {
        ContactRequest entity = contactRequestMapper.toEntity(request);
        ContactRequest saved = contactRequestRepository.save(entity);
        return contactRequestMapper.toResponse(saved);
    }

    public PageResponse<ContactRequestResponse> getAllContacts(ContactStatus status, ContactType type, Pageable pageable) {
        Page<ContactRequest> page;
        if (status != null && type != null) {
            page = contactRequestRepository.findByStatusAndContactType(status, type, pageable);
        } else if (status != null) {
            page = contactRequestRepository.findByStatus(status, pageable);
        } else if (type != null) {
            page = contactRequestRepository.findByContactType(type, pageable);
        } else {
            page = contactRequestRepository.findAll(pageable);
        }
        return toPageResponse(page.map(contactRequestMapper::toResponse));
    }

    public ContactRequestResponse getContactById(Long id) {
        ContactRequest entity = findById(id);
        return contactRequestMapper.toResponse(entity);
    }

    @Transactional
    public ContactRequestResponse updateStatus(Long id, ContactStatus status) {
        ContactRequest entity = findById(id);
        entity.setStatus(status);
        ContactRequest saved = contactRequestRepository.save(entity);
        return contactRequestMapper.toResponse(saved);
    }

    @Transactional
    public ContactNoteResponse addNote(Long contactId, String content, Long authorUserId) {
        ContactRequest contact = findById(contactId);
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", authorUserId));
        ContactNote note = new ContactNote();
        note.setContactRequest(contact);
        note.setAuthor(author);
        note.setContent(content);
        ContactNote saved = contactNoteRepository.save(note);
        return contactNoteMapper.toResponse(saved);
    }

    @Transactional
    public ContactNoteResponse updateLastNote(Long contactId, String content, Long requestingUserId) {
        ContactNote lastNote = contactNoteRepository
                .findTopByContactRequestIdOrderByCreatedAtDesc(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "contactId", contactId));
        if (!lastNote.getAuthor().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres notes.");
        }
        lastNote.setContent(content);
        ContactNote saved = contactNoteRepository.save(lastNote);
        return contactNoteMapper.toResponse(saved);
    }

    @Transactional
    public void deleteContact(Long id) {
        ContactRequest entity = findById(id);
        contactRequestRepository.delete(entity);
    }

    private ContactRequest findById(Long id) {
        return contactRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de contact", "id", id));
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
