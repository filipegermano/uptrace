package br.com.uppersystems.uptrace.trace;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides a custom appender service.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
@NoArgsConstructor
public class UpAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger logUpBackground = LoggerFactory.getLogger("UP_BACKGROUND");

    @Override
    public void start() {
        log.info("Starting Up appender");

        super.start();
        log.info("Started Up appender");
    }

    @Override
    public void stop() {
        log.info("Closing Up appender");
        super.stop();
        log.info("Closed Up appender");
    }

    @Override
    protected void append(ILoggingEvent event) {

        if (!"main".equalsIgnoreCase(event.getThreadName())) {

            if (!TraceContextHolder.getInstance().getActualTrace().isShouldPrint()) {

                GeneralTrace trace = new GeneralTrace(event);
                logUpBackground.info(Markers.append(Trace.NOME_TRACE, trace), "[UP-TRACE-BACKGROUND]");

            } else if (!"UP".equals(event.getLoggerName()) && !"UP_BACKGROUND".equals(event.getLoggerName())) {

                TraceContextHolder.getInstance().getActualTrace().log(event);
            }
        }

    }
}
