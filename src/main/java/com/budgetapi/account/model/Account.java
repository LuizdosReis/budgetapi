package com.budgetapi.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Account.TABLE_NAME)
public class Account {

    public static final String TABLE_NAME= "Accounts";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Length(min = 5, max = 50)
    private String name;

    @NotBlank
    @NotNull
    @Length(min = 3, max = 3)
    private String currency;

}
