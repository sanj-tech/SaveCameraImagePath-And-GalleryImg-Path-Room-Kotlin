package com.example.myapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pictures::class], version = 1, exportSchema = false)
abstract class ImageDatabaseInstance: RoomDatabase() {
    abstract fun imgDao():ImageDao
    companion object{
        var instance:ImageDatabaseInstance?=null

        @Synchronized
        fun getInstance(context: Context):ImageDatabaseInstance{
            if (instance==null){
                instance= Room.databaseBuilder(context.applicationContext,ImageDatabaseInstance::class.java,
                    "ImageDBFile").allowMainThreadQueries().build()
            }

            return instance!!
        }

    }

}