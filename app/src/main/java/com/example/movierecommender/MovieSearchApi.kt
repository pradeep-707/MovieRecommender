package com.example.movierecommender

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieSearchApi {

    @GET("site/autocomplete")
    fun getSuggestions(@Query("term") term: String): Call<Suggestion>

}