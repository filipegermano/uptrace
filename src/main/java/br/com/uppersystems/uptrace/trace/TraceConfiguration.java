package br.com.uppersystems.uptrace.trace;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

import br.com.uppersystems.uptrace.util.RequestResponseUtils;
import br.com.uppersystems.uptrace.util.UpHttpServletResponseWrapper;
import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;

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
    
    @Value("${uptrace.printRequest:true}")
    private Boolean printRequest;

    @Value("${uptrace.printResponse:true}")
    private Boolean printResponse;
    
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
        public void doFilter(ServletRequest request, final ServletResponse response, FilterChain chain) throws IOException, ServletException {


            HttpServletRequest httpRequest = (HttpServletRequest) request;
            UpHttpServletResponseWrapper httpResponseWrapper = new UpHttpServletResponseWrapper((HttpServletResponse) response);
            Trace trace = TraceContextHolder.getInstance().init(printAllTrace, httpRequest);
            try {

                if (shouldDisableTrace(request)) {

                    trace.setShouldPrint(false);
                }

                if (printRequest) {
                	
                	RequestResponseParser requestParser = RequestResponseUtils.build(httpRequest);
                	TraceContextHolder.getInstance().getActualTrace().setRequest(requestParser);
                }
                
                if (printResponse) {
                	
                	
                	chain.doFilter(request, httpResponseWrapper);
                	httpResponseWrapper.flushBuffer();
                	
                	RequestResponseParser responseParser = RequestResponseUtils.build(httpResponseWrapper, new String(httpResponseWrapper.getCopy(), response.getCharacterEncoding()));
                    TraceContextHolder.getInstance().getActualTrace().setResponse(responseParser);
                } else {
                	
                	chain.doFilter(request, httpResponseWrapper);
                }

            } catch (Exception e) {

                trace.setLevel(Level.ERROR.levelStr);
                StackTrace stackTrace = new StackTrace(e.getClass().getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                trace.setStackTrace(stackTrace);

                log.error("Error {} during request {} exception {}", e.getMessage(), ((HttpServletRequest) request).getRequestURL(), e);
                throw e;

            } finally {
            	
                if (!ObjectUtils.isEmpty(trace) && trace.isShouldPrint()) {

                    trace.write(httpResponseWrapper);
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
