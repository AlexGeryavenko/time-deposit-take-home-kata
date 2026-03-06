package org.ikigaidigital.adapter.out.persistence.repository;

import java.util.List;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {

  List<WithdrawalEntity> findByTimeDepositIdIn(List<Integer> timeDepositIds);
}
