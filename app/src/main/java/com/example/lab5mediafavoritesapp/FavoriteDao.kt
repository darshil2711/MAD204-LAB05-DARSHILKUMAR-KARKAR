/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Data Access Object for database operations.
 */

package com.example.lab5mediafavoritesapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    // Insert a new media item
    @Insert
    suspend fun insert(media: FavoriteMedia)

    // Get all favorites (using Flow for live updates)
    @Query("SELECT * FROM favorites_table ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteMedia>>

    // Get all favorites as a direct List (good for JSON export)
    @Query("SELECT * FROM favorites_table")
    suspend fun getAllFavoritesList(): List<FavoriteMedia>

    // Delete a specific item
    @Delete
    suspend fun delete(media: FavoriteMedia)
}