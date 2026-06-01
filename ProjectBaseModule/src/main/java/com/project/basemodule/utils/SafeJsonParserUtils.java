package com.project.basemodule.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SafeJsonParserUtils {
    public static JSONObject parseSafe(String jsonString) throws JSONException {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            if (e.getMessage().contains("Infinity") || e.getMessage().contains("NaN")) {
                // 尝试清理Infinity/NaN后重新解析
                String cleanedJson = cleanInfinityValues(jsonString);
                return new JSONObject(cleanedJson);
            }
            throw e;
        }
    }

    private static String cleanInfinityValues(String jsonString) {
        // 使用正则表达式匹配并替换Infinity/NaN
        String pattern = ":\\s*(-?Infinity|NaN)";
        return jsonString.replaceAll(pattern, ": \"$1\"");
    }

    public static double getSafeDouble(JSONObject json, String key, double defaultValue) {
        try {
            Object value = json.get(key);
            if (value instanceof String) {
                String strValue = (String) value;
                if ("Infinity".equals(strValue)) {
                    return Double.POSITIVE_INFINITY;
                } else if ("-Infinity".equals(strValue)) {
                    return Double.NEGATIVE_INFINITY;
                } else if ("NaN".equals(strValue)) {
                    return Double.NaN;
                }
            }
            return json.getDouble(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }
}
