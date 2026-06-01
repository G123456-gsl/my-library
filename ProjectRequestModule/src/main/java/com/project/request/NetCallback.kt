package com.project.request

/**
 * 通用网络回调
 */
interface NetCallback<T> {
    fun onStart()
    fun onSuccess(data: T?, response: ApiResponse<T>) // 返回 data + 原始 response
    fun onFailure(msg: String, code: Int)
    fun onComplete()
}