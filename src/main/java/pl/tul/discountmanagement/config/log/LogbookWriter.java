package pl.tul.discountmanagement.config.log;

import lombok.extern.slf4j.Slf4j;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

/**
 * Implementation of Logbook {@link HttpLogWriter}.
 */
@Slf4j
public class LogbookWriter implements HttpLogWriter {

    @Override
    public boolean isActive() {
        return log.isInfoEnabled();
    }

    @Override
    public void write(Precorrelation precorrelation, String request) {
        log.info(request);
    }

    @Override
    public void write(Correlation correlation, String response) {
        log.info(response);
    }
}
