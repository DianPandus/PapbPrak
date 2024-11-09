package com.yanz.projectpapb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaMatkul: String,
    val detailTugas: String,
    val imageUri: String? = null, // Add this line to store the image URI
    val isDone: Boolean = false // Property for the completion status
)

