package be.feysdigitalservices.immofds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BelgianPostalCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BelgianPostalCode {

    String message() default "Le code postal belge doit être composé de 4 chiffres (1000-9999)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
