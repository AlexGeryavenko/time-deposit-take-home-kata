package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBalancesServiceTest {

    @Mock
    private TimeDepositRepository repository;

    @Mock
    private TimeDepositCalculator calculator;

    @InjectMocks
    private UpdateBalancesService service;

    private List<TimeDeposit> deposits;

    @BeforeEach
    void setUp() {
        deposits = List.of(
            new TimeDeposit(1, "basic", 1000.00, 31),
            new TimeDeposit(2, "student", 2000.00, 365)
        );
    }

    @Test
    void shouldFetchAllDepositsFromRepository() {
        when(repository.findAll()).thenReturn(deposits);

        service.updateBalances();

        verify(repository).findAll();
    }

    @Test
    void shouldDelegateCalculationToCalculator() {
        when(repository.findAll()).thenReturn(deposits);

        service.updateBalances();

        ArgumentCaptor<List<TimeDeposit>> captor = ArgumentCaptor.forClass(List.class);
        verify(calculator).updateBalance(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void shouldPersistUpdatedBalancesForEachDeposit() {
        when(repository.findAll()).thenReturn(deposits);
        doAnswer(invocation -> {
            List<TimeDeposit> deps = invocation.getArgument(0);
            deps.get(0).setBalance(1000.83);
            deps.get(1).setBalance(2005.00);
            return null;
        }).when(calculator).updateBalance(anyList());

        service.updateBalances();

        verify(repository).updateBalance(1, 1000.83);
        verify(repository).updateBalance(2, 2005.00);
    }

    @Test
    void shouldReturnUpdatedDeposits() {
        when(repository.findAll()).thenReturn(deposits);

        List<TimeDeposit> result = service.updateBalances();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldHandleEmptyDepositList() {
        when(repository.findAll()).thenReturn(List.of());

        List<TimeDeposit> result = service.updateBalances();

        assertThat(result).isEmpty();
        verify(calculator).updateBalance(anyList());
        verify(repository, never()).updateBalance(anyInt(), anyDouble());
    }
}
