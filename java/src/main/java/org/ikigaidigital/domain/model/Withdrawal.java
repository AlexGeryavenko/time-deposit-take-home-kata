package org.ikigaidigital.domain.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Withdrawal {

  private final int id;
  private final double amount;
  private final LocalDate date;
}
