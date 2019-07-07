package com.projects.youtubeapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.projects.youtubeapp.R
import com.projects.youtubeapp.data_definition.YoutubeVideo
import kotlinx.android.synthetic.main.video_item_list_even.view.*

class VideoListAdapter(val youtubeVideos: ArrayList<YoutubeVideo>)
    :RecyclerView.Adapter<VideoListAdapter.ViewHolder>(){

    //Listener interface for Click on item
    interface VideoItemClickListener {
        fun onVideoItemClick(position: Int)
    }

    var videoItemClickListener: VideoItemClickListener? = null

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        //Get Views to hold the values
        val txtVideoTitle = view.txtVideoTitle
        val imgVideoThumbnail = view.imgVideoThumbnail
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        //Create the View Holder based on the layout for each video
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.video_item_list_even, p0, false)
        return ViewHolder(layout)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return youtubeVideos.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //Set OnClickListener for this item, calling custom listener created for it
        p0.itemView.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                videoItemClickListener?.onVideoItemClick(p1)
            }
        })

        //TODO FIX, not sure why this text hides the click.
        // If i don´t implement this code the text gets the click and
        // the ViewHolder doesn´t get it, so nothing happens
        p0.txtVideoTitle.setOnClickListener {
            p0.itemView.callOnClick()
        }

        //Populate views with the values of the video items
        val video = youtubeVideos.get(p1)
        //Video Title
        p0.txtVideoTitle.text = video.title
        //Video Thumbnail
        Glide.with(p0.itemView)
            .load(video.thumbnailUrl)
            .placeholder(R.drawable.ic_image_loading)
            .error(R.drawable.ic_image_broken)
            .fallback(R.drawable.ic_image_loading)
            .centerCrop()
            .into(p0.imgVideoThumbnail)
    }

    fun addItems(moreYoutubeVideos: ArrayList<YoutubeVideo>){
        youtubeVideos.addAll(moreYoutubeVideos)
        notifyDataSetChanged()
    }
}