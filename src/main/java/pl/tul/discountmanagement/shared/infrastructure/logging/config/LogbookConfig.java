package pl.tul.discountmanagement.shared.infrastructure.logging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;

/**
 * Configuration class for Logbook library.
 */
@Configuration
@Slf4j
public class LogbookConfig {
    /**
     * Return {@link Logbook} instance that logs incoming requests and produced responses.
     * If log profile is set 'DEBUG' or 'TRACE', the logbook will log all the data.
     * Otherwise, body responses are included only for erroneous responses, and requests do not contain authorization
     * header values.
     *
     * @return {@link Logbook} instance that logs incoming requests and produced responses.
     */
    @Bean
    public Logbook logbook() {
        Strategy strategy = new BodyOnlyIfStatusAtLeastStrategy(HttpStatus.BAD_REQUEST.value());
        if (log.isDebugEnabled() || log.isTraceEnabled()) {
            strategy = new DefaultStrategy();
        }
        return Logbook.builder()
                .headerFilter(HeaderFilters.authorization())
                .sink(new DefaultSink(
                        new JsonHttpLogFormatter(),
                        new LogbookWriter()))
                .strategy(strategy)
                .build();
    }
}
