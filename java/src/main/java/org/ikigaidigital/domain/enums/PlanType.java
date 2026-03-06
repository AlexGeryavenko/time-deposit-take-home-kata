package org.ikigaidigital.domain.enums;

public enum PlanType {

    BASIC("basic"),
    STUDENT("student"),
    PREMIUM("premium");

    private final String value;

    PlanType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
