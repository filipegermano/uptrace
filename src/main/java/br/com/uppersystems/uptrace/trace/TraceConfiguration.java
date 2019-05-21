package br.com.uppersystems.uptrace.trace;


import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.base.Enums;
import com.google.common.io.CharStreams;

import br.com.uppersystems.uptrace.util.RequestResponseUtils;
import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.composite.JsonWritingUtils;

/**
 * Class responsible for configuring the Trace.
 *
 * @author Filipe Germano
 */
@Configuration
@Slf4j
public class TraceConfiguration {

    @Value("${management.context-path:/actuator}")
    private String managerPath;

    @Value("${server.context-path:/}")
    private String serverPath;

    @Value("${uptrace.printAllTrace:true}")
    private Boolean printAllTrace;

    /**
     * {@inheritDoc}
     */
    public class TraceFilter implements Filter {

        @Override
        public void destroy() {
        }

        @Override
        public void init(FilterConfig arg0) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {


            HttpServletRequest r = (HttpServletRequest) request;
            HttpServletResponse response = (HttpServletResponse) res;
            Trace trace = TraceContextHolder.getInstance().init(printAllTrace, r);
            try {

                if (shouldDisableTrace(request)) {

                    trace.setShouldPrint(false);
                }
                
                HttpServletRequest requestHttp = (HttpServletRequest) request;
                Enumeration<String> headerNames = requestHttp.getHeaderNames();
                Map<String, String> map = RequestResponseUtils.convertToMap(headerNames, requestHttp);
                String body = CharStreams.toString(requestHttp.getReader());
                
                //ObjectMapper mapper = new ObjectMapper();
                //body = body.replace("\n", "").replace("\t", "").replace("\\", "");
                //String asString = mapper.writeValueAsString(body);
                
                JSONObject j = new JSONObject(body);

                RequestResponseParser requestTrace = new RequestResponseParser();
                requestTrace.setHeaders(map);
                requestTrace.setBody(j.toString());
                
                TraceContextHolder.getInstance().getActualTrace().setRequest(requestTrace);
                
                
                chain.doFilter(request, response);

            } catch (Exception e) {

                trace.setLevel(Level.ERROR.levelStr);
                StackTrace stackTrace = new StackTrace(e.getClass().getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                trace.setStackTrace(stackTrace);

                log.error("Error {} during request {} exception {}", e.getMessage(), ((HttpServletRequest) request).getRequestURL(), e);
                throw e;

            } finally {

                if (!ObjectUtils.isEmpty(trace) && trace.isShouldPrint()) {

                    trace.write(response);
                } else {
                    TraceContextHolder.getInstance().clearActual();
                }
                TraceContextHolder.getInstance().unset();

            }
        }


    }

    /**
     * Checks if it should disable the Trace from a c.
     *
     * @param request {@link ServletRequest}
     * @return True if trace should be disable, false otherwise
     */
    public boolean shouldDisableTrace(ServletRequest request) {

        String uri = ((HttpServletRequest) request).getRequestURI();
        return (uri.equalsIgnoreCase(serverPath) || uri.equalsIgnoreCase(serverPath + "/") || uri.startsWith(serverPath + managerPath) || uri.startsWith(managerPath));
    }

    /**
     * Configures and returns the {@link FilterRegistrationBean}.
     *
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {

        FilterRegistrationBean filtroRestAuth = new FilterRegistrationBean();
        filtroRestAuth.setFilter(new TraceFilter());
        filtroRestAuth.addUrlPatterns("/*");
        filtroRestAuth.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filtroRestAuth.setName("traceFilter");

        return filtroRestAuth;
    }

}
