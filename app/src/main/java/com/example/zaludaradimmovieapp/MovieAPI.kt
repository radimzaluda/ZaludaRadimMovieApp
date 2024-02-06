package com.example.zaludaradimmovieapp

interface MovieAPI {
    @GET("movie/{category}")
    suspend fun getMovieList(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query ("api_key") api_key: String = API_KEY
    )

    companion object {
        const val BASE_URL = "https://api.themovedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        const val API_KEY = "829f9eb292a2886997a7ed00b75c44e1"

    }
}