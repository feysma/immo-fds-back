package be.feysdigitalservices.immofds.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BelgianPostalCodeValidatorTest {

    private BelgianPostalCodeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new BelgianPostalCodeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void nullValue_shouldBeValid() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void blankValue_shouldBeValid() {
        assertThat(validator.isValid("", context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "1050", "4000", "7000", "9999"})
    void validBelgianPostalCodes_shouldBeValid(String postalCode) {
        assertThat(validator.isValid(postalCode, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0000", "999", "10000", "ABCD", "12AB", "0100"})
    void invalidPostalCodes_shouldBeInvalid(String postalCode) {
        assertThat(validator.isValid(postalCode, context)).isFalse();
    }
}
