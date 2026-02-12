package be.feysdigitalservices.immofds.repository;

import be.feysdigitalservices.immofds.domain.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

    List<PropertyImage> findByPropertyIdOrderByDisplayOrderAsc(Long propertyId);

    Optional<PropertyImage> findByIdAndPropertyId(Long id, Long propertyId);

    @Query("SELECT MAX(pi.displayOrder) FROM PropertyImage pi WHERE pi.property.id = :propertyId")
    Optional<Integer> findMaxDisplayOrderByPropertyId(Long propertyId);

    int countByPropertyId(Long propertyId);
}
