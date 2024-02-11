package com.example.zaludaradimmovieapp.movies.details.presentation

import com.example.zaludaradimmovieapp.movies.domain.model.Movie

data class DetailsState(
    val isLoading: Boolean = false,
    val movie: Movie? = null
)
