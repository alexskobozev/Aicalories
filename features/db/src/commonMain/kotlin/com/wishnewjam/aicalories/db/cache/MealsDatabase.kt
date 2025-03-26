package com.wishnewjam.aicalories.db.cache

import com.wishnewjam.aicalories.db.entity.MealEntry

interface MealsDatabase {
    fun insertMealEntry(mealEntry: MealEntry)
    fun getAllMealEntries(): List<MealEntry>
    fun getMealEntryById(id: Long): MealEntry?
    fun updateMealEntry(id: Long, mealEntry: MealEntry)
    fun deleteMealEntry(id: Long)
    fun searchMealEntriesByFoodName(foodName: String): List<MealEntry>
    fun getMealEntriesByDate(date: String): List<MealEntry>
    fun getMealEntriesByDateRange(startDate: String, endDate: String): List<MealEntry>
    fun getTotalCaloriesForDay(date: String): Int
}