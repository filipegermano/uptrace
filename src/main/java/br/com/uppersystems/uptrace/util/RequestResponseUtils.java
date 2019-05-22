package br.com.uppersystems.uptrace.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.google.common.io.CharStreams;

import br.com.uppersystems.uptrace.trace.RequestResponseParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestResponseUtils {

	public static RequestResponseParser build(HttpServletRequest httpRequest) {

		RequestResponseParser request = null;
		try {

			Enumeration<String> headerNames = httpRequest.getHeaderNames();
			Map<String, String> map = convertToMap(headerNames, httpRequest);
			String body = CharStreams.toString(httpRequest.getReader());

			request = new RequestResponseParser();
			request.setHeaders(map);
			if (!StringUtils.isEmpty(body)) {

				JSONObject j = new JSONObject(body);
				request.setBody(j.toString());
			}

		} catch (Exception e) {

			log.error("Error {} during request build request exception {}", e.getMessage(), e);
		}

		return request;
	}

	public static RequestResponseParser build(HttpServletResponse httpResponse, String body) {

		RequestResponseParser response = null;
		try {

			Collection<String> names = httpResponse.getHeaderNames();
			for (String string : names) {
				System.out.println(string);
			}
			
			Enumeration<String> headerNames = Collections.enumeration(httpResponse.getHeaderNames());
			Map<String, String> map = convertToMap(headerNames, httpResponse);

			response = new RequestResponseParser();
			response.setHeaders(map);
//			if (!StringUtils.isEmpty(body)) {
//
//				JSONObject j = new JSONObject(body);
//				response.setBody(j.toString());
//			}
			
			response.setBody(body);
		} catch (Exception e) {

			log.error("Error {} during request build request exception {}", e.getMessage(), e);
		}

		return response;

	}

	private static Map<String, String> convertToMap(Enumeration<String> header, HttpServletRequest httpRequest) {

		Map<String, String> map = new HashMap<String, String>();
		while (header.hasMoreElements()) {

			String element = header.nextElement();
			map.put(element.toString(), httpRequest.getHeader(element.toString()));
		}
		return map;
	}

	private static Map<String, String> convertToMap(Enumeration<String> header, HttpServletResponse httpResponse) {

		Map<String, String> map = new HashMap<String, String>();
		while (header.hasMoreElements()) {

			String element = header.nextElement();
			map.put(element.toString(), httpResponse.getHeader(element.toString()));
		}
		return map;
	}

}
