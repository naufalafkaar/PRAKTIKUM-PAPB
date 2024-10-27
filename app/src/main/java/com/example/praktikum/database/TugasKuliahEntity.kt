package com.example.praktikum.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.praktikum.MataKuliah
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tugas_kuliah")
@Parcelize
data class TugasKuliahEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "matkul")
    var mataKuliah: String,

    @ColumnInfo(name = "detail_tugas")
    var detailTugas: String,

    @ColumnInfo(name = "selesai")
    var isDone: Boolean = false // default false saat dibuat
) : Parcelable
