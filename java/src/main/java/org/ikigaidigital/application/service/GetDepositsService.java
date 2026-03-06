package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public class GetDepositsService implements GetDepositsUseCase {

    private final TimeDepositRepository repository;

    public GetDepositsService(TimeDepositRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TimeDeposit> getAllDeposits(Pageable pageable) {
        return Page.empty();
    }

    @Override
    public Map<Integer, List<Withdrawal>> getWithdrawalsByDepositId(List<Integer> depositIds) {
        return Map.of();
    }
}
