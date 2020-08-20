package com.example.movierecommender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieSearchListAdapter(movieSuggestionsList: MutableList<Element>): RecyclerView.Adapter<MovieSearchListAdapter.MovieSearchListViewHolder>() {

    var mMovieSuggestionsList = movieSuggestionsList
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MovieSearchListViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view) {
        var mTitleTextView: TextView = view.findViewById(R.id.suggested_movie_name)
        init {
            view.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.movie_suggestion_recycler_item, parent, false)
        val pvh = MovieSearchListViewHolder(v, mListener)
        return pvh
    }

    override fun getItemCount(): Int {
        return mMovieSuggestionsList.size
    }

    override fun onBindViewHolder(holder: MovieSearchListViewHolder, position: Int) {
        holder.mTitleTextView.text = mMovieSuggestionsList[position].label
    }
}