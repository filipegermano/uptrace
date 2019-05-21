package br.com.uppersystems.uptrace.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestResponseUtils {
	
	public static Map<String, String> convertToMap(Enumeration<String> header, HttpServletRequest requestHttp) {
	    
		Map<String, String> map = new HashMap<String, String>();
	    while (header.hasMoreElements()){

	        String element = header.nextElement();
	        map.put(element.toString(), requestHttp.getHeader(element.toString()));
	    }
	    return map;
	}

}
