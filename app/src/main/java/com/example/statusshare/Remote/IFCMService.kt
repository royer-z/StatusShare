package com.example.statusshare.Remote


import com.example.statusshare.Model.MyResponse
import com.example.statusshare.Model.Request
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers("Content-Type:application/json",
            "Authorization:key=AAAA1gMhkSU:APA91bELNu-A-YZ0W-JTZhvpP98fOGHyDHZ8jB9pFwfLGj_kAtbpKKCl2oaCCo5br4tfNiom9ebr_8OudehoG0M2x2BgWL2Xd_-JFZ1KXgMKCaYKGIYuIY6-UGJXk4SPSwCZnyhzSfP4")
    @POST("fcm/send")

    fun sendFriendRequestToUser(@Body body: Request):Observable<MyResponse>

}