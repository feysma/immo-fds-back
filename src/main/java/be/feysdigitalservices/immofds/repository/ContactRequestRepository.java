package be.feysdigitalservices.immofds.repository;

import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.domain.enums.ContactType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    Page<ContactRequest> findByStatus(ContactStatus status, Pageable pageable);

    Page<ContactRequest> findByContactType(ContactType contactType, Pageable pageable);

    Page<ContactRequest> findByStatusAndContactType(ContactStatus status, ContactType contactType, Pageable pageable);
}
