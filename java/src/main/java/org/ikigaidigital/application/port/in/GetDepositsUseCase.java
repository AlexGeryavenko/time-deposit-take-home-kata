package org.ikigaidigital.application.port.in;

import java.util.List;
import java.util.Map;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Inbound port for querying time deposits and their withdrawals.
 */
public interface GetDepositsUseCase {

  /**
   * Returns a paginated list of all deposits.
   *
   * @param pageable pagination parameters
   * @return page of deposits
   */
  Page<TimeDeposit> getAllDeposits(Pageable pageable);

  /**
   * Returns withdrawals grouped by deposit ID for the given deposit IDs.
   *
   * @param depositIds the deposit IDs to look up
   * @return map of deposit ID to list of withdrawals
   */
  Map<Integer, List<Withdrawal>> getWithdrawalsByDepositId(List<Integer> depositIds);
}
