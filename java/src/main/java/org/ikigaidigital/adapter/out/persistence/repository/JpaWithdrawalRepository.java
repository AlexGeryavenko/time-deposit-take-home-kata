package org.ikigaidigital.adapter.out.persistence.repository;

import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {

    List<WithdrawalEntity> findByTimeDepositIdIn(List<Integer> timeDepositIds);
}
