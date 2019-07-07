package com.projects.youtubeapp.retrofit

import com.projects.youtubeapp.data_definition.YoutubeComment
import com.projects.youtubeapp.data_definition.YoutubeVideo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class YoutubeRequest {

    //Flag to know if data is currently being pulled async
    var isLoadingData: Boolean = false
    //Map of ChannelID/Token to know the next token id of each channel (videos are pulled by groups)
    var nextPageToken = hashMapOf<String, String>()
    //Map of ChannelID/Size to know the number of videos of each channel (to know when to stop pulling)
    var numVideosChannel = hashMapOf<String, Int>()

    //Listener interface for async response
    interface ResponseListener {
        fun onResponseReady(result: ArrayList<*>){}
        fun onResponseReady(){}
    }

    //Property holding the Retrofit service created
    private val service: YoutubeService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(YoutubeService.YOUTUBE_API_BASE_URL)
            .build()
        service = retrofit.create(YoutubeService::class.java)
    }

    //Call to Youtube API to search for videos of a specific channel sorted by date
    fun getVideosForChannel(channelID: String, apiKey: String, currentPageToken:String? = "", responseListener: ResponseListener) {

        isLoadingData = true
        //Fix to avoid null
        val currentPageTokenFixed = currentPageToken?:""
        service.getVideosForChannel(channelID, apiKey, currentPageTokenFixed).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoadingData = false
                val responseValue = response.body()?.string()

                if (!numVideosChannel.containsKey(channelID))
                    numVideosChannel[channelID] = YoutubeJsonParser.parseNumVideosForChannel(responseValue)

                //Get next token value
                nextPageToken[channelID] = YoutubeJsonParser.parseNextTokenForChannel(responseValue)

                //Get videos values
                val videos = YoutubeJsonParser.parseVideosForChannel(responseValue)
                responseListener.onResponseReady(videos)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoadingData = false
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    //Call to Youtube API to search for the next set of videos of a specific channel
    fun getNextVideosForChannel(channelID: String, apiKey: String, responseListener: ResponseListener) {
        getVideosForChannel(channelID, apiKey, nextPageToken[channelID], responseListener)
    }

    //Call to Youtube API to get details of a specific video
    fun getDetailsForVideo(video: YoutubeVideo, apiKey: String, responseListener: ResponseListener) {

        isLoadingData = true
        service.getDetailsForVideo(video.videoId, apiKey).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoadingData = false

                val responseValue = response.body()?.string()

                //Get videos values
                YoutubeJsonParser.parseDetailsForVideo(video, responseValue)
                responseListener.onResponseReady()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoadingData = false
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    //Call to Youtube API to get top-level comments of a specific video
    fun getCommentsForVideo(videoId:String, apiKey:String, currentPageToken:String? = "", comments:ArrayList<YoutubeComment> = ArrayList<YoutubeComment>(), responseListener: ResponseListener) {

        isLoadingData = true
        //Fix to avoid null
        val currentPageTokenFixed = currentPageToken?:""
        service.getCommentsForVideo(videoId, apiKey, currentPageTokenFixed).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoadingData = false

                val responseValue = response.body()?.string()

                //Get comment values
                comments.addAll(YoutubeJsonParser.parseCommentsForChannel(responseValue))

                //Get next token, and if it exists, pull next set of comments in a recursive way
                val nextToken = YoutubeJsonParser.parseNextTokenForComments(responseValue)
                nextToken?.let{ getCommentsForVideo(videoId, apiKey, nextToken, comments, responseListener) }
                nextToken?: run { responseListener.onResponseReady(comments) }
             }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoadingData = false
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    //Call to Youtube API to get details of a specific channel
    fun getDetailsForChannel(channelID: String, apiKey: String, responseListener: ResponseListener) {

        isLoadingData = true
        service.getDetailsForChannel(channelID, apiKey).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoadingData = false
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoadingData = false
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}