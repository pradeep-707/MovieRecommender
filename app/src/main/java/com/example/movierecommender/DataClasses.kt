package com.example.movierecommender

data class Suggestion(var movie: List<Element>)

data class Element(var label: String, var url: String)