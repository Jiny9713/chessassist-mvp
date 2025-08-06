package com.example.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/** Request body for Stockfish analysis. */
data class AnalysisRequest(
    val fen: String,
    val depth: Int = 15,
    @SerializedName("multi_pv") val multiPv: Int = 1
)

/** Evaluation information returned by the server. */
data class Evaluation(
    val type: String,
    val value: Int
)

/** Response containing Stockfish analysis. */
data class AnalysisResponse(
    @SerializedName("best_move") val bestMove: String?,
    val pv: List<String>?,
    val evaluation: Evaluation?
)

/** Retrofit API for the Stockfish analysis server. */
interface StockfishApi {
    @POST("api/analyze")
    suspend fun analyzeFen(
        @Body request: AnalysisRequest,
        @Header("Authorization") apiKey: String? = null // e.g., "Bearer YOUR_API_KEY"
    ): AnalysisResponse
}
