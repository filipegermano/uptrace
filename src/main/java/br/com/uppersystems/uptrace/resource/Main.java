package br.com.uppersystems.uptrace.resource;

import org.json.JSONObject;

public class Main {
	
	public static void main(String[] args) {
		
		JSONObject j = new JSONObject("{\n\t\"name\": \"germano\"\n}");
		System.out.println(j.toString());
	}
	

}
