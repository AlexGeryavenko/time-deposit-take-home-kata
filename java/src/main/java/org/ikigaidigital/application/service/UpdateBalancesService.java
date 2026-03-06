package org.ikigaidigital.application.service;

import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.adapter.in.rest.config.DepositMetrics;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UpdateBalancesService implements UpdateBalancesUseCase {

    private final int batchSize;
    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;
    private final DepositMetrics metrics;

    public UpdateBalancesService(
            @Value("${api.batch.size:1000}") int batchSize,
            TimeDepositRepository repository,
            TimeDepositCalculator calculator,
            DepositMetrics metrics) {
        this.batchSize = batchSize;
        this.repository = repository;
        this.calculator = calculator;
        this.metrics = metrics;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deposits", "withdrawals"}, allEntries = true)
    public List<TimeDeposit> updateBalances() {
        return metrics.getBalanceUpdateDuration().record(() -> {
            metrics.getBalanceUpdateCount().increment();
            MDC.put("businessEvent", "update_balances_started");
            log.info("Balance update started");

            List<TimeDeposit> allUpdated = new ArrayList<>();
            int page = 0;
            double totalInterest = 0;
            Page<TimeDeposit> batch;

            do {
                batch = repository.findAll(PageRequest.of(page, batchSize));

                double balanceBefore = batch.getContent().stream()
                    .mapToDouble(TimeDeposit::getBalance)
                    .sum();

                calculator.updateBalance(batch.getContent());

                double balanceAfter = batch.getContent().stream()
                    .mapToDouble(TimeDeposit::getBalance)
                    .sum();

                double batchInterest = balanceAfter - balanceBefore;
                totalInterest += batchInterest;

                MDC.put("batchNumber", String.valueOf(page));
                MDC.put("batchSize", String.valueOf(batch.getNumberOfElements()));
                MDC.put("batchInterestAdded", String.valueOf(batchInterest));
                log.debug("Batch processed");

                for (TimeDeposit deposit : batch.getContent()) {
                    repository.updateBalance(deposit.getId(), deposit.getBalance());
                }

                allUpdated.addAll(batch.getContent());
                page++;
            } while (batch.hasNext());

            metrics.getInterestTotal().record(totalInterest);

            MDC.put("businessEvent", "update_balances_completed");
            MDC.put("totalDepositsProcessed", String.valueOf(allUpdated.size()));
            MDC.put("totalBatches", String.valueOf(page));
            log.info("Balance update completed");

            return allUpdated;
        });
    }
}
