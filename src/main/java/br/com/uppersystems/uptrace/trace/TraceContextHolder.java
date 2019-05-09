package br.com.uppersystems.uptrace.trace;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides thread safe methods to manage the Trace.
 *
 * @author Filipe Germano
 */
@Slf4j
public class TraceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    private static final ConcurrentHashMap<String, Trace> traceMap = new ConcurrentHashMap<>();

    /**
     * Implementation of the Initialization-on-demand holder idiom.
     */
    private static class LazyHolder {
        static final TraceContextHolder INSTANCE = new TraceContextHolder();
    }

    /**
     * Thread safe singleton initializer.
     *
     * @return {@link TraceContextHolder} instance
     */
    public static TraceContextHolder getInstance() {

        return LazyHolder.INSTANCE;
    }

    /**
     * Initializes a {@link Trace}
     *
     * @param printAllTrace boolean
     * @param request       {@link HttpServletRequest}
     * @return {@link Trace}
     */
    public Trace init(boolean printAllTrace, HttpServletRequest request) {
        String uuid = UUID.randomUUID().toString();
        contextHolder.set(uuid);
        traceMap.put(uuid, new Trace(printAllTrace, request));

        log.debug("Initializing TraceContext with ID: {}", uuid);
        return getActualTrace();

    }

    /**
     * Returns the actual {@link Trace}.
     *
     * @return {@link Trace}, null if the context is null
     */
    public Trace getActualTrace() {

        if (contextHolder.get() == null) {

            return null;
        } else {

            return traceMap.get(contextHolder.get());
        }
    }

    /**
     * Clears actual trace.
     */
    public void clearActual() {

        if (contextHolder.get() != null) {

            traceMap.remove(contextHolder.get());
        }
    }

    /**
     * Remove current value.
     */
    public void unset() {
        contextHolder.remove();
    }
}
