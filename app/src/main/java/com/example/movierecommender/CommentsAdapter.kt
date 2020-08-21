package com.example.movierecommender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(comments: MutableList<Comment>): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    val mComments = comments

    class CommentsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mUsername: TextView = view.findViewById(R.id.comments_username)
        var mBody: TextView = view.findViewById(R.id.comments_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comments_item, parent, false)
        return CommentsViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment = mComments[position]
        holder.mBody.text = comment.body
        holder.mUsername.text = comment.username
    }

    override fun getItemCount(): Int {
        return mComments.size
    }
}