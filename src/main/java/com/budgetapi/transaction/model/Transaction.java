package com.budgetapi.transaction.model;

import com.budgetapi.account.model.Account;
import com.budgetapi.auditing.AbstractAuditable;
import com.budgetapi.category.model.Category;
import com.budgetapi.tag.model.Tag;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = Transaction.TABLE_NAME)
@SQLDelete(sql = "UPDATE transactions SET deleted = true WHERE id=?")
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends AbstractAuditable implements Serializable {

    public static final String TABLE_NAME = "transactions";

    @Serial
    private static final long serialVersionUID = -4191457889828348790L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "description"))
    private Description description;

    @NotNull
    @ManyToOne
    private Account account;

    @NotNull
    @ManyToOne
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private BigDecimal amount;

    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status = TransactionStatus.REGISTERED;

    @Getter
    private boolean deleted = Boolean.FALSE;

    @Builder
    public Transaction(String description, Account account, Category category, Set<Tag> tags, BigDecimal amount, LocalDate date, TransactionStatus status, boolean deleted) {
        Assert.notNull(account, "Account must not be null");
        Assert.notNull(category, "Category must not be null");
        Assert.notNull(amount, "Amount must not be null");
        Assert.notNull(date, "Date must not be null");
        Assert.notNull(status, "Status must not be null");
        Assert.notNull(tags, "Tags must not be null");
        Assert.isTrue(account.getUser().equals(category.getUser()), "Account user is not the same as category user");
        Assert.isTrue(tags.stream().allMatch(tag -> tag.getUser().equals(account.getUser())), "Tags user is not the same as account user");
        this.description = new Description(description);
        this.account = account;
        this.category = category;
        this.tags = tags;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.deleted = deleted;
    }

    public void setDescription(String description) {
        this.description = new Description(description);
    }

    public String getDescription() {
        return this.description.value();
    }
}
