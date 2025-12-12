/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Main Activity handling media picking logic.
 */

package com.example.lab5mediafavoritesapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // ... previous variables ...
    private lateinit var database: FavoritesDatabase
    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize DB
        database = FavoritesDatabase.getDatabase(this)

        // 2. Setup RecyclerView
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavorites)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = FavoritesAdapter(
            onDeleteClick = { media -> deleteMedia(media) },
            onItemClick = { media -> displayMedia(Uri.parse(media.uri), media.type) }
        )
        recycler.adapter = adapter

        // 3. Load Data
        lifecycleScope.launch {
            database.favoriteDao().getAllFavorites().collect { list ->
                adapter.submitList(list)
            }
        }

        // 4. Add to Favorites Logic
        findViewById<Button>(R.id.btnAddToFav).setOnClickListener {
            currentUri?.let { uri ->
                val media = FavoriteMedia(uri = uri.toString(), type = currentType)
                lifecycleScope.launch {
                    database.favoriteDao().insert(media)
                    Snackbar.make(findViewById(R.id.recyclerFavorites), "Saved!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
class MainActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private lateinit var videoPreview: VideoView
    private var currentUri: Uri? = null
    private var currentType: String = "image"

    // Picker for Images
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentUri = it
            currentType = "image"
            displayMedia(it, "image")
        }
    }

    // Picker for Videos
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

        imgPreview = findViewById(R.id.imgPreview)
        videoPreview = findViewById(R.id.videoPreview)

        findViewById<Button>(R.id.btnPickImage).setOnClickListener { pickImage.launch("image/*") }
        findViewById<Button>(R.id.btnPickVideo).setOnClickListener { pickVideo.launch("video/*") }
    }

}

private fun deleteMedia(media: FavoriteMedia) {
    lifecycleScope.launch {
        database.favoriteDao().delete(media)

        Snackbar.make(findViewById(R.id.recyclerFavorites), "Deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                lifecycleScope.launch { database.favoriteDao().insert(media) }
            }.show()
    }
}
    private fun displayMedia(uri: Uri, type: String) {
        // Persist permission so we can view it later
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
}