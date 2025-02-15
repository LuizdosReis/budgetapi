package com.budgetapi.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 8842499629765771389L;

    @Id
    private UUID id;

    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String password;

    @NotBlank
    @NotNull
    private String roles;

    @Builder
    private User(String username, String password, String roles) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}
