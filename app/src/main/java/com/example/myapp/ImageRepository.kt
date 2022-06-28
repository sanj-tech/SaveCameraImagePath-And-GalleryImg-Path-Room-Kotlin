package com.example.myapp

import android.content.Context

class ImageRepository(context:Context) {
    var imageDao=ImageDatabaseInstance.getInstance(context).imgDao()

    fun saveData(pictures: Pictures){
        imageDao.saveData(pictures)
    }

    fun getData():List<Pictures>{
       return imageDao.getData()
    }



}