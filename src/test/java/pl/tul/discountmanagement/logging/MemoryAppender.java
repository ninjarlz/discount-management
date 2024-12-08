package pl.tul.discountmanagement.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A custom appender that keeps logs in memory. Inherits from logback {@link AppenderBase}
 */
public class MemoryAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> list = new CopyOnWriteArrayList<>();

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        list.add(loggingEvent);
    }

    /**
     * Clears the log list
     */
    public void reset() {
        list.clear();
    }

    /**
     * Check if log list contains message for input level
     *
     * @param message the message
     * @param level   the log level
     * @return true if log lost contains message, otherwise false
     */
    public boolean contains(String message, Level level) {
        return list.stream()
                .anyMatch(event -> event.toString().contains(message)
                        && event.getLevel().equals(level));
    }
}
