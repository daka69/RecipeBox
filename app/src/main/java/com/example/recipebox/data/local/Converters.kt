package com.example.recipebox.data.local

import androidx.room.TypeConverter
import com.example.recipebox.domain.model.Ingredient
import com.example.recipebox.domain.model.NutritionInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>?): String {
        return gson.toJson(value ?: emptyList<Ingredient>())
    }

    @TypeConverter
    fun toIngredientList(value: String?): List<Ingredient> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<Ingredient>>() {}.type
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromNutritionInfo(value: NutritionInfo?): String {
        return gson.toJson(value ?: NutritionInfo())
    }

    @TypeConverter
    fun toNutritionInfo(value: String?): NutritionInfo {
        if (value.isNullOrBlank()) return NutritionInfo()
        return try {
            gson.fromJson(value, NutritionInfo::class.java) ?: NutritionInfo()
        } catch (e: Exception) {
            NutritionInfo()
        }
    }
}
