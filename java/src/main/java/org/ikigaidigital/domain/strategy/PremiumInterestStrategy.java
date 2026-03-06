package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.enums.PlanType;

public class PremiumInterestStrategy implements InterestStrategy {

    @Override
    public PlanType getPlanType() {
        return PlanType.PREMIUM;
    }

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        return 0;
    }
}
