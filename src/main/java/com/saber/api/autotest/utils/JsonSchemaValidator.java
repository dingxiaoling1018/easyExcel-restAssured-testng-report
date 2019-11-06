package com.saber.api.autotest.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import java.io.IOException;

/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/9/10 10:15
 * json格式校验工具类
 */
public class JsonSchemaValidator {

    public static JsonNode getJsonNodeFromString(String jsonStr) {
        JsonNode jsonNode = null;
        try {
            jsonNode = JsonLoader.fromString(jsonStr);
        } catch (IOException e) {
            return null;
        }
        return jsonNode;
    }
}
