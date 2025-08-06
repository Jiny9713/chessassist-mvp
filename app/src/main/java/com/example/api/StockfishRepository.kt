package com.example.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository communicating with a Stockfish analysis server.
 */
class StockfishRepository(private val api: StockfishApi) {

    companion object {
        private const val TAG = "StockfishRepository"
        private const val BASE_URL = "https://실제서버주소/"

        /**
         * Create a repository with a default Retrofit instance.
         */
        fun create(
            baseUrl: String = BASE_URL,
            client: OkHttpClient? = null,
            apiKey: String? = null
        ): StockfishRepository {
            val httpClient = client ?: OkHttpClient.Builder().apply {
                if (apiKey != null) {
                    addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $apiKey")
                            .build()
                        chain.proceed(request)
                    }
                }
            }.build()
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return StockfishRepository(retrofit.create(StockfishApi::class.java))
        }
    }

    /**
     * Send [fen] to the analysis server and return the best move.
     */
    suspend fun analyzeFen(
        fen: String,
        depth: Int = 15,
        multiPv: Int = 1
    ): String = withContext(Dispatchers.IO) {
        try {
            val request = AnalysisRequest(fen, depth, multiPv)
            val response = api.analyzeFen(request)
            response.bestMove ?: "N/A"
        } catch (e: Exception) {
            Log.e(TAG, "Stockfish analysis failed", e)
            "Error: ${e.message}"
        }
    }
}

/**
 * Example function showing how to call the API.
 */
suspend fun analyzeFen(fen: String): String {
    // Requires android.permission.INTERNET in AndroidManifest.
    val repository = StockfishRepository.create()
    return repository.analyzeFen(fen)
}
