package com.example.api

import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/** Response from chessvision.ai containing FEN notation. */
data class ChessvisionResponse(val fen: String)

/** Retrofit API definition for chessvision.ai recognition endpoint. */
interface ChessvisionApi {
    @Multipart
    @POST("v1/recognize")
    suspend fun recognizeBoard(
        @Part image: MultipartBody.Part,
        @Header("Authorization") apiKey: String? = null // e.g., "Bearer YOUR_API_KEY"
    ): ChessvisionResponse
}
