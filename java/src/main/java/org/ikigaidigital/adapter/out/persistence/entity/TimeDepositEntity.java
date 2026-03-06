package org.ikigaidigital.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "time_deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_deposits_seq")
    @SequenceGenerator(name = "time_deposits_seq", sequenceName = "time_deposits_seq", allocationSize = 50)
    private Integer id;

    @Column(name = "plan_type", nullable = false)
    private String planType;

    @Column(nullable = false)
    private Integer days;

    @Column(nullable = false)
    private Double balance;
}
