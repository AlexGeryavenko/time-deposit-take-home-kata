package org.ikigaidigital.adapter.in.rest.config;

import java.util.List;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.ikigaidigital.domain.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.strategy.InterestStrategy;
import org.ikigaidigital.domain.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.strategy.StudentInterestStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

  @Bean
  public List<InterestStrategy> interestStrategies() {
    return List.of(
        new BasicInterestStrategy(),
        new StudentInterestStrategy(),
        new PremiumInterestStrategy()
    );
  }

  @Bean
  public TimeDepositCalculator timeDepositCalculator(List<InterestStrategy> strategies) {
    return new TimeDepositCalculator(strategies);
  }
}
