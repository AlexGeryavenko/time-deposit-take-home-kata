package org.ikigaidigital.adapter.out.persistence.adapter;

import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
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
import java.util.stream.Collectors;

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
        return mapper.toDomainList(jpaTimeDepositRepository.findAll());
    }

    @Override
    public Page<TimeDeposit> findAll(Pageable pageable) {
        return jpaTimeDepositRepository.findAll(pageable)
            .map(mapper::toDomain);
    }

    @Override
    public void updateBalance(int id, double balance) {
        jpaTimeDepositRepository.updateBalance(id, balance);
    }

    @Override
    public Map<Integer, List<Withdrawal>> findWithdrawalsGroupedByDepositId(List<Integer> depositIds) {
        return groupWithdrawalsByDepositId(jpaWithdrawalRepository.findByTimeDepositIdIn(depositIds));
    }

    @Override
    public Map<Integer, List<Withdrawal>> findAllWithdrawalsGroupedByDepositId() {
        return groupWithdrawalsByDepositId(jpaWithdrawalRepository.findAll());
    }

    private Map<Integer, List<Withdrawal>> groupWithdrawalsByDepositId(List<WithdrawalEntity> entities) {
        return entities.stream()
            .collect(Collectors.groupingBy(
                entity -> entity.getTimeDeposit().getId(),
                Collectors.mapping(mapper::toWithdrawalDomain, Collectors.toList())
            ));
    }
}
