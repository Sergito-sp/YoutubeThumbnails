package com.projects.youtubeapp.retrofit

import com.projects.youtubeapp.data_definition.YoutubeComment
import com.projects.youtubeapp.data_definition.YoutubeVideo
import org.json.JSONObject

//TODO("improvement) Go through JSON response structure in a better way (not hardcoded)
//  or even better use a third-party library such a GSON

//Parse JSON responses from Youtube API and extract required information
class YoutubeJsonParser {

    companion object {

        //Extract information from a JSON response of a list of Videos for a Channel
        //Return: ArrayList of YoutubeVideo
        fun parseVideosForChannel(response: String?): ArrayList<YoutubeVideo> {

            //Return value
            //If server response is null, return empty collection
            val youtubeVideos = arrayListOf<YoutubeVideo>()
            response ?: run { return youtubeVideos }

            //Get root element (/items)
            val items = JSONObject(response).getJSONArray("items")
            //Iterate through items
            for (i in 0 until items.length()) {

                //Get root Object (/items[i])
                val item = items.getJSONObject(i)

                //Extract videoId (/items[i]/id/videoId)
                val videoId = item.getJSONObject("id")
                    .getString("videoId")

                //Get snippet Object (/items[i]/snippet)
                val snippet = item.getJSONObject("snippet")

                //Extract title (/items[i]/snippet/title)
                val title = snippet.getString("title")

                //Extract thumbnailUrl (/items[i]/snippet/thumbnails/high/url)
                val thumbnailUrl = snippet.getJSONObject("thumbnails")
                    .getJSONObject("high")
                    .getString("url")


                //Create instance of YoutubeVideo and add it to Collection
                youtubeVideos.add(
                    YoutubeVideo(
                        videoId = videoId,
                        title = title,
                        thumbnailUrl = thumbnailUrl
                    )
                )
            }

            return youtubeVideos
        }

        //Extract information from a JSON response of the next token item
        fun parseNextTokenForChannel(response: String?): String {

            //Return value
            //If server response is null, return empty value
            var nextToken = ""
            response ?: run { return nextToken }

            //Extract pageToken(/pageToken)
            nextToken = if (JSONObject(response).has("nextPageToken"))
                JSONObject(response).getString("nextPageToken")
            else ""
            return nextToken
        }

        //Extract information from a JSON response of the number of videos of a channel
        fun parseNumVideosForChannel(response: String?): Int {

            //Return value
            //If server response is null, return empty value
            var numVideos = 0
            response ?: run { return numVideos }

            //Extract numVideos (/pageInfo/totalResults)
            numVideos = JSONObject(response).getJSONObject("pageInfo")
                .getInt("totalResults")

            return numVideos
        }

        //Extract information from a JSON response of the details of a Video
        //Return: ArrayList of YoutubeVideo
        fun parseDetailsForVideo(video: YoutubeVideo, response: String?) {

            //Get root element (/items)
            val items = JSONObject(response).getJSONArray("items")
            if (items.length() > 0){

                //Get root Object (/items[i])
                val item = items.getJSONObject(0)

                //Extract videoId (/items[i]/snippet/publishedAt)
                video.published = item.getJSONObject("snippet")
                    .getString("publishedAt")

                //Extract description (/items[i]/snippet/description)
                video.description = item.getJSONObject("snippet")
                    .getString("description")

                //Extract duration (/items[i]/contentDetails/duration)
                video.duration = item.getJSONObject("contentDetails")
                    .getString("duration")
            }
        }

        //Extract information from a JSON response of a list of Comments for a Video
        //Return: ArrayList of YoutubeComment
        fun parseCommentsForChannel(response: String?): ArrayList<YoutubeComment> {

            val listComments = ArrayList<YoutubeComment>()

            //Get root element (/items)
            val items = JSONObject(response).getJSONArray("items")
            //Iterate through items
            for (i in 0 until items.length()) {

                //Get root Object (/items[i])
                val item = items.getJSONObject(i)

                //Go to comment snippet (items[i]/snippet/topLevelComment/snippet)
                val snippet = item.getJSONObject("snippet")
                    .getJSONObject("topLevelComment")
                    .getJSONObject("snippet")

                //Extract author name (/snippet/authorDisplayName)
                val author = snippet
                    .getString("authorDisplayName")

                //Extract author avatar url (/snippet/authorProfileImageUrl)
                val avatarUrl = snippet
                    .getString("authorProfileImageUrl")

                //Extract comment text (/snippet/textOriginal)
                val comment = snippet
                    .getString("textOriginal")

                listComments.add(
                    YoutubeComment(
                        author,
                        avatarUrl,
                        comment
                    )
                )
            }

            return listComments
        }

        //Extract information from a JSON response of the next token item
        fun parseNextTokenForComments(response: String?): String? {

            //Return value
            //If server response is null, return empty value
            var nextToken:String? = null
            response ?: run { return nextToken }

            //Extract pageToken(/pageToken)
            nextToken = if (JSONObject(response).has("nextPageToken"))
                JSONObject(response).getString("nextPageToken")
            else null
            return nextToken
        }
    }
}