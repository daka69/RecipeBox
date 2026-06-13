package com.example.recipebox.data.remote

import com.example.recipebox.data.remote.api.RecipeApiService
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.example.recipebox.BuildConfig

object RetrofitClient {
    private const val BASE_URL = "https://api.spoonacular.com/"
    private val API_KEYS = BuildConfig.SPOONACULAR_API_KEYS.split(",").filter { it.isNotBlank() }
    private var currentKeyIndex = 0

    // Interceptor to add apiKey to every request automatically
    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()
        
        // Guard against missing API key
        if (API_KEYS.isEmpty() || API_KEYS[0] == "null" || API_KEYS[0].isBlank() || API_KEYS[0].contains("YOUR_API_KEY")) {
            throw java.io.IOException("API Key is missing or invalid. Please set SPOONACULAR_API_KEY in local.properties")
        }
        
        // Get current key
        val currentKey = API_KEYS[currentKeyIndex]
        
        val url = original.url.newBuilder()
            .addQueryParameter("apiKey", currentKey)
            .build()
        val request = original.newBuilder().url(url).build()
        
        var response = chain.proceed(request)
        
        // If quota limit reached (402), automatically rotate to the next key and retry
        if (response.code == 402 && API_KEYS.size > 1) {
            response.close() // Close failed response
            
            // Move to the next key
            currentKeyIndex = (currentKeyIndex + 1) % API_KEYS.size
            val newKey = API_KEYS[currentKeyIndex]
            
            val newUrl = original.url.newBuilder()
                .addQueryParameter("apiKey", newKey)
                .build()
            val newRequest = original.newBuilder().url(newUrl).build()
            
            response = chain.proceed(newRequest)
        }
        
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .build()

    val apiService: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }
}