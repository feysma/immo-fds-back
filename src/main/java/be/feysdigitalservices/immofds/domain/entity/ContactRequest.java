package be.feysdigitalservices.immofds.domain.entity;

import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import be.feysdigitalservices.immofds.domain.enums.ContactType;
import be.feysdigitalservices.immofds.domain.enums.PropertyType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contact_requests")
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 20)
    private ContactType contactType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ContactStatus status;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "property_reference", length = 20)
    private String propertyReference;

    @Column(name = "property_address")
    private String propertyAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 30)
    private PropertyType propertyType;

    @Column(name = "estimated_price", precision = 12, scale = 2)
    private BigDecimal estimatedPrice;

    @OneToMany(mappedBy = "contactRequest", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<ContactNote> notes = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ContactStatus.NEW;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ContactType getContactType() { return contactType; }
    public void setContactType(ContactType contactType) { this.contactType = contactType; }

    public ContactStatus getStatus() { return status; }
    public void setStatus(ContactStatus status) { this.status = status; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPropertyReference() { return propertyReference; }
    public void setPropertyReference(String propertyReference) { this.propertyReference = propertyReference; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public List<ContactNote> getNotes() { return notes; }
    public void setNotes(List<ContactNote> notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
