/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Entity class representing a single favorite media item in the database.
 */

package com.example.lab5mediafavoritesapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_media")
data class FavoriteMedia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val type: String
)
