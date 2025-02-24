package com.freshworks.bambbohr.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class CommonUtil {

    public static void flattenMap(String prefix, Map<String, Object> nestedMap, Map<String, String> flatMap) throws JsonProcessingException {

        for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Recursive call for nested Map
                flattenMap(key, (Map<String, Object>) value, flatMap);
            } else if (value instanceof List) {
                // Handle lists (e.g., custom objects)
                List<?> list = (List<?>) value;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    // If the list contains maps (custom objects)
                    for (int i = 0; i < list.size(); i++) {
                        flattenMap(key + "[" + i + "]", (Map<String, Object>) list.get(i), flatMap);
                    }
                } else {
                    // Serialize list to JSON string for flat representation
                    flatMap.put(key, JsonUtil.toJsonString(list));
                }
            }else if (!(value instanceof String || value instanceof Number || value instanceof Boolean || value == null)) {
                // Serialize custom objects to JSON string
                flatMap.put(key, JsonUtil.toJsonString(value));
            }  else if (value == null) {
                // Handle null values
                flatMap.put(key, "");
            } else {
                // Add simple key-value pairs
                flatMap.put(key, value.toString());
            }
        }
    }

    public static Map<String, String> convertMap(Map<String, Object> inputMap) throws Exception {

//        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> outputMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Convert value to JSON string
            if (value == null) {
                outputMap.put(key, "null");
            } else {
//                JsonNode jsonNode = jsonMapper.valueToTree(value);
                JsonNode jsonNode = JsonUtil.parseAsJsonNode(value);
                outputMap.put(key, jsonNode.toString());
            }
        }

        return outputMap;
    }

    public static Map<String, String> mergeMaps(Map<String, String> map1, Map<String, String> map2) {
        Map<String, String> mergedMap = new HashMap<>();

        // Use AtomicInteger for suffix generation
        AtomicInteger suffixCounter = new AtomicInteger(1);

        // Combine both maps into a single stream and handle duplicate keys
        Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    // Ensure unique keys by appending a suffix if the key already exists
                    while (mergedMap.containsKey(key)) {
                        key = entry.getKey() + "_" + suffixCounter.getAndIncrement();
                    }

                    mergedMap.put(key, value);
                });

        return mergedMap;
    }

}
