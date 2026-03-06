package org.ikigaidigital.domain.strategy;

import static org.ikigaidigital.domain.constant.InterestConstants.MONTHS_PER_YEAR;

import org.ikigaidigital.domain.enums.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;

public class StudentInterestStrategy implements InterestStrategy {

  private static final double ANNUAL_RATE = 0.03;
  private static final int MIN_DAYS = 30;
  private static final int MAX_DAYS = 366;

  @Override
  public PlanType getPlanType() {
    return PlanType.STUDENT;
  }

  @Override
  public double calculateInterest(TimeDeposit deposit) {
    if (deposit.getDays() <= MIN_DAYS || deposit.getDays() >= MAX_DAYS) {
      return 0;
    }
    return deposit.getBalance() * ANNUAL_RATE / MONTHS_PER_YEAR;
  }
}
