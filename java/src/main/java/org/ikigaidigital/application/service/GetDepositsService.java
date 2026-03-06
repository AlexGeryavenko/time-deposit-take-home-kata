package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GetDepositsService implements GetDepositsUseCase {

    private final TimeDepositRepository repository;

    public GetDepositsService(TimeDepositRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable("deposits")
    public Page<TimeDeposit> getAllDeposits(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Cacheable("withdrawals")
    public Map<Integer, List<Withdrawal>> getWithdrawalsByDepositId(List<Integer> depositIds) {
        return repository.findWithdrawalsGroupedByDepositId(depositIds);
    }
}
