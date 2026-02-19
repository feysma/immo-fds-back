package be.feysdigitalservices.immofds.repository;

import be.feysdigitalservices.immofds.domain.entity.ContactNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactNoteRepository extends JpaRepository<ContactNote, Long> {

    List<ContactNote> findByContactRequestIdOrderByCreatedAtAsc(Long contactRequestId);

    Optional<ContactNote> findTopByContactRequestIdOrderByCreatedAtDesc(Long contactRequestId);
}
