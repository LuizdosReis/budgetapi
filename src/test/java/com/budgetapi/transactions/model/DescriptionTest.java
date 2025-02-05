package com.budgetapi.transactions.model;


import com.budgetapi.transaction.model.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.budgetapi.transaction.model.Description.MAX_LENGTH;
import static com.budgetapi.transaction.model.Description.MIN_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescriptionTest {

    @ParameterizedTest
    @DisplayName("Create new description")
    @MethodSource("validDescriptionArguments")
    void createDescription(String value) {
        Description description = new Description(value);
        assertThat(description.value()).isEqualTo(value);
    }

    private static Stream<String> validDescriptionArguments() {
        return Stream.of(
                "description",
                "a".repeat(MIN_LENGTH),
                "a".repeat(MAX_LENGTH)
        );
    }

    @ParameterizedTest
    @DisplayName("Do not create description with invalid value")
    @MethodSource("invalidDescriptionArguments")
    void doNotCreateDescriptionWithInvalidValue(String value, String message) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Description(value));
        assertThat(exception.getMessage()).contains(message);
    }

    private static Stream<Arguments> invalidDescriptionArguments() {
        return Stream.of(
                Arguments.of(null, "Description must have text"),
                Arguments.of("", "Description must have text"),
                Arguments.of("a".repeat(MIN_LENGTH - 1), String.format("Description must be between %s and %s characters", MIN_LENGTH, MAX_LENGTH)),
                Arguments.of("a".repeat(MAX_LENGTH + 1), String.format("Description must be between %s and %s characters", MIN_LENGTH, MAX_LENGTH))
        );
    }
}
