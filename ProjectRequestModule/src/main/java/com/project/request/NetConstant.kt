package com.project.request

/**
 * 通用网络常量
 */
object NetConstant {
    // 默认超时时间（秒）
    const val DEFAULT_CONNECT_TIMEOUT = 10L
    const val DEFAULT_READ_TIMEOUT = 40L
    const val DEFAULT_WRITE_TIMEOUT = 40L

    // 系统错误码
    const val ERROR_CODE_EMPTY = -1
    const val ERROR_CODE_NETWORK = -2
    const val ERROR_CODE_PARSE = -3
    const val ERROR_CODE_HTTP = -4
    const val ERROR_CODE_UNKNOWN = -5
}