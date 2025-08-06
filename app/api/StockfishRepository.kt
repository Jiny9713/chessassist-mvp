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

        /**
         * Create a repository with a default Retrofit instance.
         */
        fun create(
            baseUrl: String = "https://your-stockfish-server.com/",
            client: OkHttpClient? = null
        ): StockfishRepository {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client ?: OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return StockfishRepository(retrofit.create(StockfishApi::class.java))
        }
    }

    /**
     * Send [fen] to the analysis server and return the best move.
     * @param apiKey optional API key for Authorization header.
     */
    suspend fun analyzeFen(
        fen: String,
        depth: Int = 15,
        multiPv: Int = 1,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        try {
            val request = AnalysisRequest(fen, depth, multiPv)
            val header = apiKey?.let { "Bearer $it" }
            val response = api.analyzeFen(request, header)
            response.bestMove ?: "N/A"
        } catch (e: Exception) {
            Log.e(TAG, "Stockfish analysis failed", e)
            "Error"
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
