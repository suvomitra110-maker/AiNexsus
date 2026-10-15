package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStreakDao {

    @Query("SELECT * FROM user_streak_record WHERE id = 1 LIMIT 1")
    fun getStreakRecordFlow(): Flow<UserStreakEntity?>

    @Query("SELECT * FROM user_streak_record WHERE id = 1 LIMIT 1")
    suspend fun getStreakRecord(): UserStreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStreakRecord(record: UserStreakEntity)
}
