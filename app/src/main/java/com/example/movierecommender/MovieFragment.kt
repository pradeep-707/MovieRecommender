package com.example.movierecommender

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import coil.api.load
import kotlinx.android.synthetic.*
import org.jsoup.Jsoup
import org.w3c.dom.Text

class MovieFragment : Fragment() {
    private var mMovieAddress: String? = null

    private lateinit var mMovieName: TextView
    private lateinit var mMoviePoster: ImageView
    private lateinit var mMovieRating: TextView
    private lateinit var mMovieStory: TextView
    private lateinit var mMovieDuration: TextView
    private lateinit var mMovieGenre: TextView
    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMovieAddress = it.getString(MOVIE_ADDRESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMovieName = view.findViewById(R.id.movie_name)
        mMoviePoster = view.findViewById(R.id.movie_poster)
        mMovieRating = view.findViewById(R.id.movie_rating)
        mMovieStory = view.findViewById(R.id.movie_story)
        mMovieDuration = view.findViewById(R.id.movie_duration)
        mMovieGenre = view.findViewById(R.id.movie_genre)
        mProgressBar = view.findViewById(R.id.progressbar)

        MovieList(mMovieAddress!!).execute()
    }

    inner class MovieList(val url: String): AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            val document = Jsoup.connect("https://www.bestsimilar.com/$url").get()

            val movieSection = document.select("div.item.item-big.clearfix").first()
            val movie = Movie()
            movie.name =
                movieSection.select("div.is-row.item-name.clearfix div.name-c span").first().text()
            movie.rating = movieSection.select("span[title=\"rating\"]").first().text().toFloat()
            movie.votes = movieSection.select("span[title=\"votes\"]").first().text()
            movie.imageSrc =
                movieSection.select("div.img-c > img.img-responsive").first().attr("src")
            movie.story = movieSection.select("div[class=\"attr attr-story\"]").first()
                .select("span[class=\"value\"]").text()
            movie.duration = movieSection.select(".col-lg-5.col-md-5.col-sm-5.col-xs-12")
                .select(".attr")[2].select("span.value").text()
            movie.genre = movieSection.select(".col-lg-5.col-md-5.col-sm-5.col-xs-12")
                .select(".attr")[0].select("span.value").text()

            activity!!.runOnUiThread {
                mMovieName.text = movie.name
                mMovieStory.text = movie.story
                mMovieRating.text = movie.rating.toString()
                mMovieDuration.text = movie.duration
                mMovieGenre.text = movie.genre
                mMoviePoster.load("https://www.bestsimilar.com/" + movie.imageSrc)
            }
            return null
        }

        override fun onPreExecute() {
            activity!!.runOnUiThread {
                mProgressBar.visibility = View.VISIBLE
            }
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            activity!!.runOnUiThread {
                mProgressBar.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        private const val MOVIE_ADDRESS = "movie_address"

        @JvmStatic
        fun newInstance(movieAddress: String) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putString(MOVIE_ADDRESS, movieAddress)
                }
            }
    }
}