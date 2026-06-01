package com.project.basemodule.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogUtils {
    // 日志级别：VERBOSE、DEBUG、INFO、WARN、ERROR（控制是否打印/存储）
    public static final int LEVEL_VERBOSE = 1;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_ERROR = 5;
    public static final int LEVEL_NONE = 6; // 关闭所有日志

    // 全局日志级别（Release 环境可设为 LEVEL_NONE 或 LEVEL_ERROR）
    private static int sLogLevel = LEVEL_DEBUG; // Debug 环境默认打印所有

    private static String sLogDirPath; // 日志存储目录
    private static final String TAG = "LogUtils";
    private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor(); // 单线程池异步写入

    // 初始化：在 Application 中调用，传入上下文
    public static void init(Context context) {
        // 日志存储在应用私有目录（无需动态权限，路径：/Android/data/包名/files/logs/）
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            sLogDirPath = new File(externalFilesDir, "logs").getAbsolutePath();
            // 创建目录（若不存在）
            new File(sLogDirPath).mkdirs();
        } else {
            Log.e(TAG, "初始化失败：外部存储不可用");
        }
    }

    // 设置日志级别（例如 Release 环境设为 LEVEL_ERROR，只记录错误日志）
    public static void setLogLevel(int level) {
        sLogLevel = level;
    }

    // 封装 Log.d（调试日志）
    public static void d(String tag, String msg) {
        if (sLogLevel <= LEVEL_DEBUG) {
            Log.d(tag, msg); // 打印到 Logcat
            writeToFile("D", tag, msg); // 异步写入文件
        }
    }

    // 封装 Log.e（错误日志，支持 Throwable）
    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sLogLevel <= LEVEL_ERROR) {
            if (tr != null) {
                Log.e(tag, msg, tr); // 打印到 Logcat（含异常栈）
                writeToFile("E", tag, msg + "\n" + Log.getStackTraceString(tr)); // 写入异常栈
            } else {
                Log.e(tag, msg);
                writeToFile("E", tag, msg);
            }
        }
    }

    // 异步写入日志到文件（避免阻塞主线程）
    private static void writeToFile(String level, String tag, String msg) {
        if (sLogDirPath == null) {
            Log.e(TAG, "日志目录未初始化，请先调用 init()");
            return;
        }

        // 日志文件名：按天分割（如 2024-05-20.log）
        String fileName = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()) + ".log";
        String filePath = sLogDirPath + File.separator + fileName;

        // 日志内容格式：[时间] [级别] [线程名] [标签] 消息
        String logContent = String.format(
                "[%s] [%s] [%s] [%s] %s\n",
                sTimeFormat.format(new Date()),
                level,
                Thread.currentThread().getName(),
                tag,
                msg
        );

        // 提交到单线程池异步写入
        sExecutor.execute(() -> {
            FileWriter writer = null;
            try {
                // 追加模式（true 表示不覆盖原有内容）
                writer = new FileWriter(filePath, true);
                writer.write(logContent);
                writer.flush();
            } catch (IOException e) {
                Log.e(TAG, "写入日志失败：" + e.getMessage());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 获取日志存储目录（用于导出日志）
    public static String getLogDirPath() {
        return sLogDirPath;
    }
}
