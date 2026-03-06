package org.ikigaidigital;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDepositCalculatorTest {

    private TimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TimeDepositCalculator();
    }

    @Test
    void shouldCalculateBasicInterestForEligibleDeposit() {
        List<TimeDeposit> deposits = Arrays.asList(
            new TimeDeposit(1, "basic", 1234567.00, 45)
        );

        calculator.updateBalance(deposits);

        assertThat(deposits.get(0).getBalance()).isEqualTo(1235595.81);
    }

    @Test
    void shouldNotAccrueInterestWhenDaysAtThreshold() {
        List<TimeDeposit> deposits = Arrays.asList(
            new TimeDeposit(1, "basic", 1000.00, 30)
        );

        calculator.updateBalance(deposits);

        assertThat(deposits.get(0).getBalance()).isEqualTo(1000.00);
    }

    @Test
    void shouldUpdateMultipleDepositsInSingleCall() {
        List<TimeDeposit> deposits = Arrays.asList(
            new TimeDeposit(1, "basic", 1000.00, 31),
            new TimeDeposit(2, "student", 1000.00, 31),
            new TimeDeposit(3, "premium", 1000.00, 46)
        );

        calculator.updateBalance(deposits);

        assertThat(deposits.get(0).getBalance()).isEqualTo(1000.83);
        assertThat(deposits.get(1).getBalance()).isEqualTo(1002.50);
        assertThat(deposits.get(2).getBalance()).isEqualTo(1004.17);
    }
}
