package org.ikigaidigital.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.strategy.InterestStrategy;
import org.ikigaidigital.domain.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.strategy.StudentInterestStrategy;

public class TimeDepositCalculator {

  private final List<InterestStrategy> strategies;

  public TimeDepositCalculator() {
    this.strategies = List.of(
        new BasicInterestStrategy(),
        new StudentInterestStrategy(),
        new PremiumInterestStrategy()
    );
  }

  public TimeDepositCalculator(List<InterestStrategy> strategies) {
    this.strategies = strategies;
  }

  public void updateBalance(List<TimeDeposit> deposits) {
    for (TimeDeposit deposit : deposits) {
      double interest = strategies.stream()
          .filter(strategy -> strategy.supports(deposit.getPlanType()))
          .findFirst()
          .map(strategy -> strategy.calculateInterest(deposit))
          .orElse(0.0);

      double rounded = new BigDecimal(interest)
          .setScale(2, RoundingMode.HALF_UP)
          .doubleValue();

      deposit.setBalance(deposit.getBalance() + rounded);
    }
  }
}
