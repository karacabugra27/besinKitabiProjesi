package com.example.besinkitabiprojesi.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.besinkitabiprojesi.model.Besin

@Database(entities = [Besin::class], version = 1)
abstract class BesinDatabase : RoomDatabase(){
    abstract fun besinDao() : BesinDAO
}