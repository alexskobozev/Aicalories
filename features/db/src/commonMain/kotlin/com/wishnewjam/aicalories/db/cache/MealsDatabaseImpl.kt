package com.wishnewjam.aicalories.db.cache

import app.cash.sqldelight.coroutines.asFlow
import com.wishnewjam.aicalories.db.entity.MealEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MealsDatabaseImpl(databaseDriverFactory: DatabaseDriverFactory) : MealsDatabase {
    private val database = AiCaloriesMainDb(databaseDriverFactory.createDriver())
    private val dbQuery = database.aiCaloriesMainDbQueries

    // MealEntry operations

     override fun insertMealEntry(mealEntry: MealEntry) {
        dbQuery.insertMealEntry(
            food_name = mealEntry.foodName,
            meal_calories = mealEntry.calories.toLong(),
            meal_weight = mealEntry.weight.toLong(),
            comment = mealEntry.comment,
            meal_date = mealEntry.dateUtC
        )
    }

    override fun getAllMealEntries(): Flow<List<MealEntry>> =
        dbQuery.getAllMealEntries().asFlow().map { query ->
            query.executeAsList().map { it.toMealEntry() }
        }

    override fun getMealEntryById(id: Long): MealEntry? {
        return dbQuery.getMealEntryById(id).executeAsOneOrNull()?.toMealEntry()
    }

    override fun updateMealEntry(id: Long, mealEntry: MealEntry) {
        dbQuery.updateMealEntry(
            food_name = mealEntry.foodName,
            meal_calories = mealEntry.calories.toLong(),
            meal_weight = mealEntry.weight.toLong(),
            comment = mealEntry.comment,
            meal_date = mealEntry.dateUtC,
            id = id
        )
    }

    override fun deleteMealEntry(id: Long) {
        dbQuery.deleteMealEntry(id)
    }

    override fun searchMealEntriesByFoodName(foodName: String): List<MealEntry> {
        return dbQuery.searchMealEntriesByFoodName(foodName).executeAsList().map {
            it.toMealEntry()
        }
    }

    override fun getMealEntriesByDate(date: String): List<MealEntry> {
        return dbQuery.getMealEntriesByDate(date).executeAsList().map {
            it.toMealEntry()
        }
    }

    override fun getMealEntriesByDateRange(startDate: String, endDate: String): List<MealEntry> {
        return dbQuery.getMealEntriesByDateRange(startDate, endDate).executeAsList().map {
            it.toMealEntry()
        }
    }

    override fun getTotalCaloriesForDay(date: String): Int {
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