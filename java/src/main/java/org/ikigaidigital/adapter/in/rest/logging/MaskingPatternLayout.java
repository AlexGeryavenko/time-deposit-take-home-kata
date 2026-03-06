package org.ikigaidigital.adapter.in.rest.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Pattern layout that masks sensitive data in log output.
 * Used for dev/console logging profile.
 */
public class MaskingPatternLayout extends PatternLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        String original = super.doLayout(event);
        return MaskingConverter.maskSensitiveData(original);
    }
}
