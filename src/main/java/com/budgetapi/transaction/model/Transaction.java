package com.budgetapi.transaction.model;

import com.budgetapi.account.model.Account;
import com.budgetapi.auditing.AbstractAuditable;
import com.budgetapi.category.model.Category;
import com.budgetapi.tag.model.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Transaction.TABLE_NAME)
@SQLDelete(sql = "UPDATE transactions SET deleted = true WHERE id=?")
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends AbstractAuditable implements Serializable {

    public static final String TABLE_NAME = "transactions";

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @NotBlank
    @NotNull
    @Length(min = 5, max = 50)
    private String description;

    @Setter
    @NotNull
    @ManyToOne
    private Account account;

    @Setter
    @NotNull
    @ManyToOne
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status = TransactionStatus.REGISTERED;

    @Getter
    private boolean deleted = Boolean.FALSE;

}
