package org.ikigaidigital.adapter.out.persistence.mapper;

import java.util.List;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimeDepositPersistenceMapper {

  TimeDeposit toDomain(TimeDepositEntity entity);

  List<TimeDeposit> toDomainList(List<TimeDepositEntity> entities);

  Withdrawal toWithdrawalDomain(WithdrawalEntity entity);

  List<Withdrawal> toWithdrawalDomainList(List<WithdrawalEntity> entities);
}
