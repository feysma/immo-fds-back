package be.feysdigitalservices.immofds.repository;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.enums.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Optional<Property> findByReference(String reference);

    boolean existsByReference(String reference);

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Property p")
    long findMaxId();

    Optional<Property> findByReferenceAndStatus(String reference, PropertyStatus status);
}
