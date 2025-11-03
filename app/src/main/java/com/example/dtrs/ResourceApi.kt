package com.example.dtrs;

import retrofit2.Call;
import retrofit2.http.*;

interface ResourceApi {
    @GET("resources/resources")
    fun getResources(): Call<List<Resource>>

    @GET("resources/resources/{id}")
    fun getResource(@Path("id") id: Int): Call<Resource>

    @POST("resources/resources")
    fun addResource(@Body resource: Resource): Call<Resource>

    @PUT("resources/resources/{id}")
    fun updateResource(@Path("id") id: Int, @Body resource: Resource): Call<Resource>

    @DELETE("resources/resources/{id}")
    fun deleteResource(@Path("id") id: Int): Call<Void>
}