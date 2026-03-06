package org.ikigaidigital.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class TimeDeposit {

    private int id;
    private String planType;
    @Setter
    private Double balance;
    private int days;
}
