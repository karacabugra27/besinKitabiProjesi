package com.example.besinkitabiprojesi.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.besinkitabiprojesi.model.Besin

@Dao
interface BesinDAO {

    @Insert
    suspend fun insertAll(vararg besin : Besin) : List<Long>

    @Query("select * from besin")
    suspend fun getAllBesin() : List<Besin>

    @Query("select * from besin where uuid = :besinId")
    suspend fun getBesin(besinId : Int) : Besin

    @Query("delete from besin")
    suspend fun delete(besin : Besin)
}