package com.budgetapi.account.model;

import com.budgetapi.auditing.AbstractAuditable;
import com.budgetapi.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Account.TABLE_NAME)
@SQLDelete(sql = "UPDATE accounts SET deleted = true WHERE id=?")
@EntityListeners(AuditingEntityListener.class)
public class Account extends AbstractAuditable implements Serializable {

    public static final String TABLE_NAME = "accounts";

    @Serial
    private static final long serialVersionUID = -3564927878999043356L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @NotBlank
    @NotNull
    @Length(min = 5, max = 50)
    private String name;

    @Setter
    @NotBlank
    @NotNull
    @Length(min = 3, max = 3)
    private String currency;

    @Setter
    @NotNull
    @ManyToOne
    private User user;

    @Getter
    private boolean deleted = Boolean.FALSE;

}
