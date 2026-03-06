package org.ikigaidigital.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.ikigaidigital.adapter.in.rest.config.DepositMetrics;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetDepositsServiceTest {

  @Mock
  private TimeDepositRepository repository;

  private DepositMetrics metrics;
  private GetDepositsService service;

  @BeforeEach
  void setUp() {
    metrics = new DepositMetrics(new SimpleMeterRegistry());
    service = new GetDepositsService(repository, metrics);
  }

  @Test
  void shouldReturnPaginatedDeposits() {
    Pageable pageable = PageRequest.of(0, 10);
    List<TimeDeposit> deposits = List.of(
        new TimeDeposit(1, "basic", 1000.00, 31)
    );
    Page<TimeDeposit> page = new PageImpl<>(deposits, pageable, 1);
    when(repository.findAll(pageable)).thenReturn(page);

    Page<TimeDeposit> result = service.getAllDeposits(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(1);
    verify(repository).findAll(pageable);
  }

  @Test
  void shouldReturnEmptyPageWhenNoDeposits() {
    Pageable pageable = PageRequest.of(0, 10);
    when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

    Page<TimeDeposit> result = service.getAllDeposits(pageable);

    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldRecordMetrics() {
    Page<TimeDeposit> page = new PageImpl<>(List.of());
    Pageable pageable = PageRequest.of(0, 20);
    when(repository.findAll(pageable)).thenReturn(page);

    service.getAllDeposits(pageable);

    assertThat(metrics.getQueryCount().count()).isEqualTo(1.0);
  }

  @Test
  void shouldReturnWithdrawalsGroupedByDepositId() {
    List<Integer> depositIds = List.of(1, 2);
    Map<Integer, List<Withdrawal>> withdrawals = Map.of(
        1, List.of(new Withdrawal(1, 100.00, LocalDate.of(2024, 1, 15))),
        2, List.of(new Withdrawal(2, 200.00, LocalDate.of(2024, 2, 20)))
    );
    when(repository.findWithdrawalsGroupedByDepositId(depositIds)).thenReturn(withdrawals);

    Map<Integer, List<Withdrawal>> result = service.getWithdrawalsByDepositId(depositIds);

    assertThat(result).hasSize(2);
    assertThat(result.get(1)).hasSize(1);
    assertThat(result.get(1).get(0).getAmount()).isEqualTo(100.00);
    verify(repository).findWithdrawalsGroupedByDepositId(depositIds);
  }

  @Test
  void shouldReturnEmptyMapWhenNoWithdrawals() {
    List<Integer> depositIds = List.of(1);
    when(repository.findWithdrawalsGroupedByDepositId(depositIds)).thenReturn(Map.of());

    Map<Integer, List<Withdrawal>> result = service.getWithdrawalsByDepositId(depositIds);

    assertThat(result).isEmpty();
  }
}
