package org.ikigaidigital.application.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.adapter.in.rest.config.DepositMetrics;
import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDepositsService implements GetDepositsUseCase {

  private final TimeDepositRepository repository;
  private final DepositMetrics metrics;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "deposits", key = "#pageable")
  public Page<TimeDeposit> getAllDeposits(Pageable pageable) {
    return metrics.getQueryDuration().record(() -> {
      metrics.getQueryCount().increment();
      MDC.put("businessEvent", "query_deposits");
      MDC.put("queryPageSize", String.valueOf(pageable.getPageSize()));
      MDC.put("queryPageNumber", String.valueOf(pageable.getPageNumber()));
      if (pageable.getSort().isSorted()) {
        MDC.put("querySort", pageable.getSort().toString());
      }

      Page<TimeDeposit> result = repository.findAll(pageable);

      MDC.put("queryResultCount", String.valueOf(result.getNumberOfElements()));
      log.info("Deposit query executed");

      return result;
    });
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "withdrawals", key = "#depositIds")
  public Map<Integer, List<Withdrawal>> getWithdrawalsByDepositId(List<Integer> depositIds) {
    return repository.findWithdrawalsGroupedByDepositId(depositIds);
  }
}
