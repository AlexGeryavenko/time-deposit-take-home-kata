package org.ikigaidigital.adapter.in.rest.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingJsonEncoderTest {

    private MaskingJsonEncoder encoder;
    private LoggerContext context;

    @BeforeEach
    void setUp() {
        context = (LoggerContext) LoggerFactory.getILoggerFactory();
        encoder = new MaskingJsonEncoder();
        encoder.setContext(context);
        encoder.start();
    }

    @Test
    void shouldMaskSensitiveDataInJsonOutput() {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext(context);
        event.setLoggerName("test");
        event.setMessage("X-API-Key: secret-key-value");
        event.setLevel(ch.qos.logback.classic.Level.INFO);
        event.setInstant(Instant.now());

        byte[] encoded = encoder.encode(event);
        String output = new String(encoded);

        assertThat(output).doesNotContain("secret-key-value");
        assertThat(output).contains("****");
    }

    @Test
    void shouldNotModifyNonSensitiveMessages() {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext(context);
        event.setLoggerName("test");
        event.setMessage("Normal message");
        event.setLevel(ch.qos.logback.classic.Level.INFO);
        event.setInstant(Instant.now());

        byte[] encoded = encoder.encode(event);
        String output = new String(encoded);

        assertThat(output).contains("Normal message");
    }
}
