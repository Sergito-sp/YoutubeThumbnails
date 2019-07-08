package com.projects.youtubeapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.projects.youtubeapp.R
import com.projects.youtubeapp.adapters.CommentListAdapter
import com.projects.youtubeapp.data_definition.YoutubeComment
import com.projects.youtubeapp.data_definition.YoutubeVideo
import com.projects.youtubeapp.repository.LocalData
import com.projects.youtubeapp.retrofit.YoutubeRequest
import kotlinx.android.synthetic.main.activity_video_detail.*
import kotlinx.android.synthetic.main.activity_video_detail.fabScrollUp
import kotlinx.android.synthetic.main.video_item_list_even.imgVideoThumbnail
import kotlinx.android.synthetic.main.video_item_list_even.txtVideoTitle
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.Period
import kotlin.collections.ArrayList
import android.support.v4.widget.NestedScrollView


class VideoDetailActivity : AppCompatActivity() {

    var commentsLoadDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_detail)

        /*
        val toolbar = gmbnToolbarVideoDetailActivity as Toolbar
        toolbar.setNavigationIcon(R.drawable.back_icon)
        toolbar.setNavigationOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })
        */

        //Create vertical layout manager
        val linearLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        //Handle to RecyclerView, setting the layout manager
        rvCommentsForVideo.layoutManager = linearLayoutManager

        val videoID = intent.getStringExtra(resources.getString(R.string.VIDEO_ID))

        //Find video we want detailed information
        val video: YoutubeVideo? = LocalData.youtubeVideos.find { it.videoId == videoID }

        //If the detailed information has already been retrieved, populate the views
        //If not, pull the data with a request and then populate
        if (video!!.detailedData) populateWithVideo(video)
        else YoutubeRequest().getDetailsForVideo(
            video,
            resources.getString(R.string.API_KEY),
            object : YoutubeRequest.ResponseListener {
                override fun onResponseReady(){
                    video.detailedData = true
                    populateWithVideo(video)
                }
            }
        )



        //If the detailed information has already been retrieved, populate the views with comments
        //If not, pull the data with a request and then populate
        //TODO IMPROVE: Make populateWithComments async so the activity finish creating itself
        // before the rv is populated (and user sees the activity with progressbar when not
        // pulling data from server)
        if (video.detailedData) populateWithComments(video)
        else YoutubeRequest().getCommentsForVideo(
            videoID,
            resources.getString(R.string.API_KEY),
            responseListener = object: YoutubeRequest.ResponseListener{
                override fun onResponseReady(result: ArrayList<*>) {
                    result as ArrayList<YoutubeComment>

                    video.comments.addAll(result)
                    populateWithComments(video)
                }
            }
        )

        //Add support managing visibility of FAB when scrolling
        scroller.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            //Hardcoded height, simple but effective
            if (scrollY < 1000) fabScrollUp.hide()
            else fabScrollUp.show()
        })

        //Go up when pressing FAB
        fabScrollUp.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                scroller.smoothScrollTo(0, 0)
            }
        })
    }

    fun populateWithVideo(video: YoutubeVideo) {

        txtVideoTitle.text = video.title
        txtVideoDuration.text = formatDuration(video.duration)
        txtVideoDatePublished.text = formatDate(video.published)
        txtVideoDescription.text = video.description

        //Video Thumbnail
        Glide.with(this@VideoDetailActivity)
            .load(video.thumbnailUrl)
            //.centerCrop()
            //.dontTransform()
            .fitCenter()
            //.placeholder(R.drawable.ic_image_loading)
            .error(R.drawable.ic_image_broken)
            .fallback(R.drawable.ic_image_loading)
            .into(imgVideoThumbnail)

        showVideoDetails()
    }

    fun populateWithComments(video: YoutubeVideo) {
        rvCommentsForVideo.adapter = CommentListAdapter(video.comments)
        showComments()
    }

    fun showVideoDetails(){

        txtVideoTitleCaption.visibility = View.VISIBLE
        txtVideoDurationCaption.visibility = View.VISIBLE
        txtVideoDatePublishedCaption.visibility = View.VISIBLE
        txtVideoDescriptionCaption.visibility = View.VISIBLE
        txtVideoReadMore.visibility = View.VISIBLE

        pbGroup1.visibility = View.GONE
        if (commentsLoadDone == false){
            pbGroup2.visibility = View.VISIBLE
        }
    }

    fun showComments(){
        commentsLoadDone = true
        pbGroup2.visibility = View.GONE
    }

    //Formats duration String in ISO 8601 format to custom output format
    fun formatDuration(duration: String): String{

        val d = Period.parse(duration)

        return "${String.format("%02d", d.minutes)}:${String.format("%02d", d.seconds)}"
    }

    //Formats date String in ISO 8601 format to custom output format
    fun formatDate(publishedDate: String): String{

        val dateTime = DateTime(publishedDate)
        val dateString = DateTimeFormat.forPattern("dd/MM/yyyy").print(dateTime)

        val day = dateTime.dayOfMonth()
        val month = dateTime.monthOfYear()
        val year = dateTime.year()

        return "${day.get()} ${month.asShortText}, ${year.get()}"

    }

    fun onReadMoreClick(view: View){

        if (txtVideoDescription.maxLines == 3){
            txtVideoDescription.maxLines = Int.MAX_VALUE
            txtVideoReadMore.text = resources.getString(R.string.VIDEO_DESCRIPTION_READ_LESS)
        }
        else{
            txtVideoDescription.maxLines = 3
            txtVideoReadMore.text = resources.getString(R.string.VIDEO_DESCRIPTION_READ_MORE)
        }
    }
}
