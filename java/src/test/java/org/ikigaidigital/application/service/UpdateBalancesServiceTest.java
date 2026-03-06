package org.ikigaidigital.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Collections;
import java.util.List;
import org.ikigaidigital.adapter.in.rest.config.DepositMetrics;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UpdateBalancesServiceTest {

  @Mock
  private TimeDepositRepository repository;

  private TimeDepositCalculator calculator;
  private DepositMetrics metrics;
  private UpdateBalancesService service;

  @BeforeEach
  void setUp() {
    calculator = new TimeDepositCalculator();
    metrics = new DepositMetrics(new SimpleMeterRegistry());
    service = new UpdateBalancesService(1000, repository, calculator, metrics);
  }

  @Test
  void shouldReturnEmptyListWhenNoDeposits() {
    Page<TimeDeposit> emptyPage = new PageImpl<>(Collections.emptyList());
    when(repository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

    List<TimeDeposit> result = service.updateBalances();

    assertThat(result).isEmpty();
    verify(repository, never()).updateBalance(anyInt(), anyDouble());
  }

  @Test
  void shouldProcessSingleBatch() {
    TimeDeposit deposit = new TimeDeposit(1, "basic", 1000.0, 45);
    Page<TimeDeposit> page = new PageImpl<>(List.of(deposit));
    when(repository.findAll(any(PageRequest.class))).thenReturn(page);

    List<TimeDeposit> result = service.updateBalances();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getBalance()).isGreaterThan(1000.0);
    verify(repository).updateBalance(eq(1), anyDouble());
  }

  @Test
  void shouldRecordMetrics() {
    Page<TimeDeposit> emptyPage = new PageImpl<>(Collections.emptyList());
    when(repository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

    service.updateBalances();

    assertThat(metrics.getBalanceUpdateCount().count()).isEqualTo(1.0);
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldProcessMultipleBatches() {
    TimeDeposit d1 = new TimeDeposit(1, "basic", 1000.0, 45);
    TimeDeposit d2 = new TimeDeposit(2, "student", 2000.0, 100);

    Page<TimeDeposit> firstPage = mock(Page.class);
    when(firstPage.getContent()).thenReturn(List.of(d1));
    when(firstPage.hasNext()).thenReturn(true);
    when(firstPage.getNumberOfElements()).thenReturn(1);

    Page<TimeDeposit> secondPage = mock(Page.class);
    when(secondPage.getContent()).thenReturn(List.of(d2));
    when(secondPage.hasNext()).thenReturn(false);
    when(secondPage.getNumberOfElements()).thenReturn(1);

    when(repository.findAll(PageRequest.of(0, 1000))).thenReturn(firstPage);
    when(repository.findAll(PageRequest.of(1, 1000))).thenReturn(secondPage);

    List<TimeDeposit> result = service.updateBalances();

    assertThat(result).hasSize(2);
    verify(repository, times(2)).updateBalance(anyInt(), anyDouble());
  }
}
