package com.project.request

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Maven 通用网络请求管理器
 */
class NetRequestManager private constructor(
    private val context: Context,
    private val baseUrl: String,
    private val connectTimeout: Long,
    private val readTimeout: Long,
    private val writeTimeout: Long,
    private val customInterceptors: List<Interceptor>
) {

    companion object {
        const val TAG = "NetRequestManager"
        private const val TOKEN_PREFIX = "Bearer "

        @Volatile
        private var instance: NetRequestManager? = null

        fun init(context: Context, builder: Builder) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        val appContext = context.applicationContext
                        instance = builder.build(appContext)
                    }
                }
            }
        }

        fun getInstance(): NetRequestManager {
            return instance ?: throw IllegalStateException("请先初始化 NetRequestManager")
        }
    }

    private var token: String? = null

    @Synchronized
    fun setToken(newToken: String?) {
        token = newToken
    }

    private val userAgent: String by lazy { generateUserAgent() }
    private val deviceCode: String by lazy { generateDeviceCode() }

    private val okHttpClient: OkHttpClient by lazy { buildOkHttpClient() }

    private val retrofitMap = mutableMapOf<String, Retrofit>()

    private fun buildOkHttpClient(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor { Log.d(TAG, it) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val tokenInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("Charset", "UTF-8")
                .header("User-Agent", userAgent)
                .header("Device-Code", deviceCode)
            token?.let { builder.header("Authorization", "$TOKEN_PREFIX$it") }
            chain.proceed(builder.build())
        }

        return OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(tokenInterceptor)
            .apply { customInterceptors.forEach { addInterceptor(it) } }
            .build()
    }

    /**
     * 获取User-Agent
     */
    private fun generateUserAgent(): String {
        return try {
            val webView = WebView(context)
            val ua = webView.settings.userAgentString
            webView.destroy() // 销毁WebView避免内存泄漏
            ua
        } catch (e: Exception) {
            // 兜底：拼接基础UA
            "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; ${Build.MODEL}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/120.0.0.0 Mobile Safari/537.36"
        }
    }

    /**
     * 获取Device-Code
     */
    private fun generateDeviceCode(): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ?: "unknown_device_${System.currentTimeMillis()}"
        } catch (e: Exception) {
            "unknown_device_${System.currentTimeMillis()}"
        }
    }

    // 默认 Retrofit（使用初始化的 baseUrl）
    private val retrofit: Retrofit by lazy {
        createRetrofit(baseUrl)
    }

    /**
     * 创建 Retrofit（支持多域名）
     */
    private fun createRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 获取指定域名的 Retrofit
     */
    fun getRetrofit(baseUrl: String): Retrofit {
        return retrofitMap.getOrPut(baseUrl) {
            createRetrofit(baseUrl)
        }
    }

    /**
     * 创建业务 ApiService
     */
    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    fun <S> createService(baseUrl: String, serviceClass: Class<S>): S {
        return getRetrofit(baseUrl).create(serviceClass)
    }

    // ================= Builder =================
    class Builder {
        private var baseUrl: String = ""
        private var connectTimeout: Long = NetConstant.DEFAULT_CONNECT_TIMEOUT
        private var readTimeout: Long = NetConstant.DEFAULT_READ_TIMEOUT
        private var writeTimeout: Long = NetConstant.DEFAULT_WRITE_TIMEOUT
        private val interceptors = mutableListOf<Interceptor>()

        fun baseUrl(url: String) = apply { baseUrl = url }
        fun connectTimeout(seconds: Long) = apply { connectTimeout = seconds }
        fun readTimeout(seconds: Long) = apply { readTimeout = seconds }
        fun writeTimeout(seconds: Long) = apply { writeTimeout = seconds }
        fun timeout(connect: Long, read: Long, write: Long) = apply {
            connectTimeout = connect
            readTimeout = read
            writeTimeout = write
        }

        fun addInterceptor(interceptor: Interceptor) = apply { interceptors.add(interceptor) }

        fun build(appContext: Context): NetRequestManager {
            require(baseUrl.isNotBlank()) { "BaseUrl不能为空" }
            return NetRequestManager(
                appContext,
                baseUrl,
                connectTimeout,
                readTimeout,
                writeTimeout,
                interceptors
            )
        }
    }
}

/**
 * 核心请求方法
 * @param block: Retrofit suspend lambda
 * @param callback: 回调
 * 自动解析 ApiResponse<T>，统一处理 code/msg
 */
fun <T> CoroutineScope.requestApi(
    block: suspend () -> ApiResponse<T>?,
    callback: NetCallback<T>
): Job {
    return launch(Dispatchers.Main) {
        callback.onStart()
        try {
            val response = block()

            if (response == null) {
                callback.onFailure("响应内容为空", NetConstant.ERROR_CODE_EMPTY)
            } else {
                if (response.isSuccess()) {
                    callback.onSuccess(response.data, response)
                } else {
                    callback.onFailure(response.msg ?: "业务请求失败", response.code)
                }
            }
        } catch (e: Exception) {
            handleException(e, callback)
        } finally {
            callback.onComplete()
        }
    }
}

private fun <T> handleException(e: Exception, callback: NetCallback<T>) {
    if (e is CancellationException) return // 协程取消不处理回调

    val (msg, code) = when (e) {
        is UnknownHostException -> "无法连接到服务器，请检查网络" to NetConstant.ERROR_CODE_NETWORK
        is SocketTimeoutException -> "连接超时，请稍后重试" to NetConstant.ERROR_CODE_NETWORK
        is IOException -> "网络交互异常" to NetConstant.ERROR_CODE_NETWORK
        is JsonParseException, is JsonSyntaxException -> "数据格式解析失败" to NetConstant.ERROR_CODE_PARSE
        is HttpException -> "服务器响应异常(${e.code()})" to NetConstant.ERROR_CODE_HTTP
        else -> "未知错误: ${e.localizedMessage}" to NetConstant.ERROR_CODE_UNKNOWN
    }
    Log.e(NetRequestManager.TAG, "Request Error: $msg", e)
    callback.onFailure(msg, code)
}
