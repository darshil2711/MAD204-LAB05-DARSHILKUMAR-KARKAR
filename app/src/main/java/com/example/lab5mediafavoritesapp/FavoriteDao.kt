/*
 * Course: MAD204-01 Java Development for MA - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Data Access Object (DAO) for the FavoriteMedia entity.
 */
package com.example.lab5mediafavoritesapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    /**
     * Inserts a media item. If it already exists, it's replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: FavoriteMedia)

    /**
     * Deletes a media item from the database.
     */
    @Delete
    suspend fun delete(media: FavoriteMedia)

    /**
     * Retrieves all favorite media items, ordered by newest first.
     * Returns a Flow for automatic UI updates.
     */
    @Query("SELECT * FROM favorite_media ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<FavoriteMedia>>

    /**
     * Retrieves all favorite media items as a simple List.
     * Used for one-time operations like JSON export.
     */
    @Query("SELECT * FROM favorite_media")
    suspend fun getAllFavoritesAsList(): List<FavoriteMedia>
}
