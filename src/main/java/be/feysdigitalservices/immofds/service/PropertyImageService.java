package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.entity.PropertyImage;
import be.feysdigitalservices.immofds.dto.request.ImageReorderRequest;
import be.feysdigitalservices.immofds.dto.response.PropertyImageResponse;
import be.feysdigitalservices.immofds.exception.ImageProcessingException;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.PropertyMapper;
import be.feysdigitalservices.immofds.repository.PropertyImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PropertyImageService {

    private final PropertyImageRepository imageRepository;
    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;

    public PropertyImageService(PropertyImageRepository imageRepository, PropertyService propertyService,
                                PropertyMapper propertyMapper) {
        this.imageRepository = imageRepository;
        this.propertyService = propertyService;
        this.propertyMapper = propertyMapper;
    }

    @Transactional
    public PropertyImageResponse uploadImage(String propertyReference, MultipartFile file, boolean isPrimary) {
        Property property = propertyService.findByReference(propertyReference);

        try {
            PropertyImage image = new PropertyImage();
            image.setFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setData(file.getBytes());
            image.setDisplayOrder(imageRepository.findMaxDisplayOrderByPropertyId(property.getId()).orElse(-1) + 1);
            image.setPrimary(isPrimary);
            image.setProperty(property);

            if (isPrimary) {
                clearPrimaryFlag(property.getId());
            }

            PropertyImage saved = imageRepository.save(image);
            return propertyMapper.toImageResponse(saved);
        } catch (IOException e) {
            throw new ImageProcessingException("Erreur lors du traitement de l'image", e);
        }
    }

    public PropertyImage getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
    }

    public List<PropertyImageResponse> getImagesByProperty(String propertyReference) {
        Property property = propertyService.findByReference(propertyReference);
        List<PropertyImage> images = imageRepository.findByPropertyIdOrderByDisplayOrderAsc(property.getId());
        return propertyMapper.toImageResponseList(images);
    }

    @Transactional
    public void reorderImages(String propertyReference, ImageReorderRequest request) {
        Property property = propertyService.findByReference(propertyReference);
        List<Long> imageIds = request.imageIds();

        for (int i = 0; i < imageIds.size(); i++) {
            PropertyImage image = imageRepository.findByIdAndPropertyId(imageIds.get(i), property.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image non trouvée pour ce bien"));
            image.setDisplayOrder(i);
            imageRepository.save(image);
        }
    }

    @Transactional
    public void setPrimaryImage(String propertyReference, Long imageId) {
        Property property = propertyService.findByReference(propertyReference);
        PropertyImage image = imageRepository.findByIdAndPropertyId(imageId, property.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));

        clearPrimaryFlag(property.getId());
        image.setPrimary(true);
        imageRepository.save(image);
    }

    @Transactional
    public void deleteImage(String propertyReference, Long imageId) {
        Property property = propertyService.findByReference(propertyReference);
        PropertyImage image = imageRepository.findByIdAndPropertyId(imageId, property.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        imageRepository.delete(image);
    }

    private void clearPrimaryFlag(Long propertyId) {
        List<PropertyImage> images = imageRepository.findByPropertyIdOrderByDisplayOrderAsc(propertyId);
        images.forEach(img -> {
            if (img.isPrimary()) {
                img.setPrimary(false);
                imageRepository.save(img);
            }
        });
    }
}
