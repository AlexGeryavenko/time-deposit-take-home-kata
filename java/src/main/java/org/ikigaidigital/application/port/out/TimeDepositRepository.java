package org.ikigaidigital.application.port.out;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Outbound port for time deposit persistence operations.
 */
public interface TimeDepositRepository {

    List<TimeDeposit> findAll();

    Page<TimeDeposit> findAll(Pageable pageable);

    void updateBalance(int id, double balance);

    Map<Integer, List<Withdrawal>> findWithdrawalsGroupedByDepositId(List<Integer> depositIds);

    Map<Integer, List<Withdrawal>> findAllWithdrawalsGroupedByDepositId();
}
