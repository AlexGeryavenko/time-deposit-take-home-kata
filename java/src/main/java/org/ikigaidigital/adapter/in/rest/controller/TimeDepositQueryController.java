package org.ikigaidigital.adapter.in.rest.controller;

import org.ikigaidigital.adapter.in.rest.generated.api.TimeDepositQueriesApi;
import org.ikigaidigital.adapter.in.rest.generated.model.PagedTimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.mapper.TimeDepositRestMapper;
import org.ikigaidigital.application.port.in.GetDepositsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeDepositQueryController implements TimeDepositQueriesApi {

    private final GetDepositsUseCase getDepositsUseCase;
    private final TimeDepositRestMapper mapper;

    public TimeDepositQueryController(GetDepositsUseCase getDepositsUseCase,
                                      TimeDepositRestMapper mapper) {
        this.getDepositsUseCase = getDepositsUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<PagedTimeDepositResponse> getTimeDeposits(Integer page, Integer size, String sort) {
        return ResponseEntity.ok(new PagedTimeDepositResponse());
    }
}
