package com.sumit.musicplayer

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {

    @GET("search")
    fun post(@Query("term")postId:String): Call<ResponseDTO>
}
