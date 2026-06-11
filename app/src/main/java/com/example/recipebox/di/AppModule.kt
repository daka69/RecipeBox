package com.example.recipebox.di

import android.content.Context
import com.example.recipebox.data.database.RecipeDatabase
import com.example.recipebox.data.local.dao.RecipeDao
import com.example.recipebox.data.remote.RetrofitClient
import com.example.recipebox.data.remote.api.RecipeApiService
import com.example.recipebox.data.repository.RecipeRepositoryImpl
import com.example.recipebox.data.translation.TranslationService
import com.example.recipebox.domain.repository.RecipeRepository
import com.example.recipebox.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RecipeDatabase {
        return RecipeDatabase.getDatabase(context)
    }

    @Provides
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideApiService(): RecipeApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideTranslationService(): TranslationService {
        return TranslationService()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        apiService: RecipeApiService,
        dao: RecipeDao
    ): RecipeRepository {
        return RecipeRepositoryImpl(apiService, dao)
    }

    @Provides
    @Singleton
    fun provideGetPublicRecipesUseCase(repository: RecipeRepository): GetPublicRecipesUseCase {
        return GetPublicRecipesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPersonalRecipesUseCase(repository: RecipeRepository): GetPersonalRecipesUseCase {
        return GetPersonalRecipesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBookmarkedRecipesUseCase(repository: RecipeRepository): GetBookmarkedRecipesUseCase {
        return GetBookmarkedRecipesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRecipeDetailUseCase(repository: RecipeRepository): GetRecipeDetailUseCase {
        return GetRecipeDetailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddRecipeUseCase(repository: RecipeRepository): AddRecipeUseCase {
        return AddRecipeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateRecipeUseCase(repository: RecipeRepository): UpdateRecipeUseCase {
        return UpdateRecipeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteRecipeUseCase(repository: RecipeRepository): DeleteRecipeUseCase {
        return DeleteRecipeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleBookmarkUseCase(repository: RecipeRepository): ToggleBookmarkUseCase {
        return ToggleBookmarkUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchRecipesUseCase(repository: RecipeRepository): SearchRecipesUseCase {
        return SearchRecipesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMealPlanUseCase(repository: RecipeRepository): GetMealPlanUseCase {
        return GetMealPlanUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRandomTriviaUseCase(repository: RecipeRepository): GetRandomTriviaUseCase {
        return GetRandomTriviaUseCase(repository)
    }
}
