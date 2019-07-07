package com.projects.youtubeapp.data_definition

data class YoutubeVideo(
    val videoId: String, //ID of the video in the Youtube site
    var title: String, //Title of the video
    var thumbnailUrl: String = "", //URL to video Thumbnail
    var published: String = "", //Date published, in ISO 8601 format (with Timezone)
    var description: String = "", //Description of the video
    var duration: String = "" //Duration of the video, in ISO 8601 format
)
{
    var detailedData:Boolean = false //Boolean to check if the instance already has pulled all the data
    val comments: ArrayList<YoutubeComment> = ArrayList<YoutubeComment>()
}