package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberRegistrationHistoryDao {

    @Query("SELECT * FROM member_registration_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<MemberRegistrationHistoryEntity>>

    @Query("SELECT * FROM member_registration_history WHERE district = :district ORDER BY timestamp DESC")
    fun getHistoryByDistrict(district: String): Flow<List<MemberRegistrationHistoryEntity>>

    @Query("SELECT * FROM member_registration_history WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getHistoryByMemberId(memberId: String): Flow<List<MemberRegistrationHistoryEntity>>

    @Query("SELECT * FROM member_registration_history WHERE actionType = :actionType ORDER BY timestamp DESC")
    fun getHistoryByActionType(actionType: String): Flow<List<MemberRegistrationHistoryEntity>>

    @Query("SELECT * FROM member_registration_history WHERE memberName LIKE '%' || :query || '%' OR memberId LIKE '%' || :query || '%' OR district LIKE '%' || :query || '%' OR details LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<MemberRegistrationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: MemberRegistrationHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHistory(histories: List<MemberRegistrationHistoryEntity>)

    @Query("DELETE FROM member_registration_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM member_registration_history")
    suspend fun clearAllHistory()

    @Query("SELECT COUNT(*) FROM member_registration_history")
    suspend fun getHistoryCount(): Int
}
