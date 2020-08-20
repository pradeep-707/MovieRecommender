package com.example.movierecommender

import android.provider.BaseColumns

object DBContract {
    class WatchedMoviesTable:BaseColumns {
        companion object {
            const val TABLE_NAME = "watchedMovies"
            const val COLUMN_NAME = "name"
            const val COLUMN_URL = "url"
            const val COLUMN_RATING = "rating"
            const val COLUMN_IMAGE_SRC = "imageSrc"
            const val COLUMN_STORY = "story"
            const val COLUMN_DURATION = "duration"
            const val COLUMN_GENRE = "genre"
        }
    }
}