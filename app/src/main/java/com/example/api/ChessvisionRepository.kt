package com.example.api

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Repository that uploads a bitmap to chessvision.ai and extracts the FEN string.
 */
class ChessvisionRepository(private val api: ChessvisionApi) {

    companion object {
        private const val TAG = "ChessvisionRepository"

        /**
         * Create a repository with a default Retrofit instance.
         */
        fun create(baseUrl: String = "https://api.chessvision.ai/", client: OkHttpClient? = null): ChessvisionRepository {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client ?: OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return ChessvisionRepository(retrofit.create(ChessvisionApi::class.java))
        }
    }

    /**
     * Upload [bitmap] and return the FEN string.
     * @param apiKey optional API key for Authorization header.
     */
    suspend fun extractFenFromImage(bitmap: Bitmap, apiKey: String? = null): String = withContext(Dispatchers.IO) {
        val file = File.createTempFile("board", ".png")
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val header = apiKey?.let { "Bearer $it" }
            val response = api.recognizeBoard(body, header)
            response.fen
        } catch (e: Exception) {
            Log.e(TAG, "FEN extraction failed", e)
            throw e
        } finally {
            file.delete()
        }
    }
}

/**
 * Example function showing how to call the API.
 */
suspend fun sampleFenExtraction(bitmap: Bitmap) {
    // Requires android.permission.INTERNET in AndroidManifest.
    val repository = ChessvisionRepository.create()
    val fen = repository.extractFenFromImage(bitmap /*, apiKey = "YOUR_API_KEY" */)
    Log.d("ChessvisionSample", "FEN: $fen")
}
