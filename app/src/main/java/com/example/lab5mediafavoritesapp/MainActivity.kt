/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Main Activity handling media picking logic, database operations, and display.
 */

package com.example.lab5mediafavoritesapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.ui.input.key.type
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var imgPreview: ImageView
    private lateinit var videoPreview: VideoView

    // Media URI and Type
    private var currentUri: Uri? = null
    private var currentType: String = "image"

    // Database and Adapter
    private lateinit var database: FavoritesDatabase
    private lateinit var adapter: FavoritesAdapter

    // Picker for a single image
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentUri = it
            currentType = "image"
            displayMedia(it, "image")
        }
    }

    // Picker for a single video
    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentUri = it
            currentType = "video"
            displayMedia(it, "video")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- View Initialization ---
        imgPreview = findViewById(R.id.imgPreview)
        videoPreview = findViewById(R.id.videoPreview)

        // --- Database Initialization ---
        database = FavoritesDatabase.getDatabase(this)

        // --- RecyclerView Setup ---
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavorites)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = FavoritesAdapter(
            onDeleteClick = { media -> deleteMedia(media) },
            onItemClick = { media -> displayMedia(Uri.parse(media.uri), media.type) }
        )
        recycler.adapter = adapter

        // --- Load Data into RecyclerView ---
        lifecycleScope.launch {
            database.favoriteDao().getAllFavorites().collect { list ->
                adapter.submitList(list)
            }
        }

        // --- Button Click Listeners ---
        findViewById<Button>(R.id.btnPickImage).setOnClickListener { pickImage.launch("image/*") }
        findViewById<Button>(R.id.btnPickVideo).setOnClickListener { pickVideo.launch("video/*") }

        findViewById<Button>(R.id.btnAddToFav).setOnClickListener {
            currentUri?.let { uri ->
                val media = FavoriteMedia(uri = uri.toString(), type = currentType)
                lifecycleScope.launch {
                    database.favoriteDao().insert(media)
                    Snackbar.make(findViewById(R.id.recyclerFavorites), "Saved!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Displays the selected media (image or video) in the appropriate view.
     * @param uri The URI of the media to display.
     * @param type A string indicating the media type ("image" or "video").
     */
    private fun displayMedia(uri: Uri, type: String) {
        // Persist permission to access the URI across device reboots
        val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, flag)

        if (type == "image") {
            imgPreview.visibility = View.VISIBLE
            videoPreview.visibility = View.GONE
            imgPreview.setImageURI(uri)
        } else {
            imgPreview.visibility = View.GONE
            videoPreview.visibility = View.VISIBLE
            videoPreview.setVideoURI(uri)
            videoPreview.start()
        }
    }

    /**
     * Deletes a media item from the database and shows a Snackbar with an UNDO option.
     * @param media The FavoriteMedia object to delete.
     */
    private fun deleteMedia(media: FavoriteMedia) {
        lifecycleScope.launch {
            database.favoriteDao().delete(media)

            Snackbar.make(findViewById(R.id.recyclerFavorites), "Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    lifecycleScope.launch { database.favoriteDao().insert(media) }
                }.show()
        }
    }
}
