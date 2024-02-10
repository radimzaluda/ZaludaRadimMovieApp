package com.example.zaludaradimmovieapp.movies.DependencyInjection

import com.example.zaludaradimmovieapp.movies.data.repository.MovieListRepositoryImplementation
import com.example.zaludaradimmovieapp.movies.domain.model.repository.MovieListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieListRepository(
        movieListRepositoryImpl: MovieListRepositoryImplementation
    ): MovieListRepository

}