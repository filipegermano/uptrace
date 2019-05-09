package br.com.uppersystems.uptrace.trace;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Data class that represents a General Trace
 *
 * @author Filipe Germano
 *
 */
@Data
public class GeneralTrace {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime ts;

    private String level;

    private String logger;

    private String thread;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stackTrace;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> mdc;

    private Object content;

    public GeneralTrace(ILoggingEvent e) {

        this.ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(e.getTimeStamp()), ZoneId.systemDefault());
        this.content = e.getFormattedMessage();
        this.level = e.getLevel().toString();
        this.logger = e.getLoggerName();
        this.thread = e.getThreadName();

        if (e.hasCallerData()) {
            StackTraceElement st = e.getCallerData()[0];
            String callerData = String.format("%s.%s:%d", st.getClassName(), st.getMethodName(), st.getLineNumber());
            this.stackTrace = callerData;
        }

        Map<String, String> mdc = e.getMDCPropertyMap();
        if (mdc != null && !mdc.isEmpty()) {
            this.mdc = mdc;
        }

    }
}
