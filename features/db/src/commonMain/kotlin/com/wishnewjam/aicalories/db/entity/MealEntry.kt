package com.wishnewjam.aicalories.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealEntry(
    @SerialName("food_name")
    val foodName: String,
    @SerialName("meal_calories")
    val calories: Int,
    @SerialName("meal_weight")
    val weight: Int,
    @SerialName("comment")
    val comment: String,
    @SerialName("meal_date")
    var dateUtC: String,
)