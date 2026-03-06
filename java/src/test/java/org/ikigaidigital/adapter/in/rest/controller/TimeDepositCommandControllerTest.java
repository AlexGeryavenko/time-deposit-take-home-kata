package org.ikigaidigital.adapter.in.rest.controller;

import org.ikigaidigital.adapter.in.rest.generated.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.mapper.TimeDepositRestMapper;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeDepositCommandControllerTest {

    @Mock
    private UpdateBalancesUseCase updateBalancesUseCase;

    @Mock
    private TimeDepositRestMapper mapper;

    @InjectMocks
    private TimeDepositCommandController controller;

    @Test
    void shouldReturnOkWithUpdatedDeposits() {
        List<TimeDeposit> deposits = List.of(new TimeDeposit(1, "basic", 1000.83, 31));
        TimeDepositResponse response = new TimeDepositResponse()
            .id(1).planType("basic").balance(1000.83).days(31);

        when(updateBalancesUseCase.updateBalances()).thenReturn(deposits);
        when(mapper.toResponseList(deposits)).thenReturn(List.of(response));

        ResponseEntity<List<TimeDepositResponse>> result = controller.updateBalances();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getId()).isEqualTo(1);
        verify(updateBalancesUseCase).updateBalances();
    }

    @Test
    void shouldReturnEmptyListWhenNoDeposits() {
        when(updateBalancesUseCase.updateBalances()).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        ResponseEntity<List<TimeDepositResponse>> result = controller.updateBalances();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }
}
