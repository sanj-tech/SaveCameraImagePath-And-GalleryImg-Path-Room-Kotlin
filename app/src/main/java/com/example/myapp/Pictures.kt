package com.example.myapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Pictures(var galleryImage:String,var cameraImage:String) {
    @PrimaryKey(autoGenerate = true)
    var columnId:Int=0
}