package org.ikigaidigital.adapter.in.rest.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingPatternLayoutTest {

    private MaskingPatternLayout layout;

    @BeforeEach
    void setUp() {
        layout = new MaskingPatternLayout();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        layout.setContext(context);
        layout.setPattern("%msg%n");
        layout.start();
    }

    @Test
    void shouldMaskApiKeyInPatternOutput() {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext((LoggerContext) LoggerFactory.getILoggerFactory());
        event.setMessage("X-API-Key: my-secret");
        event.setLevel(ch.qos.logback.classic.Level.INFO);

        String output = layout.doLayout(event);

        assertThat(output).doesNotContain("my-secret");
        assertThat(output).contains("****");
    }

    @Test
    void shouldNotModifyNonSensitiveMessages() {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext((LoggerContext) LoggerFactory.getILoggerFactory());
        event.setMessage("Normal log message");
        event.setLevel(ch.qos.logback.classic.Level.INFO);

        String output = layout.doLayout(event);

        assertThat(output).contains("Normal log message");
    }
}
