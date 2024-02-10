package com.example.zaludaradimmovieapp.movies.presentation

sealed interface MovieListUIEvent {

    data class Paginate(val category: String) : MovieListUIEvent
    object Navigate: MovieListUIEvent
}