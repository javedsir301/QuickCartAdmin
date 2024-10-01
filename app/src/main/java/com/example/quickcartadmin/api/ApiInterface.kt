package com.example.quickcartadmin.api

import com.example.quickcartadmin.models.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {


    @Headers(
        "Content-Type: application/json",
        "Authorization: Key=BCOq4lYNvLL211MDca1nAl8vVvpzB51rSzWYW971wItIT49-YoSFsxrIFRTjoKqWL-skgTX48YowIcFjG8FzLQQ"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: Notification): Call<Notification>
}