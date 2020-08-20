package com.example.movierecommender

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load

class MovieListAdapter(similarMoviesList: MutableList<Movie>, ctx: Context): RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    var mSimilarMovieList = similarMoviesList
    val context = ctx
    lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieListAdapter.MovieListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.movie_recycler_item, parent, false)
        val pvh = MovieListViewHolder(v, mListener)
        return pvh
    }

    override fun getItemCount(): Int {
        return mSimilarMovieList.size
    }

    override fun onBindViewHolder(holder: MovieListAdapter.MovieListViewHolder, position: Int) {
        val similarMovie = mSimilarMovieList[position]
        holder.mTitleTextView.text = similarMovie.name
        //Picasso.get().load("http://www.bestsimilar.com" + similarMovie.imageSrc).into(holder.mImageView)
        //Glide.with(context).load("http://www.bestsimilar.com" + similarMovie.imageSrc).into(holder.mImageView)
        holder.mImageView.load("https://www.bestsimilar.com" + similarMovie.imageSrc)
        holder.mRatingTextView.text = similarMovie.rating.toString()
        holder.mStoryTextView.text = similarMovie.story
    }

    class MovieListViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view) {
        var mImageView: ImageView = view.findViewById(R.id.movie_poster)
        var mTitleTextView: TextView = view.findViewById(R.id.movie_name)
        var mRatingTextView: TextView = view.findViewById(R.id.movie_rating)
        var mStoryTextView: TextView = view.findViewById(R.id.movie_story)

        init {
            view.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }
}