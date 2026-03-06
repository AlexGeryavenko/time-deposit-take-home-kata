package org.ikigaidigital.adapter.in.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.ikigaidigital.adapter.in.rest.generated.model.PagedTimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.generated.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.generated.model.WithdrawalResponse;
import org.ikigaidigital.adapter.in.rest.mapper.TimeDepositRestMapper;
import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TimeDepositQueryControllerTest {

  @Mock
  private GetDepositsUseCase getDepositsUseCase;

  @Mock
  private TimeDepositRestMapper mapper;

  @InjectMocks
  private TimeDepositQueryController controller;

  @Test
  void shouldReturnPaginatedDepositsWithWithdrawals() {
    TimeDeposit deposit = new TimeDeposit(1, "basic", 1000.00, 31);
    Page<TimeDeposit> page = new PageImpl<>(List.of(deposit),
        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id")), 1);
    Withdrawal withdrawal = new Withdrawal(1, 100.00, LocalDate.of(2024, 1, 15));
    WithdrawalResponse withdrawalResponse = new WithdrawalResponse()
        .id(1).amount(100.00).date(LocalDate.of(2024, 1, 15));
    TimeDepositResponse depositResponse = new TimeDepositResponse()
        .id(1).planType("basic").balance(1000.00).days(31)
        .withdrawals(List.of(withdrawalResponse));

    when(getDepositsUseCase.getAllDeposits(any())).thenReturn(page);
    when(getDepositsUseCase.getWithdrawalsByDepositId(List.of(1)))
        .thenReturn(Map.of(1, List.of(withdrawal)));
    when(mapper.toResponse(deposit)).thenReturn(new TimeDepositResponse()
        .id(1).planType("basic").balance(1000.00).days(31));
    when(mapper.toWithdrawalResponseList(List.of(withdrawal)))
        .thenReturn(List.of(withdrawalResponse));

    ResponseEntity<PagedTimeDepositResponse> result = controller.getTimeDeposits(0, 20, "id,asc");

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent()).hasSize(1);
    assertThat(result.getBody().getContent().get(0).getWithdrawals()).hasSize(1);
    assertThat(result.getBody().getTotalElements()).isEqualTo(1L);
    assertThat(result.getBody().getTotalPages()).isEqualTo(1);
  }

  @Test
  void shouldReturnEmptyPageWhenNoDeposits() {
    Page<TimeDeposit> emptyPage = Page.empty(PageRequest.of(0, 20));

    when(getDepositsUseCase.getAllDeposits(any())).thenReturn(emptyPage);

    ResponseEntity<PagedTimeDepositResponse> result = controller.getTimeDeposits(0, 20, "id,asc");

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent()).isEmpty();
  }
}
