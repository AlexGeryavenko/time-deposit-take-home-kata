package org.ikigaidigital.adapter.out.persistence.repository;

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTimeDepositRepository extends JpaRepository<TimeDepositEntity, Integer> {

  @Modifying
  @Query("UPDATE TimeDepositEntity t SET t.balance = :balance WHERE t.id = :id")
  void updateBalance(@Param("id") int id, @Param("balance") double balance);
}
