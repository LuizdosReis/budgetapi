package com.budgetapi.category.model;

import com.budgetapi.auditing.AbstractAuditable;
import com.budgetapi.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = Category.TABLE_NAME)
@SQLDelete(sql = "UPDATE categories SET deleted = true WHERE id=?")
@EntityListeners(AuditingEntityListener.class)
public class Category extends AbstractAuditable implements Serializable {
    public static final String TABLE_NAME = "categories";

    @Serial
    private static final long serialVersionUID = -2700015069847781699L;

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @NotBlank
    @NotNull
    @Length(min = 5, max = 50)
    private String name;

    @Setter
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Type type;

    @Setter
    @NotNull
    @ManyToOne
    private User user;

    @Getter
    private boolean deleted = Boolean.FALSE;

}
