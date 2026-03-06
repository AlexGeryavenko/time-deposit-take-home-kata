package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;

import java.util.List;

public class UpdateBalancesService implements UpdateBalancesUseCase {

    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public UpdateBalancesService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Override
    public List<TimeDeposit> updateBalances() {
        return List.of();
    }
}
