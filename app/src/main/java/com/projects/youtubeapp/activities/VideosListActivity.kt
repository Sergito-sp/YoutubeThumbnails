package com.projects.youtubeapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.projects.youtubeapp.R
import com.projects.youtubeapp.adapters.VideoListAdapter
import com.projects.youtubeapp.data_definition.YoutubeVideo
import com.projects.youtubeapp.repository.LocalData
import com.projects.youtubeapp.retrofit.YoutubeRequest
import kotlinx.android.synthetic.main.activity_videos_list.*


class VideosListActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_list)

        //Create vertical layout manager
        linearLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        //Handle to RecyclerView, setting the layout manager
        rvVideosForChannel.layoutManager = linearLayoutManager
        rvVideosForChannel.setHasFixedSize(true)

        //Create Youtube API request
        val youtubeApiRequest = YoutubeRequest()

        //Do first request
        youtubeApiRequest.getVideosForChannel(
            resources.getString(R.string.CHANNEL_ID),
            resources.getString(R.string.API_KEY),
            //Define listener (as argument)
            //Create an adapter with the result of the request
            responseListener = object:YoutubeRequest.ResponseListener{
                override fun onResponseReady(result: ArrayList<*>) {

                    LocalData.youtubeVideos = result as ArrayList<YoutubeVideo>
                    rvVideosForChannel.adapter = VideoListAdapter(result).also {
                        //Implement action when an item is clicked
                        it.videoItemClickListener = object:VideoListAdapter.VideoItemClickListener{
                            override fun onVideoItemClick(position: Int) {
                                val intent = Intent(this@VideosListActivity,
                                    VideoDetailActivity::class.java)

                                intent.putExtra(resources.getString(R.string.VIDEO_ID),
                                    it.youtubeVideos.get(position).videoId)

                                startActivity(intent)
                            }
                        }
                    }

                    pb_loading_video_list.visibility = View.GONE
                }
            }
        )

        //Add support for infinite scrolling and managing visibility of FAB when scrolling
        rvVideosForChannel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                val channelID = resources.getString(R.string.CHANNEL_ID)

                if (youtubeApiRequest.numVideosChannel[channelID]?:0 <= totalItemCount)
                    rvVideosForChannel.setPadding(0,0,0,0)

                if (!youtubeApiRequest.isLoadingData &&
                    youtubeApiRequest.numVideosChannel[channelID]?:0 > totalItemCount &&
                    totalItemCount == lastVisibleItemPosition + 1) {

                    showProgressView()
                    youtubeApiRequest.getNextVideosForChannel(
                        channelID,
                        resources.getString(R.string.API_KEY),
                        //Define listener (as argument)
                        //Create an adapter with the result of the request
                        responseListener = object:YoutubeRequest.ResponseListener{
                            override fun onResponseReady(result: ArrayList<*>) {
                                hideProgressView()
                                val adapter = rvVideosForChannel.adapter as VideoListAdapter
                                adapter.addItems(result as ArrayList<YoutubeVideo>)
                            }
                        }
                    )
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.findFirstVisibleItemPosition() > 0) fabScrollUp.show()
                else fabScrollUp.hide()
            }
        })

        //Go up when pressing FAB
        fabScrollUp.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                rvVideosForChannel.smoothScrollToPosition(0)
            }
        })



    }

    fun showProgressView() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressView() {
        progressBar.visibility = View.INVISIBLE
    }
}
