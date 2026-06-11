package com.example.recipebox.data.translation

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TranslationService {

    private val enToIdOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.INDONESIAN)
        .build()

    private val translator = Translation.getClient(enToIdOptions)
    private var isModelReady = false
    private val mutex = Mutex()

    // Cache translations to avoid re-translating
    private val cache = mutableMapOf<String, String>()

    suspend fun ensureModelDownloaded(): Boolean {
        if (isModelReady) return true
        return mutex.withLock {
            if (isModelReady) return true
            try {
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                translator.downloadModelIfNeeded(conditions).await()
                isModelReady = true
                true
            } catch (e: Exception) {
                // Try without wifi requirement
                try {
                    val conditions = DownloadConditions.Builder().build()
                    translator.downloadModelIfNeeded(conditions).await()
                    isModelReady = true
                    true
                } catch (e2: Exception) {
                    false
                }
            }
        }
    }

    suspend fun translate(text: String): String {
        if (text.isBlank()) return text
        // Check cache first
        cache[text]?.let { return it }

        return try {
            if (!isModelReady) {
                ensureModelDownloaded()
            }
            val result = translator.translate(text).await()
            cache[text] = result
            result
        } catch (e: Exception) {
            text // Return original if translation fails
        }
    }

    suspend fun translateBatch(texts: List<String>): List<String> {
        if (!isModelReady) {
            val downloaded = ensureModelDownloaded()
            if (!downloaded) return texts
        }
        return texts.map { translate(it) }
    }

    fun clearCache() {
        cache.clear()
    }

    fun isReady(): Boolean = isModelReady
}
