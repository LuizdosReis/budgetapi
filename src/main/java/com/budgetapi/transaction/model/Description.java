package com.budgetapi.transaction.model;

import org.springframework.util.Assert;

import java.io.Serializable;

public record Description(String value) implements Serializable {

    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 50;

    public Description {
        Assert.hasText(value, "Description must have text");
        Assert.isTrue(value.length() >= MIN_LENGTH && value.length() <= MAX_LENGTH, String.format("Description must be between %s and %s characters", MIN_LENGTH, MAX_LENGTH));
    }
}
