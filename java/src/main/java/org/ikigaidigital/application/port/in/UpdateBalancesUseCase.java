package org.ikigaidigital.application.port.in;

import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;

/**
 * Inbound port for recalculating interest on all time deposits.
 */
public interface UpdateBalancesUseCase {

  /**
   * Recalculates and persists interest for all deposits.
   *
   * @return the updated list of deposits with new balances
   */
  List<TimeDeposit> updateBalances();
}
