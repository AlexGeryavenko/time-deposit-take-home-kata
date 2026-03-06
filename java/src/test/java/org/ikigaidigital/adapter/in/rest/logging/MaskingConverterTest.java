package org.ikigaidigital.adapter.in.rest.logging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingConverterTest {

    @Test
    void shouldMaskApiKeyInHeader() {
        String input = "X-API-Key: my-secret-key-123";
        String result = MaskingConverter.maskSensitiveData(input);

        assertThat(result).isEqualTo("X-API-Key: ****");
        assertThat(result).doesNotContain("my-secret-key-123");
    }

    @Test
    void shouldMaskApiKeyWithEqualsSign() {
        String input = "X-API-Key=secret123";
        String result = MaskingConverter.maskSensitiveData(input);

        assertThat(result).isEqualTo("X-API-Key=****");
    }

    @Test
    void shouldMaskSqlBindParameters() {
        String input = "binding parameter [1] as [VARCHAR] - [sensitive-value]";
        String result = MaskingConverter.maskSensitiveData(input);

        assertThat(result).contains("****");
        assertThat(result).doesNotContain("sensitive-value");
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertThat(MaskingConverter.maskSensitiveData(null)).isNull();
    }

    @Test
    void shouldNotModifyMessageWithoutSensitiveData() {
        String input = "Normal log message without sensitive data";
        String result = MaskingConverter.maskSensitiveData(input);

        assertThat(result).isEqualTo(input);
    }
}
