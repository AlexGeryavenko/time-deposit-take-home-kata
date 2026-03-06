package org.ikigaidigital.adapter.out.persistence.adapter;

import org.ikigaidigital.adapter.out.persistence.mapper.TimeDepositPersistenceMapper;
import org.ikigaidigital.adapter.out.persistence.repository.JpaTimeDepositRepository;
import org.ikigaidigital.adapter.out.persistence.repository.JpaWithdrawalRepository;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TimeDepositRepositoryAdapter implements TimeDepositRepository {

    private final JpaTimeDepositRepository jpaTimeDepositRepository;
    private final JpaWithdrawalRepository jpaWithdrawalRepository;
    private final TimeDepositPersistenceMapper mapper;

    public TimeDepositRepositoryAdapter(JpaTimeDepositRepository jpaTimeDepositRepository,
                                        JpaWithdrawalRepository jpaWithdrawalRepository,
                                        TimeDepositPersistenceMapper mapper) {
        this.jpaTimeDepositRepository = jpaTimeDepositRepository;
        this.jpaWithdrawalRepository = jpaWithdrawalRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TimeDeposit> findAll() {
        return List.of();
    }

    @Override
    public Page<TimeDeposit> findAll(Pageable pageable) {
        return Page.empty();
    }

    @Override
    public void updateBalance(int id, double balance) {
    }

    @Override
    public Map<Integer, List<Withdrawal>> findWithdrawalsGroupedByDepositId(List<Integer> depositIds) {
        return Map.of();
    }

    @Override
    public Map<Integer, List<Withdrawal>> findAllWithdrawalsGroupedByDepositId() {
        return Map.of();
    }
}
