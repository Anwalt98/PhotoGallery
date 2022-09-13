package com.bignerdranch.android.photogallery.FirebaseNotifications

import com.bignerdranch.android.photogallery.FirebaseNotifications.Constants.Companion.CONTENT_TYPE
import com.bignerdranch.android.photogallery.FirebaseNotifications.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @POST("fcm/send")
    @Headers("Authorization: key=$SERVER_KEY","Content-Type:$CONTENT_TYPE")
    suspend fun postNotification(
        @Body notification: PushNotification
    ) : Response<ResponseBody>


}