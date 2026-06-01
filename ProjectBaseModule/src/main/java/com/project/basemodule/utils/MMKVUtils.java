package com.project.basemodule.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MMKVUtils {

    private static final String MMKV_ID = "myapp_mmkv";
    private static MMKV mmkv;

    public static void init(Context context) {
        MMKV.initialize(context);
        mmkv = MMKV.mmkvWithID(MMKV_ID);
    }

    // ============ 基本数据类型存取 ============

    public static void put(String key, String value) {
        mmkv.encode(key, value);
    }

    public static String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    public static void put(String key, int value) {
        mmkv.encode(key, value);
    }

    public static int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    public static void put(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public static boolean getBoolean(String key) {
        return mmkv.decodeBool(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    public static void put(String key, long value) {
        mmkv.encode(key, value);
    }

    public static long getLong(String key) {
        return mmkv.decodeLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    public static void put(String key, float value) {
        mmkv.encode(key, value);
    }

    public static float getFloat(String key) {
        return mmkv.decodeFloat(key, 0f);
    }

    public static float getFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }

    public static void put(String key, double value) {
        mmkv.encode(key, value);
    }

    public static double getDouble(String key) {
        return mmkv.decodeDouble(key, 0.0);
    }

    public static double getDouble(String key, double defaultValue) {
        return mmkv.decodeDouble(key, defaultValue);
    }

    // ============ 对象存取（使用 Gson） ============

    private static final Gson gson = new Gson();

    public static <T> void putObject(String key, T obj) {
        String json = gson.toJson(obj);
        mmkv.encode(key, json);
    }

    public static <T> T getObject(String key, Class<T> clazz) {
        String json = mmkv.decodeString(key, null);
        return json == null ? null : gson.fromJson(json, clazz);
    }

    // ============ 集合存取 ============

    public static <T> void putList(String key, List<T> list) {
        String json = gson.toJson(list);
        mmkv.encode(key, json);
    }

    public static <T> List<T> getList(String key) {
        String json = mmkv.decodeString(key, null);
        if (json == null) return null;
        Type type = new TypeToken<List<T>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static <K, V> void putMap(String key, Map<K, V> map) {
        String json = gson.toJson(map);
        mmkv.encode(key, json);
    }

    public static <K, V> Map<K, V> getMap(String key) {
        String json = mmkv.decodeString(key, null);
        if (json == null) return null;
        Type type = new TypeToken<Map<K, V>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // ============ 其他操作 ============

    public static void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    public static void clearAll() {
        mmkv.clearAll();
    }

    public static boolean containsKey(String key) {
        return mmkv.containsKey(key);
    }
}

