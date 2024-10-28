package com.yanz.projectpapb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: Tugas)

    @Query("SELECT * FROM tugas")
    fun getAllTugas(): Flow<List<Tugas>>
}

