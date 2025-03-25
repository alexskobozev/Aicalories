package com.wishnewjam.aicalories.db.cache

import com.wishnewjam.aicalories.db.entity.MealEntry

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AiCaloriesMainDb(databaseDriverFactory.createDriver())
    private val dbQuery = database.aiCaloriesMainDbQueries

    // MealEntry operations

    internal fun insertMealEntry(mealEntry: MealEntry) {
        dbQuery.insertMealEntry(
            food_name = mealEntry.foodName,
            meal_calories = mealEntry.calories.toLong(),
            meal_weight = mealEntry.weight.toLong(),
            comment = mealEntry.comment,
            meal_date = mealEntry.dateUtC
        )
    }

    internal fun getAllMealEntries(): List<MealEntry> {
        return dbQuery.getAllMealEntries().executeAsList().map {
            it.toMealEntry()
        }
    }

    internal fun getMealEntryById(id: Long): MealEntry? {
        return dbQuery.getMealEntryById(id).executeAsOneOrNull()?.toMealEntry()
    }

    internal fun updateMealEntry(id: Long, mealEntry: MealEntry) {
        dbQuery.updateMealEntry(
            food_name = mealEntry.foodName,
            meal_calories = mealEntry.calories.toLong(),
            meal_weight = mealEntry.weight.toLong(),
            comment = mealEntry.comment,
            meal_date = mealEntry.dateUtC,
            id = id
        )
    }

    internal fun deleteMealEntry(id: Long) {
        dbQuery.deleteMealEntry(id)
    }

    internal fun searchMealEntriesByFoodName(foodName: String): List<MealEntry> {
        return dbQuery.searchMealEntriesByFoodName(foodName).executeAsList().map {
            it.toMealEntry()
        }
    }

    internal fun getMealEntriesByDate(date: String): List<MealEntry> {
        return dbQuery.getMealEntriesByDate(date).executeAsList().map {
            it.toMealEntry()
        }
    }

    internal fun getMealEntriesByDateRange(startDate: String, endDate: String): List<MealEntry> {
        return dbQuery.getMealEntriesByDateRange(startDate, endDate).executeAsList().map {
            it.toMealEntry()
        }
    }

    internal fun getTotalCaloriesForDay(date: String): Int {
        return dbQuery.getTotalCaloriesForDay(date).executeAsOneOrNull()?.SUM?.toInt() ?: 0
    }

    // Extension function to convert database entity to domain model
    private fun com.wishnewjam.aicalories.db.cache.MealEntry.toMealEntry(): MealEntry {
        return MealEntry(
            foodName = food_name,
            calories = meal_calories.toInt(),
            weight = meal_weight.toInt(),
            comment = comment,
            dateUtC = meal_date
        )
    }
}