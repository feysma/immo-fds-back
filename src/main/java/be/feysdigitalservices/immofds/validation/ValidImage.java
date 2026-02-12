package be.feysdigitalservices.immofds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default "Le fichier doit être une image valide (JPEG, PNG, WebP)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
