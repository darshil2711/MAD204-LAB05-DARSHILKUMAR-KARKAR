/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Entity class representing a media item in the database.
 */

package com.example.lab5mediafavoritesapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class FavoriteMedia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val type: String, // "image" or "video"
    val timestamp: Long = System.currentTimeMillis()
)