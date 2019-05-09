package br.com.uppersystems.uptrace.trace;


import br.com.uppersystems.uptrace.util.UrlUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.pattern.PathPattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

import static net.logstash.logback.marker.Markers.append;

/**
 * Represents the trace message.
 *
 * @author Filipe Germano
 */
@Data
@Slf4j
public class Trace {

    private static final Logger logUp = LoggerFactory.getLogger("UP");

    @JsonIgnore
    public static final String NOME_TRACE = "trace";

    @JsonIgnore
    public static final String TAG_TRACE = "[UP-TRACE]";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime insertedOnDate = LocalDateTime.now();

    @JsonIgnore
    private Long initialTime;

    private Long durationMillis;

    private String verb;

    private String url;

    private String pattern;

    private String method;

    private int resultStatus;

    private String app;

    private String receivedFromAddress;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StackTrace stackTrace;

    @Getter
    private List<GeneralTrace> logs = Lists.newArrayList();

    @JsonIgnore
    private boolean printAllTrace;

    @JsonIgnore
    private String level;

    @JsonIgnore
    private boolean shouldPrint;

    public Trace() {

    }

    /**
     * Creates a Trace.
     *
     * @param printAllTrace boolean, should print all trace
     * @param request       String, profile
     */
    public Trace(boolean printAllTrace, HttpServletRequest request) {

        this.printAllTrace = printAllTrace;
        //ExceptionPIER.checkThrow(request == null ? true : false, ExceptionsMessagesPIEREnum.GLOBAL_REGISTRO_NAO_ENCONTRADO);

        this.shouldPrint = true;

        setInitialTime(System.currentTimeMillis());
        setVerb(request.getMethod());
        setUrl(UrlUtil.getCurrentUrl(request));

        Enumeration<String> headers = request.getHeaders("x-forwarded-for");
        if (ObjectUtils.isNotEmpty(headers)) {

            List<String> listaIPs = Lists.newArrayList();
            while (headers.hasMoreElements()) {
                String ip = (String) headers.nextElement();
                listaIPs.add(ip);
            }

            setReceivedFromAddress(Joiner.on(",").join(listaIPs.toArray()));

        }

    }

    /**
     * Creates and adds a new trace to the traces List.
     *
     * @param event Message to be added to the trace
     * @return {@link Trace} created
     */
    public Trace log(ILoggingEvent event) {

        logs.add(new GeneralTrace(event));

        return this;

    }

    /**
     * Writes a {@link HttpServletResponse} to the Pier Trace
     *
     * @param response {@link HttpServletResponse}
     */
    public void write(HttpServletResponse response) {

        try {

            setResultStatus(response.getStatus());
            setDurationMillis(System.currentTimeMillis() - getInitialTime());

            prepareLog(response.getStatus(), printAllTrace);

        } catch (Exception e) {

            log.error(e.getMessage(), e);
        } finally {

            TraceContextHolder.getInstance().clearActual();
        }

    }


    private void prepareLog(Integer statusCode, Boolean printAllTrace) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String url = "";
        if (ObjectUtils.isEmpty(getUrl())) {
            url = getUrl();
        }

        if (printAllTrace) {

            if (HttpStatus.valueOf(statusCode).is2xxSuccessful()) {

                this.level = Level.INFO.levelStr;
                logUp.info(append(NOME_TRACE, this), TAG_TRACE + " - {}", mapper.writeValueAsString(this));
            } else if (HttpStatus.valueOf(statusCode).is4xxClientError()) {

                this.level = Level.WARN.levelStr;
                logUp.warn(append(NOME_TRACE, this), TAG_TRACE + " - {}", mapper.writeValueAsString(this));
            } else {

                this.level = Level.ERROR.levelStr;
                logUp.error(append(NOME_TRACE, this), TAG_TRACE + " - {}", mapper.writeValueAsString(this));
            }
        } else {

            if (HttpStatus.valueOf(statusCode).is2xxSuccessful()) {

                this.level = Level.INFO.levelStr;
                logUp.info(append(NOME_TRACE, this), TAG_TRACE + " - " + url);
            } else if (HttpStatus.valueOf(statusCode).is4xxClientError()) {

                this.level = Level.WARN.levelStr;
                logUp.warn(append(NOME_TRACE, this), TAG_TRACE + " - " + url);
            } else {

                this.level = Level.ERROR.levelStr;
                logUp.error(append(NOME_TRACE, this), TAG_TRACE + " - " + url);
            }
        }

    }
}
