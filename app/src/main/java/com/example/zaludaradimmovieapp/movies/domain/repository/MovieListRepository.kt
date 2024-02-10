package com.example.zaludaradimmovieapp.movies.domain.model.repository

import com.example.zaludaradimmovieapp.movies.domain.model.Movie
import com.example.zaludaradimmovieapp.movies.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {
    suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>>

    suspend fun getMovie(id: Int): Flow<Resource<Movie>>
}