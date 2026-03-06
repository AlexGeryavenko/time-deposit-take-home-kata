package org.ikigaidigital.adapter.in.rest.mapper;

import org.ikigaidigital.adapter.in.rest.generated.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.generated.model.WithdrawalResponse;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeDepositRestMapper {

    TimeDepositResponse toResponse(TimeDeposit deposit);

    List<TimeDepositResponse> toResponseList(List<TimeDeposit> deposits);

    WithdrawalResponse toWithdrawalResponse(Withdrawal withdrawal);

    List<WithdrawalResponse> toWithdrawalResponseList(List<Withdrawal> withdrawals);
}
