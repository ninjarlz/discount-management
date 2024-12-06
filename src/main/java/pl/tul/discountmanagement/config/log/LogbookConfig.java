package pl.tul.discountmanagement.config.log;

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

@Configuration
@Slf4j
public class LogbookConfig {
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
