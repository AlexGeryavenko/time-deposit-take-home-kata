package org.ikigaidigital.adapter.in.rest.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Pattern;

/**
 * Logback converter that masks sensitive data in log messages. Masks API keys (X-API-Key header values) and SQL bind parameters.
 */
public class MaskingConverter extends ClassicConverter {

  private static final Pattern API_KEY_PATTERN =
      Pattern.compile("(X-API-Key[:\\s=]+)([^\\s,;\"'}{]+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern SQL_BIND_PATTERN =
      Pattern.compile("(binding parameter \\[\\d+] as \\[\\w+] - \\[)([^]]+)(])", Pattern.CASE_INSENSITIVE);

  public static String maskSensitiveData(String message) {
    if (message == null) {
      return null;
    }
    String masked = API_KEY_PATTERN.matcher(message).replaceAll("$1****");
    masked = SQL_BIND_PATTERN.matcher(masked).replaceAll("$1****$3");
    return masked;
  }

  @Override
  public String convert(ILoggingEvent event) {
    return maskSensitiveData(event.getFormattedMessage());
  }
}
