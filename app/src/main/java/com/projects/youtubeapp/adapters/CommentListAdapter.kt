package com.projects.youtubeapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.projects.youtubeapp.R
import com.projects.youtubeapp.data_definition.YoutubeComment
import kotlinx.android.synthetic.main.comment_item_list.view.*

class CommentListAdapter(val youtubeComments: ArrayList<YoutubeComment>)
    : RecyclerView.Adapter<CommentListAdapter.ViewHolder>(){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        //Get Views to hold the values
        val txtCommentAuthor = view.txtCommentAuthor
        val txtCommentText = view.txtCommentText
        val imgCommentAuthorAvatar = view.imgCommentAuthorAvatar
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        //Create the View Holder based on the layout for each video
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.comment_item_list, p0, false)
        return ViewHolder(layout)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return youtubeComments.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //Populate views with the values of the comment items
        val comment = youtubeComments.get(p1)
        //Comment Author
        p0.txtCommentAuthor.text = comment.author
        //Comment text
        p0.txtCommentText.text = comment.comment
        //Comment author avatar
        Glide.with(p0.itemView)
            .load(comment.avatarUrl)
            .placeholder(R.drawable.ic_image_loading)
            .error(R.drawable.ic_image_broken)
            .fallback(R.drawable.ic_image_loading)
            .centerCrop()
            .apply(RequestOptions.circleCropTransform())
            .into(p0.imgCommentAuthorAvatar)
    }
}