package com.example.workout.data.db

import android.content.Context
import android.content.res.Resources
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.workout.R
import com.example.workout.data.db.entity.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(version = 2, entities = [Exercise::class], exportSchema = false)
abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    private class ExerciseDatabaseCallback(
        private val scope: CoroutineScope,
        private val resources: Resources
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val exerciseDao = database.exerciseDao()
                    prePopulateDatabase(exerciseDao)
                }
            }
        }

        private suspend fun prePopulateDatabase(exerciseDao: ExerciseDao) {
            val jsonString = resources.openRawResource(R.raw.exercises).bufferedReader().use {
                it.readText()
            }
            val typeToken = object : TypeToken<List<Exercise>>() {}.type
            val exercises = Gson().fromJson<List<Exercise>>(jsonString, typeToken)
            exerciseDao.insertAllExercises(exercises)
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: ExerciseDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope, resources: Resources): ExerciseDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    ExerciseDatabase::class.java,
                    "players_database")
                    .addCallback(ExerciseDatabaseCallback(coroutineScope, resources))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}