package com.example.movierecommender

data class Movie (
    var name: String = "",
    var url: String = "",
    var rating: Float = 0F,
    var votes: String = "",
    var imageSrc: String = "",
    var story: String = "",
    var duration: String = "",
    var genre: String = ""
)