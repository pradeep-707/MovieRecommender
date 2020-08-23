package com.example.movierecommender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load

class PopularMovieGridAdapter(popularMovieList: MutableList<Movie>): RecyclerView.Adapter<PopularMovieGridAdapter.PopularMovieGridViewHolder>() {

    var mPopularMovieList = popularMovieList
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
    ): PopularMovieGridAdapter.PopularMovieGridViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.popular_item, parent, false)
        val pvh = PopularMovieGridViewHolder(v, mListener)
        return pvh
    }

    override fun getItemCount(): Int {
        return mPopularMovieList.size
    }

    override fun onBindViewHolder(holder: PopularMovieGridAdapter.PopularMovieGridViewHolder, position: Int) {
        val popularMovie = mPopularMovieList[position]
        holder.mTitleTextView.text = popularMovie.name
        holder.mImageView.load("https://www.bestsimilar.com" + popularMovie.imageSrc)
    }

    class PopularMovieGridViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view) {
        var mImageView: ImageView = view.findViewById(R.id.popular_image)
        var mTitleTextView: TextView = view.findViewById(R.id.popular_name)

        init {
            view.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }
}