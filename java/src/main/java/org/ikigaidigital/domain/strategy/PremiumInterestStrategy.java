package org.ikigaidigital.domain.strategy;

import static org.ikigaidigital.domain.constant.InterestConstants.MONTHS_PER_YEAR;

import org.ikigaidigital.domain.enums.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;

public class PremiumInterestStrategy implements InterestStrategy {

  private static final double ANNUAL_RATE = 0.05;
  private static final int MIN_DAYS = 45;

  @Override
  public PlanType getPlanType() {
    return PlanType.PREMIUM;
  }

  @Override
  public double calculateInterest(TimeDeposit deposit) {
    if (deposit.getDays() <= MIN_DAYS) {
      return 0;
    }
    return deposit.getBalance() * ANNUAL_RATE / MONTHS_PER_YEAR;
  }
}
