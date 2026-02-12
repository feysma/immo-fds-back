package be.feysdigitalservices.immofds.mapper;

import be.feysdigitalservices.immofds.domain.entity.User;
import be.feysdigitalservices.immofds.dto.request.UserCreateRequest;
import be.feysdigitalservices.immofds.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "role", expression = "java(user.getRole().getLabel())")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    UserResponse toResponse(User user);

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
