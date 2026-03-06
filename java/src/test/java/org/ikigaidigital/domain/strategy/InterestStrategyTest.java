package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.enums.PlanType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class InterestStrategyTest {

    @Nested
    class BasicInterestStrategyTest {

        private final BasicInterestStrategy strategy = new BasicInterestStrategy();

        @Test
        void shouldReturnBasicPlanType() {
            assertThat(strategy.getPlanType()).isEqualTo(PlanType.BASIC);
        }

        @Test
        void shouldSupportBasicPlanTypeString() {
            assertThat(strategy.supports("basic")).isTrue();
            assertThat(strategy.supports("student")).isFalse();
        }

        @Test
        void shouldReturnZeroInterestWhenDaysAtOrBelowThreshold() {
            assertThat(strategy.calculateInterest(deposit(1000.00, 0))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 29))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 30))).isEqualTo(0.0);
        }

        @Test
        void shouldCalculateInterestWhenDaysAboveThreshold() {
            // 1000 * 0.01 / 12 = 0.8333...
            assertThat(strategy.calculateInterest(deposit(1000.00, 31)))
                .isCloseTo(0.8333, within(0.001));
        }

        @Test
        void shouldCalculateInterestForLargeBalance() {
            // 1234567 * 0.01 / 12 = 1028.80583...
            assertThat(strategy.calculateInterest(deposit(1234567.00, 45)))
                .isCloseTo(1028.8058, within(0.001));
        }

        @Test
        void shouldReturnZeroInterestForZeroBalance() {
            assertThat(strategy.calculateInterest(deposit(0.00, 31))).isEqualTo(0.0);
        }
    }

    @Nested
    class StudentInterestStrategyTest {

        private final StudentInterestStrategy strategy = new StudentInterestStrategy();

        @Test
        void shouldReturnStudentPlanType() {
            assertThat(strategy.getPlanType()).isEqualTo(PlanType.STUDENT);
        }

        @Test
        void shouldSupportStudentPlanTypeString() {
            assertThat(strategy.supports("student")).isTrue();
            assertThat(strategy.supports("basic")).isFalse();
        }

        @Test
        void shouldReturnZeroInterestWhenDaysAtOrBelowThreshold() {
            assertThat(strategy.calculateInterest(deposit(1000.00, 0))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 30))).isEqualTo(0.0);
        }

        @Test
        void shouldCalculateInterestWhenDaysInEligibleRange() {
            // 1000 * 0.03 / 12 = 2.5
            assertThat(strategy.calculateInterest(deposit(1000.00, 31))).isEqualTo(2.5);
            // 2000 * 0.03 / 12 = 5.0
            assertThat(strategy.calculateInterest(deposit(2000.00, 365))).isEqualTo(5.0);
        }

        @Test
        void shouldReturnZeroInterestWhenDaysAtOrAboveCap() {
            assertThat(strategy.calculateInterest(deposit(2000.00, 366))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 400))).isEqualTo(0.0);
        }

        @Test
        void shouldReturnZeroInterestForZeroBalance() {
            assertThat(strategy.calculateInterest(deposit(0.00, 31))).isEqualTo(0.0);
        }
    }

    @Nested
    class PremiumInterestStrategyTest {

        private final PremiumInterestStrategy strategy = new PremiumInterestStrategy();

        @Test
        void shouldReturnPremiumPlanType() {
            assertThat(strategy.getPlanType()).isEqualTo(PlanType.PREMIUM);
        }

        @Test
        void shouldSupportPremiumPlanTypeString() {
            assertThat(strategy.supports("premium")).isTrue();
            assertThat(strategy.supports("basic")).isFalse();
        }

        @Test
        void shouldReturnZeroInterestWhenDaysAtOrBelowThreshold() {
            assertThat(strategy.calculateInterest(deposit(1000.00, 0))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 30))).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(deposit(1000.00, 45))).isEqualTo(0.0);
        }

        @Test
        void shouldCalculateInterestWhenDaysAboveThreshold() {
            // 1000 * 0.05 / 12 = 4.1666...
            assertThat(strategy.calculateInterest(deposit(1000.00, 46)))
                .isCloseTo(4.1667, within(0.001));
        }

        @Test
        void shouldCalculateInterestForLargeBalance() {
            // 1234567 * 0.05 / 12 = 5144.02916...
            assertThat(strategy.calculateInterest(deposit(1234567.00, 46)))
                .isCloseTo(5144.0292, within(0.001));
        }

        @Test
        void shouldReturnZeroInterestForZeroBalance() {
            assertThat(strategy.calculateInterest(deposit(0.00, 46))).isEqualTo(0.0);
        }
    }

    private static TimeDeposit deposit(double balance, int days) {
        return new TimeDeposit(1, "any", balance, days);
    }
}
