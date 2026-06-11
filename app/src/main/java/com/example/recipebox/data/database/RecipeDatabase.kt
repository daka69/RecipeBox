package com.example.recipebox.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipebox.data.local.dao.RecipeDao
import com.example.recipebox.data.local.entity.RecipeEntity
import com.example.recipebox.data.local.Converters
@Database(entities = [RecipeEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN ingredientsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN summary TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN nutritionJson TEXT NOT NULL DEFAULT '{}'")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN healthScore INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN isVegetarian INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN isVegan INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN isGlutenFree INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN isDairyFree INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE personal_recipes ADD COLUMN isCachedPublic INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}