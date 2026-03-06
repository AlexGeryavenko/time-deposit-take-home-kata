package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.domain.enums.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;

/**
 * Strategy for calculating monthly interest on a time deposit. Each implementation encapsulates the interest rate and eligibility
 * rules for a specific {@link PlanType}.
 */
public interface InterestStrategy {

  /**
   * @return the plan type this strategy handles
   */
  PlanType getPlanType();

  /**
   * Calculates the raw (unrounded) monthly interest for the given deposit. Returns 0 if the deposit is not yet eligible for
   * interest.
   *
   * @param deposit the time deposit to calculate interest for
   * @return raw interest amount (caller is responsible for rounding)
   */
  double calculateInterest(TimeDeposit deposit);

  /**
   * Checks whether this strategy supports the given plan type string.
   *
   * @param planType the plan type value to match (e.g. "basic", "student", "premium")
   * @return true if this strategy handles the given plan type
   */
  default boolean supports(String planType) {
    return getPlanType().getValue().equals(planType);
  }
}
