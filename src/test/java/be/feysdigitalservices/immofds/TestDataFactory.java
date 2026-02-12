package be.feysdigitalservices.immofds;

import be.feysdigitalservices.immofds.domain.entity.ContactRequest;
import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.entity.User;
import be.feysdigitalservices.immofds.domain.enums.*;
import be.feysdigitalservices.immofds.dto.request.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static Property createProperty() {
        Property property = new Property();
        property.setId(1L);
        property.setReference("IMM-2026-00001");
        property.setTitle("Belle maison à Bruxelles");
        property.setDescription("Magnifique maison 3 chambres");
        property.setPropertyType(PropertyType.HOUSE);
        property.setTransactionType(TransactionType.SALE);
        property.setStatus(PropertyStatus.PUBLISHED);
        property.setPrice(new BigDecimal("350000.00"));
        property.setSurface(150.0);
        property.setBedrooms(3);
        property.setBathrooms(2);
        property.setRooms(7);
        property.setFloors(2);
        property.setConstructionYear(2005);
        property.setEnergyRating(EnergyRating.B);
        property.setGarden(true);
        property.setGarage(true);
        property.setTerrace(false);
        property.setBasement(true);
        property.setElevator(false);
        property.setFurnished(false);
        property.setStreet("Rue de la Loi");
        property.setNumber("42");
        property.setPostalCode("1000");
        property.setCity("Bruxelles");
        property.setProvince(Province.BRUXELLES_CAPITALE);
        property.setLatitude(50.8503);
        property.setLongitude(4.3517);
        return property;
    }

    public static PropertyCreateRequest createPropertyRequest() {
        return new PropertyCreateRequest(
                "Belle maison à Bruxelles",
                "Magnifique maison 3 chambres",
                PropertyType.HOUSE,
                TransactionType.SALE,
                new BigDecimal("350000.00"),
                150.0, 3, 2, 7, 2, 2005,
                EnergyRating.B,
                true, true, false, true, false, false,
                "Rue de la Loi", "42", "1000", "Bruxelles",
                Province.BRUXELLES_CAPITALE,
                50.8503, 4.3517
        );
    }

    public static PropertyUpdateRequest createPropertyUpdateRequest() {
        return new PropertyUpdateRequest(
                "Maison rénovée à Bruxelles",
                "Maison entièrement rénovée",
                PropertyType.HOUSE,
                TransactionType.SALE,
                new BigDecimal("375000.00"),
                155.0, 3, 2, 7, 2, 2005,
                EnergyRating.A,
                true, true, true, true, false, false,
                "Rue de la Loi", "42", "1000", "Bruxelles",
                Province.BRUXELLES_CAPITALE,
                50.8503, 4.3517
        );
    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@immofds.be");
        user.setPassword("$2a$10$encoded");
        user.setFirstName("Admin");
        user.setLastName("ImmoFDS");
        user.setRole(UserRole.SUPER_ADMIN);
        user.setActive(true);
        return user;
    }

    public static UserCreateRequest createUserRequest() {
        return new UserCreateRequest(
                "newadmin@immofds.be",
                "SecurePass123!",
                "Nouveau",
                "Admin",
                UserRole.ADMIN
        );
    }

    public static ContactRequest createContactRequest() {
        ContactRequest contact = new ContactRequest();
        contact.setId(1L);
        contact.setContactType(ContactType.GENERAL_CONTACT);
        contact.setStatus(ContactStatus.NEW);
        contact.setFirstName("Jean");
        contact.setLastName("Dupont");
        contact.setEmail("jean.dupont@example.com");
        contact.setPhone("+32 470 12 34 56");
        contact.setMessage("Je souhaite des informations sur vos biens.");
        return contact;
    }

    public static GeneralContactRequest createGeneralContactRequest() {
        return new GeneralContactRequest(
                "Jean",
                "Dupont",
                "jean.dupont@example.com",
                "+32 470 12 34 56",
                "Je souhaite des informations."
        );
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest("admin@immofds.be", "Admin@2026!");
    }
}
