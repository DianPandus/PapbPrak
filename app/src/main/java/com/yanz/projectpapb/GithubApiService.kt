package com.yanz.projectpapb

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Data model untuk GitHub Profile
data class GithubProfile(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val followers: Int,
    val following: Int
)

// Interface untuk endpoint API GitHub
interface GithubApiService {

    // Mendapatkan profil GitHub berdasarkan username
    @GET("users/{username}")
    suspend fun getProfile(@Path("username") username: String): GithubProfile

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        // Fungsi untuk membuat instance Retrofit
        fun create(): GithubApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Konverter untuk parsing JSON
                .build()

            // Menghasilkan implementasi dari API
            return retrofit.create(GithubApiService::class.java)
        }
    }
}
