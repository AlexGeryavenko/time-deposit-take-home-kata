package org.ikigaidigital.adapter.in.rest.controller;

import java.util.List;
import java.util.Map;
import org.ikigaidigital.adapter.in.rest.generated.api.TimeDepositQueriesApi;
import org.ikigaidigital.adapter.in.rest.generated.model.PagedTimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.generated.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.mapper.TimeDepositRestMapper;
import org.ikigaidigital.adapter.in.rest.util.PageableConverter;
import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeDepositQueryController implements TimeDepositQueriesApi {

  private final GetDepositsUseCase getDepositsUseCase;
  private final TimeDepositRestMapper mapper;

  public TimeDepositQueryController(GetDepositsUseCase getDepositsUseCase,
      TimeDepositRestMapper mapper) {
    this.getDepositsUseCase = getDepositsUseCase;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<PagedTimeDepositResponse> getTimeDeposits(Integer page, Integer size, String sort) {
    Pageable pageable = PageableConverter.toPageable(page, size, sort);
    Page<TimeDeposit> depositPage = getDepositsUseCase.getAllDeposits(pageable);

    List<Integer> depositIds = depositPage.getContent().stream()
        .map(TimeDeposit::getId)
        .toList();

    Map<Integer, List<Withdrawal>> withdrawalsMap = depositIds.isEmpty()
        ? Map.of()
        : getDepositsUseCase.getWithdrawalsByDepositId(depositIds);

    List<TimeDepositResponse> content = depositPage.getContent().stream()
        .map(deposit -> {
          TimeDepositResponse response = mapper.toResponse(deposit);
          List<Withdrawal> withdrawals = withdrawalsMap.getOrDefault(deposit.getId(), List.of());
          response.setWithdrawals(mapper.toWithdrawalResponseList(withdrawals));
          return response;
        })
        .toList();

    PagedTimeDepositResponse response = new PagedTimeDepositResponse()
        .content(content)
        .page(depositPage.getNumber())
        .size(depositPage.getSize())
        .totalElements(depositPage.getTotalElements())
        .totalPages(depositPage.getTotalPages());

    return ResponseEntity.ok(response);
  }
}
