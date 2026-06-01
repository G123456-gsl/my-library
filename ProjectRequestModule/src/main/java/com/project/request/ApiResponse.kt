package com.project.request

data class ApiResponse<T>(
    val code: Int,
    val msg: String?,
    val data: T?
) {
    fun isSuccess(): Boolean {
        return code == 200
    }
}