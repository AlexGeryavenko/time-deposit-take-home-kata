package org.ikigaidigital.adapter.in.rest.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class DepositMetrics {

    private final Counter balanceUpdateCount;
    private final Timer balanceUpdateDuration;
    private final Counter interestTotal;
    private final Counter queryCount;
    private final Timer queryDuration;

    public DepositMetrics(MeterRegistry registry) {
        this.balanceUpdateCount = Counter.builder("deposit.balance_update.count")
            .description("Number of balance update operations")
            .register(registry);
        this.balanceUpdateDuration = Timer.builder("deposit.balance_update.duration")
            .description("Duration of balance update operations")
            .register(registry);
        this.interestTotal = Counter.builder("deposit.interest.total")
            .description("Total interest accrued across all deposits")
            .register(registry);
        this.queryCount = Counter.builder("deposit.query.count")
            .description("Number of deposit query operations")
            .register(registry);
        this.queryDuration = Timer.builder("deposit.query.duration")
            .description("Duration of deposit query operations")
            .register(registry);
    }

    public Counter getBalanceUpdateCount() {
        return balanceUpdateCount;
    }

    public Timer getBalanceUpdateDuration() {
        return balanceUpdateDuration;
    }

    public Counter getInterestTotal() {
        return interestTotal;
    }

    public Counter getQueryCount() {
        return queryCount;
    }

    public Timer getQueryDuration() {
        return queryDuration;
    }
}
