// TugasKuliahDao.kt
package com.example.praktikum.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasKuliahDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: TugasKuliahEntity): Long // Mengembalikan ID dari item yang dimasukkan

    @Query("SELECT * FROM tugas_kuliah")
    fun getAllTugas(): Flow<List<TugasKuliahEntity>> // Menggunakan Flow untuk reaktivitas

    @Query("UPDATE tugas_kuliah SET selesai = :isDone WHERE id = :id")
    suspend fun updateTugasStatus(id: Int, isDone: Boolean): Int // Mengembalikan jumlah baris yang diperbarui
}

