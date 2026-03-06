package org.ikigaidigital.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Withdrawal {

    private int id;
    private double amount;
    private LocalDate date;
}
