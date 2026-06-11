package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.repository.RecipeRepository

class GetRandomTriviaUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(): String {
        var trivia = repository.getRandomTrivia()
        
        // Spoonacular API sometimes embeds Google Ads JS snippets or HTML in their trivia string.
        // We need to sanitize it.
        val scriptIndex = trivia.indexOf("(adsbygoogle")
        if (scriptIndex != -1) {
            trivia = trivia.substring(0, scriptIndex)
        }
        
        // Remove any lingering HTML tags
        trivia = trivia.replace(Regex("<[^>]*>"), "")
        
        return trivia.trim()
    }
}
