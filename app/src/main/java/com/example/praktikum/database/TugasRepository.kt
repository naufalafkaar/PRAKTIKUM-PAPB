package com.example.praktikum.database

import android.app.Application
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val tugasDao: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = TugasDB.getDatabase(application)
        tugasDao = db.tugasDao()
    }

    suspend fun getAllTugas(): List<Tugas> = tugasDao.getAllTugas()

    suspend fun insertTugas(tugas: Tugas) {
        tugasDao.insertTugas(tugas)
    }

    suspend fun deleteTugas(tugas: Tugas) {
        tugasDao.deleteTugas(tugas)
    }


    suspend fun updateTugas(tugas: Tugas) {
        tugasDao.updateTugas(tugas)
    }
}
