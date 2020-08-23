package com.example.movierecommender

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.*
import org.jsoup.Jsoup

class MovieFragment : Fragment() {
    private var mMovieAddress: String? = null

    private lateinit var mMovieName: TextView
    private lateinit var mMoviePoster: ImageView
    private lateinit var mMovieRating: TextView
    private lateinit var mMovieStory: TextView
    private lateinit var mMovieDuration: TextView
    private lateinit var mMovieGenre: TextView
    private lateinit var mProgressBar: ProgressBar

    private lateinit var mCommentEditText: EditText
    private lateinit var mCommentPostButton: Button

    private lateinit var mPercentLikedTextView: TextView
    private lateinit var mThumbUp: ImageView
    private lateinit var mThumbDown: ImageView

    private lateinit var mCommentsRecyclerView: RecyclerView
    private lateinit var mCommentsAdapter: CommentsAdapter
    private lateinit var mCommentsLayoutManager: LinearLayoutManager

    private lateinit var mBookTicketsButton: Button

    private var mUsername: String = ""


    val mMovie = Movie()
    var mCommentsList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMovieAddress = it.getString(MOVIE_ADDRESS)
            mUsername = it.getString(USERNAME).toString()
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
        //mProgressBar = view.findViewById(R.id.progressbar)

        mPercentLikedTextView = view.findViewById(R.id.percent_liked_text_view)
        mThumbUp = view.findViewById(R.id.thumb_up)
        mThumbDown = view.findViewById(R.id.thumb_down)
        mThumbUp.setOnClickListener { rateThumbUp() }
        mThumbDown.setOnClickListener { rateThumbDown() }

        mCommentEditText = view.findViewById(R.id.comment_edit_text)
        mCommentPostButton = view.findViewById(R.id.comment_post_button)
        mCommentPostButton.setOnClickListener {
            addComment()
        }

        mBookTicketsButton = view.findViewById(R.id.book_tickets_button)
        mBookTicketsButton.setOnClickListener {
            val newFragment =
                TicketFragment()
            newFragment.arguments = Bundle()
            newFragment.arguments!!.putString("username", mUsername)
            newFragment.arguments!!.putString("movieName", mMovie.name)
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        MovieList(mMovieAddress!!).execute()
        createCommentsList()
    }

    private fun createCommentsList() {
        mCommentsRecyclerView = view!!.findViewById(R.id.comments_recycler_view)
        mCommentsAdapter = CommentsAdapter(mCommentsList)
        mCommentsLayoutManager = LinearLayoutManager(activity!!)
        mCommentsRecyclerView.adapter = mCommentsAdapter
        mCommentsRecyclerView.layoutManager = mCommentsLayoutManager
    }

    private fun addComment() {
        val comment = Comment(mUsername, mCommentEditText.text.toString())
        val reference = FirebaseDatabase.getInstance().reference
        reference.child("movies").child(mMovie.name).child("comments")
            .push().setValue(comment)
        loadComments()
        mCommentEditText.text.clear()
    }

    private fun rateThumbUp() {
        FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovie.name).child("ratings").child(mUsername).setValue(true)
            .addOnCompleteListener { Toast.makeText(activity!!, "Rated Thumbs Up!", Toast.LENGTH_SHORT).show() }
        loadRating()
    }

    private fun rateThumbDown() {
        FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovie.name).child("ratings").child(mUsername).setValue(false)
            .addOnCompleteListener { Toast.makeText(activity!!, "Rated Thumbs Down!", Toast.LENGTH_SHORT).show() }
        loadRating()
    }

    private fun loadComments() {
        val reference = FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovie.name).child("comments")

        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mCommentsList.clear()
                for(commentDs in dataSnapshot.children) {
                    val comment = commentDs.getValue(Comment::class.java)
                    mCommentsList.add(comment!!)
                    Log.i("comment", comment.toString())
                }
                mCommentsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        reference.addListenerForSingleValueEvent(valueEventListener)
        reference.addValueEventListener(valueEventListener)
    }

    private fun loadRating() {
        val reference = FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovie.name).child("ratings")

        val valueEventListener = object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mPercentLikedTextView.text = "This movie has not been rated yet"
                }
                var thumpUp = 0
                var total = 0
                for(ratingDs in dataSnapshot.children) {
                    total += 1
                    if (ratingDs.getValue(Boolean::class.java)!!)
                        thumpUp += 1
                }
                val rating: Int = ((thumpUp / total.toFloat()) * 100).toInt()
                if (total == 0) {
                    mPercentLikedTextView.text = "This movie has not been rated yet"
                } else {
                    mPercentLikedTextView.text = "$rating% liked this movie. ($total votes)"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        reference.addValueEventListener(valueEventListener)
        reference.addListenerForSingleValueEvent(valueEventListener)
    }

    inner class MovieList(val url: String): AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            val document = Jsoup.connect("https://www.bestsimilar.com/$url").get()

            val movieSection = document.select("div.item.item-big.clearfix").first()
            //val movie = Movie()
            mMovie.name =
                movieSection.select("div.is-row.item-name.clearfix div.name-c span").first().text()
            mMovie.rating = movieSection.select("span[title=\"rating\"]").first().text().toFloat()
            mMovie.votes = movieSection.select("span[title=\"votes\"]").first().text()
            mMovie.imageSrc =
                movieSection.select("div.img-c > img.img-responsive").first().attr("src")
            mMovie.story = movieSection.select("div[class=\"attr attr-story\"]").first()
                .select("span[class=\"value\"]").text()
            mMovie.duration = movieSection.select(".col-lg-5.col-md-5.col-sm-5.col-xs-12")
                .select(".attr")[2].select("span.value").text()
            mMovie.genre = movieSection.select(".col-lg-5.col-md-5.col-sm-5.col-xs-12")
                .select(".attr")[0].select("span.value").text()


            activity!!.runOnUiThread {
                loadComments()
                loadRating()
                mMovieName.text = mMovie.name
                mMovieStory.text = mMovie.story
                mMovieRating.text = mMovie.rating.toString()
                mMovieDuration.text = mMovie.duration
                mMovieGenre.text = mMovie.genre
                mMoviePoster.load("https://www.bestsimilar.com/" + mMovie.imageSrc)
                view!!.findViewById<RelativeLayout>(R.id.parent_view).visibility = View.VISIBLE
            }
            return null
        }

        override fun onPreExecute() {
            activity!!.runOnUiThread {
                //mProgressBar.visibility = View.VISIBLE
            }
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            activity!!.runOnUiThread {
                //mProgressBar.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        private const val MOVIE_ADDRESS = "movie_address"
        private const val USERNAME = "username"

        @JvmStatic
        fun newInstance(movieAddress: String, username:String) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putString(MOVIE_ADDRESS, movieAddress)
                    putString(USERNAME, username)
                }
            }
    }
}