package be.feysdigitalservices.immofds.mapper;

import be.feysdigitalservices.immofds.domain.entity.ContactNote;
import be.feysdigitalservices.immofds.dto.response.ContactNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactNoteMapper {

    @Mapping(target = "authorId", expression = "java(note.getAuthor().getId())")
    @Mapping(target = "authorName", expression = "java(note.getAuthor().getFirstName() + \" \" + note.getAuthor().getLastName())")
    @Mapping(target = "createdAt", expression = "java(note.getCreatedAt() != null ? note.getCreatedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)")
    @Mapping(target = "updatedAt", expression = "java(note.getUpdatedAt() != null ? note.getUpdatedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)")
    ContactNoteResponse toResponse(ContactNote note);
}
