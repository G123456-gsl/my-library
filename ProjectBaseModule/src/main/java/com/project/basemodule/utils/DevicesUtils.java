package com.project.basemodule.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.UUID;


public class DevicesUtils {

    //获取设备标识
    public static String getDeviceIdentifier(Context context) {
        try {
            // 优先尝试获取 Advertising ID
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (adInfo != null && adInfo.getId() != null) {
                return adInfo.getId();
            }
        } catch (Exception e) {
            // 处理异常
        }

        // 备用方案：使用 Android ID
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        if (androidId != null && !androidId.equals("9774d56d682e549c")) {
            return androidId;
        }

        // 最后备选：生成基于硬件信息的 UUID
        return generateFallbackId();
    }

    private static String generateFallbackId() {
        String hardwareInfo = Build.BOARD + Build.BRAND + Build.DEVICE +
                Build.HARDWARE + Build.MODEL + Build.PRODUCT;
        return UUID.nameUUIDFromBytes(hardwareInfo.getBytes()).toString();
    }
}
