package com.example.myapplication

import androidx.room.Dao
import androidx.room.Query

@Dao
interface KanjiDao {
    @Query("SELECT groop_id, MIN(place) as place FROM kanji WHERE place != 0 GROUP BY groop_id ORDER BY groop_id ASC LIMIT 1")
    suspend fun findGroopWithMinPlace(): GroopWithPlace?

    @Query("UPDATE kanji SET groop_id = :newGroopId WHERE id = :id")
    suspend fun updateGroopId(id: Int, newGroopId: Int)

    @Query("UPDATE kanji SET place = :place WHERE id = :id")
    suspend fun updatePlace(id: Int, place: Int)

    @Query("SELECT MAX(place) FROM kanji WHERE groop_id = :groopId")
    suspend fun getMaxPlace(groopId: Int): Int?

    @Query("SELECT id FROM kanji WHERE groop_id = :groopId ORDER BY place ASC LIMIT 1")
    suspend fun findMinPlaceKanji(groopId: Int): Int?

    @Query("SELECT groop_id, COUNT(*) AS count FROM kanji WHERE groop_id IN (:groopIds) GROUP BY groop_id")
    suspend fun getCountsWhereGroopIdIn(groopIds: List<Int>): List<GroupCount>

    @Query("SELECT groop_id FROM kanji WHERE id = :cardId LIMIT 1")
    suspend fun getGroopId(cardId: Int): Int

    @Query("SELECT * FROM Kanji WHERE id = :id")
    suspend fun getKanjiById(id: Int): Kanji

}

data class GroupCount(val groop_id: Int, val count: Int)