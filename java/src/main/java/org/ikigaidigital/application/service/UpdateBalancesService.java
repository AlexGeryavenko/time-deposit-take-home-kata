package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateBalancesService implements UpdateBalancesUseCase {

    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public UpdateBalancesService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deposits", "withdrawals"}, allEntries = true)
    public List<TimeDeposit> updateBalances() {
        List<TimeDeposit> deposits = repository.findAll();

        calculator.updateBalance(deposits);

        for (TimeDeposit deposit : deposits) {
            repository.updateBalance(deposit.getId(), deposit.getBalance());
        }

        return deposits;
    }
}
