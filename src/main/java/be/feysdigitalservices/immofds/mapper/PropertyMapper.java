package be.feysdigitalservices.immofds.mapper;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.entity.PropertyImage;
import be.feysdigitalservices.immofds.dto.request.PropertyCreateRequest;
import be.feysdigitalservices.immofds.dto.response.PropertyDetailResponse;
import be.feysdigitalservices.immofds.dto.response.PropertyImageResponse;
import be.feysdigitalservices.immofds.dto.response.PropertySummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    Property toEntity(PropertyCreateRequest request);

    @Mapping(target = "propertyType", expression = "java(property.getPropertyType().getLabel())")
    @Mapping(target = "transactionType", expression = "java(property.getTransactionType().getLabel())")
    @Mapping(target = "status", expression = "java(property.getStatus().getLabel())")
    @Mapping(target = "province", expression = "java(property.getProvince().getLabel())")
    @Mapping(target = "energyRating", expression = "java(property.getEnergyRating() != null ? property.getEnergyRating().getLabel() : null)")
    @Mapping(target = "primaryImageId", expression = "java(getPrimaryImageId(property))")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    PropertySummaryResponse toSummaryResponse(Property property);

    @Mapping(target = "propertyType", expression = "java(property.getPropertyType().getLabel())")
    @Mapping(target = "transactionType", expression = "java(property.getTransactionType().getLabel())")
    @Mapping(target = "status", expression = "java(property.getStatus().getLabel())")
    @Mapping(target = "province", expression = "java(property.getProvince().getLabel())")
    @Mapping(target = "energyRating", expression = "java(property.getEnergyRating() != null ? property.getEnergyRating().getLabel() : null)")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "formatDateTime")
    PropertyDetailResponse toDetailResponse(Property property);

    @Mapping(target = "isPrimary", source = "primary")
    PropertyImageResponse toImageResponse(PropertyImage image);

    List<PropertyImageResponse> toImageResponseList(List<PropertyImage> images);

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    default Long getPrimaryImageId(Property property) {
        if (property.getImages() == null) return null;
        return property.getImages().stream()
                .filter(PropertyImage::isPrimary)
                .map(PropertyImage::getId)
                .findFirst()
                .orElse(property.getImages().isEmpty() ? null : property.getImages().get(0).getId());
    }
}
