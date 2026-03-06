package org.ikigaidigital.application.port.out;

import java.util.List;
import java.util.Map;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Outbound port for time deposit persistence.
 *
 * <p>Abstracts the data store from the domain layer, allowing the
 * persistence adapter to be swapped without affecting business logic.
 */
public interface TimeDepositRepository {

  /**
   * Returns all time deposits.
   *
   * @return an unordered list of all deposits
   */
  List<TimeDeposit> findAll();

  /**
   * Returns a paginated subset of time deposits.
   *
   * @param pageable pagination and sorting parameters
   * @return a page of deposits
   */
  Page<TimeDeposit> findAll(Pageable pageable);

  /**
   * Updates the balance of a single deposit.
   *
   * @param id      the deposit ID
   * @param balance the new balance value
   */
  void updateBalance(int id, double balance);

  /**
   * Returns withdrawals for the given deposit IDs, grouped by deposit ID.
   *
   * @param depositIds the deposit IDs to query
   * @return a map of deposit ID to its withdrawals; IDs with no withdrawals are absent
   */
  Map<Integer, List<Withdrawal>> findWithdrawalsGroupedByDepositId(List<Integer> depositIds);

  /**
   * Returns all withdrawals grouped by their parent deposit ID.
   *
   * @return a map of deposit ID to its withdrawals
   */
  Map<Integer, List<Withdrawal>> findAllWithdrawalsGroupedByDepositId();
}
