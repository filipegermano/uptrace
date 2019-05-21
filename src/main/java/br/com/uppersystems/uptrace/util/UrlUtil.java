package br.com.uppersystems.uptrace.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.ObjectUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class provides methods to handle {@link URL}s.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
public class UrlUtil {

    /**
     * Returns the current URL from a {@link HttpServletRequest}.
     *
     * @param request The {@link HttpServletRequest}
     * @return The current URL as a {@link String}
     */
    public static String getCurrentUrl(HttpServletRequest request) {

        try {

            URL url = new URL(request.getRequestURL().toString());

            String query = request.getQueryString();
            if (!ObjectUtils.isEmpty(url) && !ObjectUtils.isEmpty(query)) {

                return url.toString() + "?" + query;
            } else {

                return url.toString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Transforms a {@link String} to a {@link URL}.
     *
     * @param target The String to be converted
     * @return The formed URL
     * @throws IllegalStateException
     */
    public static URL getUrl(String target) {

        try {
            return new URL(target);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Target URL is malformed", ex);
        }
    }
}
