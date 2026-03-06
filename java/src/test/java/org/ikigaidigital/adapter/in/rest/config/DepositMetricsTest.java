package org.ikigaidigital.adapter.in.rest.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepositMetricsTest {

  private DepositMetrics metrics;
  private SimpleMeterRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new SimpleMeterRegistry();
    metrics = new DepositMetrics(registry);
  }

  @Test
  void shouldRegisterBalanceUpdateCounter() {
    metrics.getBalanceUpdateCount().increment();

    assertThat(registry.counter("deposit.balance_update.count").count()).isEqualTo(1.0);
  }

  @Test
  void shouldRegisterBalanceUpdateTimer() {
    assertThat(metrics.getBalanceUpdateDuration()).isNotNull();
    assertThat(registry.timer("deposit.balance_update.duration")).isNotNull();
  }

  @Test
  void shouldRegisterInterestTotalSummary() {
    metrics.getInterestTotal().record(42.5);

    assertThat(metrics.getInterestTotal().count()).isEqualTo(1);
    assertThat(metrics.getInterestTotal().totalAmount()).isEqualTo(42.5);
  }

  @Test
  void shouldRegisterQueryCounter() {
    metrics.getQueryCount().increment();
    metrics.getQueryCount().increment();

    assertThat(registry.counter("deposit.query.count").count()).isEqualTo(2.0);
  }

  @Test
  void shouldRegisterQueryTimer() {
    assertThat(metrics.getQueryDuration()).isNotNull();
    assertThat(registry.timer("deposit.query.duration")).isNotNull();
  }
}
