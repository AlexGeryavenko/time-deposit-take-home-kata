package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.enums.PlanType;

public class StudentInterestStrategy implements InterestStrategy {

    @Override
    public PlanType getPlanType() {
        return PlanType.STUDENT;
    }

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        return 0;
    }
}
