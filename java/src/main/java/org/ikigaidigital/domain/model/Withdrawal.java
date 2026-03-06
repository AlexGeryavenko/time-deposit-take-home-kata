package org.ikigaidigital.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Withdrawal {

    private final int id;
    private final double amount;
    private final LocalDate date;
}
