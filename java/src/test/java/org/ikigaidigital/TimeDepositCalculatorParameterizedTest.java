package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TimeDepositCalculatorParameterizedTest {

  private final TimeDepositCalculator calculator = new TimeDepositCalculator();

  static Stream<Arguments> basicPlanCases() {
    return Stream.of(
        // No interest: days <= 30
        Arguments.of("basic", 1000.00, 0, 1000.00),
        Arguments.of("basic", 1000.00, 29, 1000.00),
        Arguments.of("basic", 1000.00, 30, 1000.00),
        // Interest: days > 30
        Arguments.of("basic", 1000.00, 31, 1000.83),
        Arguments.of("basic", 1000.00, 365, 1000.83),
        Arguments.of("basic", 1234567.00, 45, 1235595.81)
    );
  }

  static Stream<Arguments> studentPlanCases() {
    return Stream.of(
        // No interest: days <= 30
        Arguments.of("student", 1000.00, 0, 1000.00),
        Arguments.of("student", 1000.00, 30, 1000.00),
        // Interest: 30 < days < 366
        Arguments.of("student", 1000.00, 31, 1002.50),
        Arguments.of("student", 2000.00, 365, 2005.00),
        Arguments.of("student", 1234567.00, 45, 1237653.42),
        // No interest: days >= 366 (cap)
        Arguments.of("student", 2000.00, 366, 2000.00),
        Arguments.of("student", 1000.00, 400, 1000.00)
    );
  }

  static Stream<Arguments> premiumPlanCases() {
    return Stream.of(
        // No interest: days <= 30
        Arguments.of("premium", 1000.00, 0, 1000.00),
        Arguments.of("premium", 1000.00, 30, 1000.00),
        // No interest: 30 < days <= 45
        Arguments.of("premium", 1000.00, 31, 1000.00),
        Arguments.of("premium", 1000.00, 44, 1000.00),
        Arguments.of("premium", 1000.00, 45, 1000.00),
        // Interest: days > 45
        Arguments.of("premium", 1000.00, 46, 1004.17),
        Arguments.of("premium", 10000.00, 46, 10041.67),
        Arguments.of("premium", 1234567.00, 46, 1239711.03)
    );
  }

  static Stream<Arguments> edgeCases() {
    return Stream.of(
        // Zero balance
        Arguments.of("basic", 0.00, 31, 0.00),
        Arguments.of("student", 0.00, 31, 0.00),
        Arguments.of("premium", 0.00, 46, 0.00),
        // Large balance
        Arguments.of("basic", 9999999.99, 31, 10008333.32),
        Arguments.of("student", 9999999.99, 31, 10024999.99),
        Arguments.of("premium", 9999999.99, 46, 10041666.66)
    );
  }

  @ParameterizedTest(name = "{0} plan, days={2}, balance={1} → expected={3}")
  @MethodSource("basicPlanCases")
  void shouldCalculateBasicPlanInterest(String planType, double balance, int days, double expected) {
    assertSingleDeposit(planType, balance, days, expected);
  }

  @ParameterizedTest(name = "{0} plan, days={2}, balance={1} → expected={3}")
  @MethodSource("studentPlanCases")
  void shouldCalculateStudentPlanInterest(String planType, double balance, int days, double expected) {
    assertSingleDeposit(planType, balance, days, expected);
  }

  @ParameterizedTest(name = "{0} plan, days={2}, balance={1} → expected={3}")
  @MethodSource("premiumPlanCases")
  void shouldCalculatePremiumPlanInterest(String planType, double balance, int days, double expected) {
    assertSingleDeposit(planType, balance, days, expected);
  }

  @ParameterizedTest(name = "{0} plan, days={2}, balance={1} → expected={3}")
  @MethodSource("edgeCases")
  void shouldHandleEdgeCases(String planType, double balance, int days, double expected) {
    assertSingleDeposit(planType, balance, days, expected);
  }

  private void assertSingleDeposit(String planType, double balance, int days, double expected) {
    List<TimeDeposit> deposits = Arrays.asList(new TimeDeposit(1, planType, balance, days));
    calculator.updateBalance(deposits);
    assertThat(deposits.get(0).getBalance()).isEqualTo(expected);
  }
}
