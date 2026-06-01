package com.project.request

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface CommonApiService {

    @GET
    suspend fun get(
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): ResponseBody?

    @POST
    suspend fun post(
        @Url url: String,
        @Body body: RequestBody
    ): ResponseBody?
}