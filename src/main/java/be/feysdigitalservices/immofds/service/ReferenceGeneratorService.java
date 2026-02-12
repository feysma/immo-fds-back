package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class ReferenceGeneratorService {

    private final PropertyRepository propertyRepository;

    public ReferenceGeneratorService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public String generateReference() {
        long nextId = propertyRepository.findMaxId() + 1;
        return String.format("IMM-%d-%05d", Year.now().getValue(), nextId);
    }
}
