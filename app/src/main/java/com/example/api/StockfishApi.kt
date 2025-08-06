package com.example.api

import retrofit2.http.GET
import retrofit2.http.Query

/** Principal variation returned by Lichess cloud evaluation. */
data class PrincipalVariation(
    val moves: String,
    val cp: Int?,
    val mate: Int?,
    val depth: Int?
)

/** Response from the Lichess cloud evaluation API. */
data class CloudEvalResponse(
    val pvs: List<PrincipalVariation>?
)

/** Retrofit API for Lichess cloud evaluation. */
interface StockfishApi {
    @GET("cloud-eval")
    suspend fun analyzeFen(
        @Query("fen") fen: String,
        @Query("depth") depth: Int = 15,
        @Query("multiPv") multiPv: Int = 1
    ): CloudEvalResponse
}
