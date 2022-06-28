package com.example.myapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    fun saveData(pictures:Pictures)

    @Query("Select * from Pictures order by columnId desc")
    fun getData():List<Pictures>

}