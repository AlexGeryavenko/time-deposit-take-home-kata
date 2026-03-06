package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.enums.PlanType;

public interface InterestStrategy {

    PlanType getPlanType();

    double calculateInterest(TimeDeposit deposit);

    default boolean supports(String planType) {
        return getPlanType().getValue().equals(planType);
    }
}
