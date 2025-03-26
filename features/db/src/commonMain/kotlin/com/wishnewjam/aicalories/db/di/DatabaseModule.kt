package com.wishnewjam.aicalories.db.di

import com.wishnewjam.aicalories.db.cache.MealsDatabase
import com.wishnewjam.aicalories.db.cache.MealsDatabaseImpl
import com.wishnewjam.aicalories.db.platform.dbDriverFactory
import org.koin.dsl.module

val mealsDatabaseImplModule = module {
    single<MealsDatabase> {
        MealsDatabaseImpl(databaseDriverFactory = dbDriverFactory())
    }
}