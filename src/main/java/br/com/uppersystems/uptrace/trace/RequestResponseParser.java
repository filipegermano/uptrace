package br.com.uppersystems.uptrace.trace;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class RequestResponseParser implements Serializable {

	private static final long serialVersionUID = -4038412721353014184L;

	@JsonInclude(Include.NON_NULL)
	private Map<String, String> headers;

	@JsonInclude(Include.NON_NULL)
	private String body;

}
