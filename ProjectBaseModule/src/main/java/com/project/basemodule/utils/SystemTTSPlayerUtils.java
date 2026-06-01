package com.project.basemodule.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SystemTTSPlayerUtils {

    private static final String TAG = "SystemTTSPlayer";

    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;

    public SystemTTSPlayerUtils(Context context) {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.CHINESE); // 可改为 Locale.US 等
                textToSpeech.setPitch(0.9f);//音调
                textToSpeech.setSpeechRate(1.2f);//语速
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "语言不支持");
                } else {
                    isInitialized = true;
                }
            } else {
                Log.e(TAG, "TTS 初始化失败");
            }
        });
    }

    /**
     * 播放指定文本
     */
    public void speak(String text) {
        if (isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_" + System.currentTimeMillis());
        } else {
            Log.w(TAG, "TTS 未初始化完成，无法播报");
        }
    }

    public void speak(String text, boolean flush) {
        if (isInitialized) {
            int queueMode = flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
            textToSpeech.speak(text, queueMode, null, "tts_" + System.currentTimeMillis());
        } else {
            Log.w(TAG, "TTS 未初始化完成，无法播报");
        }
    }

    /**
     * 停止播报
     */
    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
