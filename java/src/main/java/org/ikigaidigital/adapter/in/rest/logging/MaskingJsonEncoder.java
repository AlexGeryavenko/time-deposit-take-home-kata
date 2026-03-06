package org.ikigaidigital.adapter.in.rest.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.encoder.LogstashEncoder;

/**
 * JSON encoder that masks sensitive data before encoding. Extends LogstashEncoder to intercept and mask log messages.
 */
public class MaskingJsonEncoder extends LogstashEncoder {

  @Override
  public byte[] encode(ILoggingEvent event) {
    return super.encode(maskEvent(event));
  }

  private ILoggingEvent maskEvent(ILoggingEvent event) {
    String original = event.getFormattedMessage();
    String masked = MaskingConverter.maskSensitiveData(original);
    if (original.equals(masked)) {
      return event;
    }
    return new MaskedLoggingEvent(event, masked);
  }
}
