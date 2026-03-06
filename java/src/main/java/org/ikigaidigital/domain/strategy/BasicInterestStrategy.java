package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.constant.InterestConstants;
import org.ikigaidigital.domain.enums.PlanType;

public class BasicInterestStrategy implements InterestStrategy {

    @Override
    public PlanType getPlanType() {
        return PlanType.BASIC;
    }

    private static final double ANNUAL_RATE = 0.01;
    private static final int MIN_DAYS = 30;

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() <= MIN_DAYS) {
            return 0;
        }
        return deposit.getBalance() * ANNUAL_RATE / InterestConstants.MONTHS_PER_YEAR;
    }
}
