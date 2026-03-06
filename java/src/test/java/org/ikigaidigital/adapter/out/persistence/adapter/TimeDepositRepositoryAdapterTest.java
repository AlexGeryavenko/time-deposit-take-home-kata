package org.ikigaidigital.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.out.persistence.mapper.TimeDepositPersistenceMapper;
import org.ikigaidigital.adapter.out.persistence.repository.JpaTimeDepositRepository;
import org.ikigaidigital.adapter.out.persistence.repository.JpaWithdrawalRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TimeDepositRepositoryAdapterTest {

  @Mock
  private JpaTimeDepositRepository jpaTimeDepositRepository;

  @Mock
  private JpaWithdrawalRepository jpaWithdrawalRepository;

  @Mock
  private TimeDepositPersistenceMapper mapper;

  @InjectMocks
  private TimeDepositRepositoryAdapter adapter;

  @Test
  void shouldFindAllDepositsAndMapToDomain() {
    TimeDepositEntity entity = new TimeDepositEntity(1, "basic", 31, 1000.00);
    List<TimeDepositEntity> entities = List.of(entity);
    List<TimeDeposit> expected = List.of(new TimeDeposit(1, "basic", 1000.00, 31));

    when(jpaTimeDepositRepository.findAll()).thenReturn(entities);
    when(mapper.toDomainList(entities)).thenReturn(expected);

    List<TimeDeposit> result = adapter.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1);
    verify(jpaTimeDepositRepository).findAll();
    verify(mapper).toDomainList(entities);
  }

  @Test
  void shouldFindAllDepositsPaginatedAndMapToDomain() {
    Pageable pageable = PageRequest.of(0, 10);
    TimeDepositEntity entity = new TimeDepositEntity(1, "basic", 31, 1000.00);
    Page<TimeDepositEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
    TimeDeposit domain = new TimeDeposit(1, "basic", 1000.00, 31);

    when(jpaTimeDepositRepository.findAll(pageable)).thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    Page<TimeDeposit> result = adapter.findAll(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(jpaTimeDepositRepository).findAll(pageable);
  }

  @Test
  void shouldDelegateUpdateBalanceToJpaRepository() {
    adapter.updateBalance(1, 1000.83);

    verify(jpaTimeDepositRepository).updateBalance(1, 1000.83);
  }

  @Test
  void shouldFindWithdrawalsGroupedByDepositId() {
    TimeDepositEntity depositEntity = new TimeDepositEntity(1, "basic", 31, 1000.00);
    WithdrawalEntity withdrawalEntity = new WithdrawalEntity(1, depositEntity, 100.00, LocalDate.of(2024, 1, 15));
    Withdrawal withdrawal = new Withdrawal(1, 100.00, LocalDate.of(2024, 1, 15));

    when(jpaWithdrawalRepository.findByTimeDepositIdIn(List.of(1))).thenReturn(List.of(withdrawalEntity));
    when(mapper.toWithdrawalDomain(withdrawalEntity)).thenReturn(withdrawal);

    Map<Integer, List<Withdrawal>> result = adapter.findWithdrawalsGroupedByDepositId(List.of(1));

    assertThat(result).containsKey(1);
    assertThat(result.get(1)).hasSize(1);
    assertThat(result.get(1).get(0).getAmount()).isEqualTo(100.00);
  }

  @Test
  void shouldReturnEmptyMapWhenNoWithdrawals() {
    when(jpaWithdrawalRepository.findByTimeDepositIdIn(List.of(1))).thenReturn(List.of());

    Map<Integer, List<Withdrawal>> result = adapter.findWithdrawalsGroupedByDepositId(List.of(1));

    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindAllWithdrawalsGroupedByDepositId() {
    TimeDepositEntity depositEntity = new TimeDepositEntity(1, "basic", 31, 1000.00);
    WithdrawalEntity withdrawalEntity = new WithdrawalEntity(1, depositEntity, 50.00, LocalDate.of(2024, 3, 10));
    Withdrawal withdrawal = new Withdrawal(1, 50.00, LocalDate.of(2024, 3, 10));

    when(jpaWithdrawalRepository.findAll()).thenReturn(List.of(withdrawalEntity));
    when(mapper.toWithdrawalDomain(withdrawalEntity)).thenReturn(withdrawal);

    Map<Integer, List<Withdrawal>> result = adapter.findAllWithdrawalsGroupedByDepositId();

    assertThat(result).containsKey(1);
    assertThat(result.get(1)).hasSize(1);
  }
}
