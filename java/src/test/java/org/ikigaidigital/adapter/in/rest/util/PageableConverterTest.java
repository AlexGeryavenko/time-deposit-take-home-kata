package org.ikigaidigital.adapter.in.rest.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageableConverterTest {

    @Test
    void shouldConvertWithAllParameters() {
        Pageable pageable = PageableConverter.toPageable(1, 10, "balance,desc");

        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("balance")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("balance").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void shouldUseDefaultsWhenParametersAreNull() {
        Pageable pageable = PageableConverter.toPageable(null, null, null);

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void shouldDefaultToAscWhenDirectionNotProvided() {
        Pageable pageable = PageableConverter.toPageable(0, 10, "planType");

        assertThat(pageable.getSort().getOrderFor("planType")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("planType").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void shouldUseDefaultSortWhenSortIsBlank() {
        Pageable pageable = PageableConverter.toPageable(0, 10, "  ");

        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
    }
}
