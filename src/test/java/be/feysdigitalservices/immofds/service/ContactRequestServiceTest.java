package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.TestDataFactory;
import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.dto.request.GeneralContactRequest;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import be.feysdigitalservices.immofds.dto.response.PageResponse;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.ContactRequestMapper;
import be.feysdigitalservices.immofds.repository.ContactRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactRequestServiceTest {

    @Mock
    private ContactRequestRepository contactRequestRepository;

    @Mock
    private ContactRequestMapper contactRequestMapper;

    @InjectMocks
    private ContactRequestService contactRequestService;

    @Test
    void createGeneralContact_shouldSaveAndReturn() {
        GeneralContactRequest request = TestDataFactory.createGeneralContactRequest();
        ContactRequest entity = TestDataFactory.createContactRequest();
        ContactRequestResponse response = mock(ContactRequestResponse.class);

        when(contactRequestMapper.toEntity(request)).thenReturn(entity);
        when(contactRequestRepository.save(entity)).thenReturn(entity);
        when(contactRequestMapper.toResponse(entity)).thenReturn(response);

        ContactRequestResponse result = contactRequestService.createGeneralContact(request);

        assertThat(result).isNotNull();
        verify(contactRequestRepository).save(entity);
    }

    @Test
    void getAllContacts_noFilters_shouldReturnAll() {
        ContactRequest entity = TestDataFactory.createContactRequest();
        Page<ContactRequest> page = new PageImpl<>(List.of(entity));
        ContactRequestResponse response = mock(ContactRequestResponse.class);

        when(contactRequestRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(contactRequestMapper.toResponse(entity)).thenReturn(response);

        PageResponse<ContactRequestResponse> result = contactRequestService.getAllContacts(
                null, null, PageRequest.of(0, 20));

        assertThat(result.content()).hasSize(1);
    }

    @Test
    void updateStatus_shouldUpdateAndReturn() {
        ContactRequest entity = TestDataFactory.createContactRequest();
        ContactRequestResponse response = mock(ContactRequestResponse.class);

        when(contactRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(contactRequestRepository.save(entity)).thenReturn(entity);
        when(contactRequestMapper.toResponse(entity)).thenReturn(response);

        contactRequestService.updateStatus(1L, ContactStatus.IN_PROGRESS);

        assertThat(entity.getStatus()).isEqualTo(ContactStatus.IN_PROGRESS);
    }

    @Test
    void getContactById_notFound_shouldThrow() {
        when(contactRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactRequestService.getContactById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
