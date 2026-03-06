package org.ikigaidigital.adapter.in.rest.controller;

import org.ikigaidigital.adapter.in.rest.generated.api.TimeDepositCommandsApi;
import org.ikigaidigital.adapter.in.rest.generated.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.mapper.TimeDepositRestMapper;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TimeDepositCommandController implements TimeDepositCommandsApi {

    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final TimeDepositRestMapper mapper;

    public TimeDepositCommandController(UpdateBalancesUseCase updateBalancesUseCase,
                                        TimeDepositRestMapper mapper) {
        this.updateBalancesUseCase = updateBalancesUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<List<TimeDepositResponse>> updateBalances() {
        List<TimeDeposit> updated = updateBalancesUseCase.updateBalances();
        return ResponseEntity.ok(mapper.toResponseList(updated));
    }
}
