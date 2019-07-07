package com.projects.youtubeapp.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YoutubeService {

    companion object {

        const val YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/"

        //Order to pull data (by date, most recent first)
        const val ORDER = "date"
        //Order to pull data of comments(by date, most recent first)
        const val ORDER_COMMENTS = "time"
        //Max number of results to pull
        const val MAX_RESULTS = 10
        //Max number of comments to pull
        const val MAX_RESULTS_COMMENTS = 100
    }

    //Call to Youtube API to search for videos of a specific channel sorted by date
    @GET("search?" +
            "part=snippet&" +
            "type=video&" +
            "maxResults=$MAX_RESULTS&" +
            "order=$ORDER")
    fun getVideosForChannel(@Query("channelId") channelId:String,
                            @Query("key") apiKey:String,
                            @Query("pageToken") nextPageToken:String): Call<ResponseBody>

    //Call to Youtube API to get details of a specific video
    @GET("videos?" +
            "part=snippet,contentDetails")
    fun getDetailsForVideo(@Query("id") videoId:String,
                           @Query("key") apiKey:String): Call<ResponseBody>

    //Call to Youtube API to get top-level comments of a specific video
    @GET("commentThreads?" +
            "part=snippet&" +
            "maxResults=$MAX_RESULTS_COMMENTS&" +
            "order=$ORDER_COMMENTS")
    fun getCommentsForVideo(@Query("videoId") channelID:String,
                            @Query("key") apiKey:String,
                            @Query("pageToken") nextPageToken:String): Call<ResponseBody>

    //Call to Youtube API to get details of a specific channel
    @GET("channels?" +
            "part=snippet")
    fun getDetailsForChannel(@Query("channelId") channelID:String,
                             @Query("key") apiKey:String): Call<ResponseBody>





}