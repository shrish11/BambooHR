package com.freshworks.bambbohr.common.util;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class JsonUtil {

    public static final ObjectMapper jsonMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public static final TypeFactory typeFactory = jsonMapper.getTypeFactory();

    public static final JsonFactory jsonFactory = jsonMapper.getFactory();


    public static <T> T parseAsObject(final Object input,
                                      final TypeReference<T> targetType) throws IOException {
        return new JsonParseAsObjectHelper<T>(targetType).readValue(input);
    }

    public static <T> T parseAsObject(final Object input,
                                      final Class<T> targetInstance) throws IOException {
        return new JsonParseAsObjectHelper<T>(targetInstance).readValue(input);
    }

    public static <T> List<T> parseAsObjects(final Object input,
                                             final Class<T> targetInstance) throws IOException {
        JavaType listType = typeFactory.constructCollectionType(List.class, targetInstance);
        return new JsonParseAsObjectHelper<List<T>>(listType).readValue(input);
    }

    public static JsonNode parseAsJsonNode(final Object input) throws JsonProcessingException {
        if (input instanceof String) {
            return jsonMapper.readTree((String) input);
        }
        return jsonMapper.readTree(toJsonString(input));
    }

    public static JSONArray parseAsJsonArray(final Object input) {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONParser jsonParser = new JSONParser();
            if (input instanceof String) {
                jsonArray = (JSONArray) jsonParser.parse((String) input);
            } else {
                jsonArray = (JSONArray) jsonParser.parse(toJsonString(input));
            }
        } catch (ParseException e) {
            List<Map<String, Object>> objectMap = jsonMapper.convertValue(input,
                    typeFactory.constructArrayType(
                            typeFactory.constructMapType(Map.class, String.class, Object.class))
            );
            jsonArray.addAll(objectMap);
        }

        return jsonArray;
    }

    private static final class JsonParseAsObjectHelper<T> {
        private final ObjectReader reader;

        private JsonParseAsObjectHelper(ObjectReader reader) {
            this.reader = reader;
        }

        private JsonParseAsObjectHelper(Class<T> target) {
            this(jsonMapper.readerFor(target));
        }

        private JsonParseAsObjectHelper(TypeReference<T> target) {
            this(jsonMapper.readerFor(target));
        }

        private JsonParseAsObjectHelper(JavaType target) {
            this(jsonMapper.readerFor(target));
        }

        /**
         * Parses the input with the provided {@link ObjectReader}.
         * For all input types other than String
         * are converted to equivalent json string before processing.
         *
         * <br />
         * This is for returning a single target instance type object.
         *
         * @param input to be processed and converted to the target instance type
         * @return target instance type with the parsed values
         * @throws IOException if input json is invalid or failed during parsing the json
         */
        private T readValue(final Object input)
                throws IOException {

            if (input instanceof String) {
                return reader.readValue(jsonFactory.createParser((String) input));
            } else if (input instanceof JsonNode) {
                return reader.readValue((JsonNode) input);
            } else if (input instanceof JSONObject) {
                String jsonInput = ((JSONObject) input).toJSONString();
                return reader.readValue(jsonInput);
            } else if (input instanceof JSONArray) {
                String jsonInput = ((JSONArray) input).toJSONString();
                return reader.readValue(jsonInput);
            } else {
                String jsonInput = toJsonString(input);
                return reader.readValue(jsonInput);
            }
        }
    }

    public static String toJsonString(final Object input) {
        final String methodName = "toJSONString";

        try {
            return jsonMapper.writer().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            log.error(input.toString(), e);
            return null;
        }
    }

    public static Map<String, Object> flattenAsMap(final Object input) {
        String json;
        if (input instanceof String) {
            json = (String) input;
        } else {
            json = Objects.requireNonNull(toJsonString(input));
        }
        return JsonFlattener.flattenAsMap(json);
    }

}
