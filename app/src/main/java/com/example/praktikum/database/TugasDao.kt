// TugasKuliahDao.kt
package com.example.praktikum.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDAO {
    @Query("SELECT * FROM tugas")
    fun getAllTugas(): List<Tugas>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)

    @Update
    fun updateTugas(tugas: Tugas)
}
