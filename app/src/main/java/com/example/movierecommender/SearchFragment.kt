package com.example.movierecommender

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.android.material.textfield.TextInputLayout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SearchFragment : Fragment() {
    lateinit var mMainMovieImage: ImageView
    lateinit var mMainMovieTitle: TextView
    lateinit var mMainMovieRating: TextView
    lateinit var mMainMovieStory: TextView
    lateinit var mProgressBar: ProgressBar

    lateinit var mSimilarMovieRecyclerView: RecyclerView
    lateinit var mSimilarMovieAdapter: MovieListAdapter
    lateinit var mSimilarMovieLayoutManager: LinearLayoutManager
    val mSimilarMoviesList = mutableListOf<Movie>()

    lateinit var mMovieSearchRecyclerView: RecyclerView
    lateinit var mMovieSearchAdapter: MovieSearchListAdapter
    lateinit var mMovieSearchLayoutManager: LinearLayoutManager
    val mMovieSuggestionsList = mutableListOf<Element>()

    lateinit var mPopularMoviesTextView: TextView
    lateinit var mPopularMovieRecyclerView: RecyclerView
    lateinit var mPopularMovieGridAdapter: PopularMovieGridAdapter
    lateinit var mPopularMovieGridLayoutManager: GridLayoutManager
    val mPopularMovieList = mutableListOf<Movie>()

    lateinit var mSearchEditText: EditText

    lateinit var retrofit: Retrofit
    lateinit var movieSearchApi: MovieSearchApi

    var mUsername = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUsername = arguments!!.getString("username")!!
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMainMovieImage = view.findViewById(R.id.main_movie_image)
        mMainMovieRating = view.findViewById(R.id.main_movie_rating)
        mMainMovieStory = view.findViewById(R.id.main_movie_story)
        mMainMovieTitle = view.findViewById(R.id.main_movie_name)
        mProgressBar = view.findViewById(R.id.progressbar)
        mPopularMoviesTextView = view.findViewById(R.id.popular_movies_text_view)

        retrofit = Retrofit.Builder()
            .baseUrl("https://bestsimilar.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieSearchApi = retrofit.create(MovieSearchApi::class.java)

        initializeMovieListView()
        initializeMovieSearchListView()
        initializePopularMovieGrid()

        PopularMovieList().execute()

        mSearchEditText = view.findViewById<TextInputLayout>(R.id.search_edit).editText!!
        mSearchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val term = s.toString()
                if (term.length > 1) {
                    updateSearchResults(term)
                } else {
                    mMovieSuggestionsList.clear()
                    mMovieSearchAdapter.notifyDataSetChanged()
                }
            }

        })
    }

    fun updateSearchResults(term: String) {
        val call: Call<Suggestion> = movieSearchApi.getSuggestions(term)
        call.enqueue(object: Callback<Suggestion> {
            override fun onFailure(call: Call<Suggestion>, t: Throwable) {
                Log.e("Search", "FAILURE")
            }

            override fun onResponse(call: Call<Suggestion>, response: Response<Suggestion>) {
                if (!response.isSuccessful) {
                    return
                }
                mPopularMoviesTextView.visibility = View.GONE
                mPopularMovieRecyclerView.visibility = View.GONE

                mMovieSuggestionsList.clear()
                mMovieSuggestionsList.addAll(response.body()!!.movie)
                mMovieSearchAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun initializePopularMovieGrid() {
        mPopularMovieRecyclerView = view!!.findViewById(R.id.popular_grid_recycler_view)
        mPopularMovieGridLayoutManager = GridLayoutManager(activity!!, 2)
        mPopularMovieGridAdapter = PopularMovieGridAdapter(mPopularMovieList)
        mPopularMovieGridAdapter.setOnItemClickListener(object: PopularMovieGridAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val newFragment = MovieFragment.newInstance(mPopularMovieGridAdapter.mPopularMovieList[position].url, mUsername)
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_container, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        mPopularMovieRecyclerView.adapter = mPopularMovieGridAdapter
        mPopularMovieRecyclerView.layoutManager = mPopularMovieGridLayoutManager
    }

    private fun initializeMovieListView() {
        mSimilarMovieLayoutManager = LinearLayoutManager(activity!!)
        mSimilarMovieRecyclerView = view!!.findViewById(R.id.movie_recycler_view)
        mSimilarMovieAdapter = MovieListAdapter(mSimilarMoviesList, activity!!)
        mSimilarMovieAdapter.setOnItemClickListener(object: MovieListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val newFragment = MovieFragment.newInstance(mSimilarMovieAdapter.mSimilarMovieList[position].url, mUsername)
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_container, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        mSimilarMovieRecyclerView.adapter = mSimilarMovieAdapter
        mSimilarMovieRecyclerView.layoutManager = mSimilarMovieLayoutManager
    }

    private fun initializeMovieSearchListView() {
        mMovieSearchLayoutManager = LinearLayoutManager(activity!!)
        mMovieSearchRecyclerView = view!!.findViewById(R.id.search_recycler_view)
        mMovieSearchAdapter = MovieSearchListAdapter(mMovieSuggestionsList)
        mMovieSearchAdapter.setOnItemClickListener(object: MovieSearchListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val view = activity!!.currentFocus
                view?.let { v ->
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
                //mSearchEditText.text.clear()
                mSearchEditText.clearFocus()
                MovieList(mMovieSearchAdapter.mMovieSuggestionsList[position].url).execute()
                mMovieSearchAdapter.mMovieSuggestionsList.clear()
                mMovieSearchAdapter.notifyDataSetChanged()
            }

        })
        mMovieSearchRecyclerView.adapter = mMovieSearchAdapter
        mMovieSearchRecyclerView.layoutManager = mMovieSearchLayoutManager
    }

    inner class PopularMovieList(): AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val url = "https://bestsimilar.com/"
            val document: Document = Jsoup.connect(url).get()
            val popularMoviesSection = document.select(".section-list-body.section-movie-grid").first()
            val popularMovies = popularMoviesSection.select("div.col-md-2.col-sm-2.col-ms-4.col-xs-4")
            println(popularMovies.first())
            for (popularMovie in popularMovies) {
                val imageLink = popularMovie.select(".img-responsive.lazy").first().attr("data-src")
                val movieLink = popularMovie.select(".block-ins-caption").select("a").attr("href")
                val movieName = popularMovie.select(".block-ins-caption").text()
                mPopularMovieList.add(Movie(
                    name = movieName,
                    imageSrc = imageLink,
                    url = movieLink
                ))
            }
            activity!!.runOnUiThread {
                mPopularMovieGridAdapter.notifyDataSetChanged()
            }
            return null
        }
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

            val similarMovies = document.getElementsByClass("item item-small clearfix border-top")
            mSimilarMoviesList.clear()
            similarMovies.forEach {
                val similarMovie = Movie()
                similarMovie.name = it.select("a[href]").first().text()
                similarMovie.url = it.select("a[href]").first().attr("href")
                Log.i("ERROR", similarMovie.name)
                try {
                    similarMovie.rating = it.select("span[title=\"rating\"]").first().text().toFloat()
                    similarMovie.votes = it.select("span[title=\"votes\"]").first().text()
                } catch (e: Exception) {
                    similarMovie.rating = 0F
                    similarMovie.votes = "0"
                }
                similarMovie.imageSrc =
                    it.select("img[alt=\"${similarMovie.name}\"]").first().attr("data-src")
                similarMovie.story = it.select("div[class=\"attr attr-story\"]").first()
                    .select("span[class=\"value\"]").text()
                mSimilarMoviesList.add(similarMovie)
            }
            activity!!.runOnUiThread {
                mMainMovieTitle.text = movie.name
                mMainMovieStory.text = movie.story
                mMainMovieRating.text = movie.rating.toString()
                mMainMovieImage.load("https://www.bestsimilar.com/" + movie.imageSrc)
                mSimilarMovieAdapter.notifyDataSetChanged()
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
}
