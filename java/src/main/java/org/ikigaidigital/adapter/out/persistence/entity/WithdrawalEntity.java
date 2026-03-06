package org.ikigaidigital.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "withdrawals_seq")
    @SequenceGenerator(name = "withdrawals_seq", sequenceName = "withdrawals_seq", allocationSize = 50)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "time_deposit_id", nullable = false,
        foreignKey = @ForeignKey(name = "withdrawals_time_deposit_id_fkey"))
    private TimeDepositEntity timeDeposit;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;
}
