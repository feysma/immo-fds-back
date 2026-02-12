package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.domain.enums.ContactType;
import be.feysdigitalservices.immofds.dto.request.GeneralContactRequest;
import be.feysdigitalservices.immofds.dto.request.SellYourHomeRequest;
import be.feysdigitalservices.immofds.dto.request.VisitRequestDto;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.ContactRequestMapper;
import be.feysdigitalservices.immofds.repository.ContactRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final ContactRequestMapper contactRequestMapper;

    public ContactRequestService(ContactRequestRepository contactRequestRepository,
                                 ContactRequestMapper contactRequestMapper) {
        this.contactRequestRepository = contactRequestRepository;
        this.contactRequestMapper = contactRequestMapper;
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
    public ContactRequestResponse updateNotes(Long id, String adminNotes) {
        ContactRequest entity = findById(id);
        entity.setAdminNotes(adminNotes);
        ContactRequest saved = contactRequestRepository.save(entity);
        return contactRequestMapper.toResponse(saved);
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
