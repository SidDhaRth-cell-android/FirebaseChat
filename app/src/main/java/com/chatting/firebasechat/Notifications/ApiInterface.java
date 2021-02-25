package com.chatting.firebasechat.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({"Content-Type:application/json",
            "Authorization:key=AAAAimgA-ag:APA91bFHNmrMs0_VnzS-8CZyaKfHwmLYHpwOZQkDkpx5pjP0NXB50M6uFmjlbsJzdBR-B60_oTpYzUhEIy9xElJEsF50w22mYPOenfW8wOYsTXCKRyKDxApeRQMrrwbDEymTBVskmfiI"})

    @POST("fcm/send")
    Call<MyResponse> sendNotification(
            @Body Sender body
    );
}
