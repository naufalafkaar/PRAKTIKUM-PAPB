package com.example.praktikum.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tugas_kuliah")
@Parcelize
data class TugasKuliahEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "matkul")
    var matkul: String,

    @ColumnInfo(name = "detail_tugas")
    var detailTugas: String,

    @ColumnInfo(name = "selesai")
    var selesai: Boolean = false // default false saat dibuat
) : Parcelable
