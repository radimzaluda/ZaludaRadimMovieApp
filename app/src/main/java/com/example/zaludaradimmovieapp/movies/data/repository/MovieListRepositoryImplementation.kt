package com.example.zaludaradimmovieapp.movies.data.repository

// Importy potřebných tříd a balíčků
import com.example.zaludaradimmovieapp.movies.data.local.movie.MovieDatabase
import com.example.zaludaradimmovieapp.movies.data.mappers.toMovie
import com.example.zaludaradimmovieapp.movies.data.mappers.toMovieEntity
import com.example.zaludaradimmovieapp.movies.data.remote.MovieAPI
import com.example.zaludaradimmovieapp.movies.domain.model.Movie
import com.example.zaludaradimmovieapp.movies.domain.model.repository.MovieListRepository
import com.example.zaludaradimmovieapp.movies.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

// Definice třídy MovieListRepositoryImplementation
class MovieListRepositoryImplementation @Inject constructor(
    private val movieApi: MovieAPI,
    private val movieDatabase: MovieDatabase
) : MovieListRepository {

    // Metoda pro získání seznamu filmů
    override suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {
            // zobrazení  zprávy o načítání
            emit(Resource.Loading(true))

            // Získání seznamu filmů z lokální databáze
            val localMovieList = movieDatabase.movieDAO.getMovieListByCategory(category)

            // Určení, zda se mají použít lokální data nebo načíst data z vzdáleného API
            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !forceFetchFromRemote

            if (shouldLoadLocalMovie) {
                // Pokud jsou dostupná lokální data, zobrazí se
                emit(Resource.Success(
                    data = localMovieList.map { movieEntity ->
                        movieEntity.toMovie(category)
                    }
                ))

                // zobrazení zprávy o ukončení načítání
                emit(Resource.Loading(false))
                return@flow
            }

            // Pokud se mají načíst data z API
            val movieListFromApi = try {
                movieApi.getMoviesList(category, page)
            } catch (e: IOException) {
                // Chyba při načítání dat z API kvůli síťové chybě
                e.printStackTrace()
                emit(Resource.Error(message = "Nelze načíst filmy"))
                return@flow
            } catch (e: HttpException) {
                // Chyba při načítání dat z API kvůli chybě serveru
                e.printStackTrace()
                emit(Resource.Error(message = "Nelze načíst filmy"))
                return@flow
            } catch (e: Exception) {
                // Obecná chyba při načítání dat z API
                e.printStackTrace()
                emit(Resource.Error(message = "Nelze načíst filmy"))
                return@flow
            }

            // Mapování dat z API na entity filmů
            val movieEntities = movieListFromApi.results.let {
                it.map { movieDto ->
                    movieDto.toMovieEntity(category)
                }
            }

            // Aktualizace lokální databáze s novými daty
            movieDatabase.movieDAO.upsertMovieList(movieEntities)

            // Emitování úspěšných dat
            emit(Resource.Success(
                movieEntities.map { it.toMovie(category) }
            ))

            // Emise zprávy o skončení načítání
            emit(Resource.Loading(false))
        }
    }

    // Metoda pro získání detailu filmu podle ID
    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> {
        return flow {
            // zobrazení zprávy o načítání
            emit(Resource.Loading(true))

            // Získání detailu filmu z lokální databáze
            val movieEntity = movieDatabase.movieDAO.getMovieById(id)

            if (movieEntity != null) {
                // Pokud je film nalezen v lokální databázi, zobrazí jej
                emit(
                    Resource.Success(movieEntity.toMovie(movieEntity.category))
                )

                // zobrazení zprávy o ukončení načítání
                emit(Resource.Loading(false))
                return@flow
            }

            // Pokud film není nalezen v lokální databázi, vypíše chybu
            emit(Resource.Error("Takový film v databázi neexistuje"))

            // zobrazení zprávy o ukončení načítání
            emit(Resource.Loading(false))
        }
    }
}
