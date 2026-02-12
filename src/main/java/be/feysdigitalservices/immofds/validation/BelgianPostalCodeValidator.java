package be.feysdigitalservices.immofds.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class BelgianPostalCodeValidator implements ConstraintValidator<BelgianPostalCode, String> {

    private static final Pattern BELGIAN_POSTAL_CODE_PATTERN = Pattern.compile("^[1-9]\\d{3}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return BELGIAN_POSTAL_CODE_PATTERN.matcher(value).matches();
    }
}
