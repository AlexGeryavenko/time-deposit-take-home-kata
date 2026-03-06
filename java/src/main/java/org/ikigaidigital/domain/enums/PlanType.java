package org.ikigaidigital.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanType {
  BASIC("basic"),
  STUDENT("student"),
  PREMIUM("premium");

  private final String value;
}
