package com.project.request

/**
 * 通用响应接口
 */
interface IApiResponse<T> {
    fun getCode(): Int
    fun getMsg(): String
    fun getData(): T
    fun isSuccess(): Boolean
}