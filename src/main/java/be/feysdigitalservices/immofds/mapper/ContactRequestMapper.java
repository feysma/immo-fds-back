package be.feysdigitalservices.immofds.mapper;

import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.dto.request.GeneralContactRequest;
import be.feysdigitalservices.immofds.dto.request.SellYourHomeRequest;
import be.feysdigitalservices.immofds.dto.request.VisitRequestDto;
import be.feysdigitalservices.immofds.dto.response.ContactRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ContactRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactType", constant = "GENERAL_CONTACT")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "propertyReference", ignore = true)
    @Mapping(target = "propertyAddress", ignore = true)
    @Mapping(target = "propertyType", ignore = true)
    @Mapping(target = "estimatedPrice", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactRequest toEntity(GeneralContactRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactType", constant = "SELL_YOUR_HOME")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "propertyReference", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactRequest toEntity(SellYourHomeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactType", constant = "VISIT_REQUEST")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "propertyAddress", ignore = true)
    @Mapping(target = "propertyType", ignore = true)
    @Mapping(target = "estimatedPrice", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactRequest toEntity(VisitRequestDto request);

    @Mapping(target = "contactType", expression = "java(entity.getContactType().name())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    @Mapping(target = "propertyType", expression = "java(entity.getPropertyType() != null ? entity.getPropertyType().name() : null)")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "formatDateTime")
    ContactRequestResponse toResponse(ContactRequest entity);

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
