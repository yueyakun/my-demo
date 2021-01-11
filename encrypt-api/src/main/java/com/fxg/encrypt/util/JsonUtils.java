package com.fxg.encrypt.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class JsonUtils {

	private JsonUtils() {
	}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static JsonNode getNode(String content, String key) throws IOException {
		JsonNode jsonNode = OBJECT_MAPPER.readTree(content);
		return jsonNode.get(key);
	}

	public static JsonNode getNode(Object obj, String key) throws IOException {
		String content = OBJECT_MAPPER.writeValueAsString(obj);
		JsonNode jsonNode = OBJECT_MAPPER.readTree(content);
		return jsonNode.get(key);
	}

	public static String writeValueAsString(Object body) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(body);
	}

	public static HashMap<String, String> convertJsonStringToHashMap(String bodyString) throws JsonProcessingException {
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
		};
		return OBJECT_MAPPER.readValue(bodyString, typeRef);
	}
}
