package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberCardDao {
    @Query("SELECT * FROM member_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<MemberCardEntity>>

    @Query("SELECT * FROM member_cards WHERE cardType = :cardType ORDER BY createdAt DESC")
    fun getCardsByType(cardType: String): Flow<List<MemberCardEntity>>

    @Query("SELECT * FROM member_cards WHERE district = :district ORDER BY createdAt DESC")
    fun getCardsByDistrict(district: String): Flow<List<MemberCardEntity>>

    @Query("SELECT * FROM member_cards WHERE approvalStatus = :status ORDER BY createdAt DESC")
    fun getCardsByApprovalStatus(status: String): Flow<List<MemberCardEntity>>

    @Query("SELECT * FROM member_cards WHERE id = :id")
    fun getCardById(id: Long): Flow<MemberCardEntity?>

    @Query("SELECT * FROM member_cards WHERE name LIKE '%' || :query || '%' OR memberId LIKE '%' || :query || '%' OR district LIKE '%' || :query || '%' OR firmName LIKE '%' || :query || '%' OR utrNumber LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCards(query: String): Flow<List<MemberCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: MemberCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<MemberCardEntity>)

    @Update
    suspend fun updateCard(card: MemberCardEntity)

    @Query("UPDATE member_cards SET approvalStatus = :status, approvedBy = :approvedBy, approvedAt = :approvedAt, rejectionReason = :reason WHERE id = :id")
    suspend fun updateApprovalStatus(id: Long, status: String, approvedBy: String, approvedAt: Long, reason: String?)

    @Delete
    suspend fun deleteCard(card: MemberCardEntity)

    @Query("DELETE FROM member_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("SELECT COUNT(*) FROM member_cards")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM member_cards WHERE approvalStatus = 'PENDING'")
    suspend fun getPendingCount(): Int
}
