package de.nihas101.midas.accountstatement.repository;

import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_statement_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private ShareholderEntity shareholder;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "row_key", nullable = false)
    private String rowKey;

    @Column(name = "position", nullable = false)
    private Integer position;
}
