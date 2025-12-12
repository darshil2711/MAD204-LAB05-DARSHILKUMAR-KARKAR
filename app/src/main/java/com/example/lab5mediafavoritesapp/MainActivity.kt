/*
 * Course: MAD204-01 Java Development for MA - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Main Activity handling media picking logic, database operations, and display.
 */
package com.example.lab5mediafavoritesapp

import android.content.Context
import android.content.Intent
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
    private val gson = Gson()

    private var currentUri: Uri? = null
    private var currentType: String = "image"

    // --- ACTIVITY RESULT LAUNCHERS ---
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        handlePickedMedia(uri, "image")
    }
    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        handlePickedMedia(uri, "video")
    }
    private val pickMultiple = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleMultiplePickedMedia(uris)
    }

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- View Initialization ---
        imgPreview = findViewById(R.id.imgPreview)
        videoPreview = findViewById(R.id.videoPreview)

        // --- Database Initialization ---
        database = FavoritesDatabase.getDatabase(this)
        setupRecyclerView()
        setupClickListeners()
        loadLastOpenedMedia()
    }

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
            Snackbar.make(binding.root, "${uris.size} items saved!", Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { displayMedia(Uri.parse(it.uri), it.type) },
            onDeleteClick = { deleteMedia(it) }
        )
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavorites.adapter = adapter

        lifecycleScope.launch {
            database.favoriteDao().getAllFavorites().collect { adapter.submitList(it) }
        }
    }

    private fun setupClickListeners() {
        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnPickVideo.setOnClickListener { pickVideo.launch("video/*") }
        binding.btnPickMultiple.setOnClickListener { pickMultiple.launch("*/*") }
        binding.btnAddToFav.setOnClickListener { addToFavorites() }
        binding.btnExport.setOnClickListener { exportFavoritesToJson() }
        binding.btnImport.setOnClickListener { importFavoritesFromJson() }
    }

    private fun addToFavorites() {
        currentUri?.let { uri ->
            lifecycleScope.launch {
                database.favoriteDao().insert(FavoriteMedia(uri = uri.toString(), type = currentType))
                Snackbar.make(binding.root, "Saved to favorites!", Snackbar.LENGTH_SHORT).show()
            }
        } ?: Snackbar.make(binding.root, "No media selected to save.", Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Displays the media in the correct view.
     * The permission should already be taken before this is called.
     */
    private fun displayMedia(uri: Uri, type: String) {
        binding.imgPreview.visibility = if (type == "image") View.VISIBLE else View.GONE
        binding.videoPreview.visibility = if (type == "video") View.VISIBLE else View.GONE

        if (type == "image") {
            binding.imgPreview.setImageURI(uri)
        } else {
            binding.videoPreview.setVideoURI(uri)
            binding.videoPreview.start()
        }
        saveLastOpenedMedia(uri, type)
    }

    private fun deleteMedia(media: FavoriteMedia) {
        lifecycleScope.launch {
            database.favoriteDao().delete(media)
            Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    lifecycleScope.launch { database.favoriteDao().insert(media) }
                }.show()
        }
    }

    private fun exportFavoritesToJson() {
        lifecycleScope.launch {
            val jsonString = gson.toJson(database.favoriteDao().getAllFavoritesAsList())
            Log.d("JSON_EXPORT", jsonString)
            Snackbar.make(binding.root, "Favorites exported to Logcat.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun importFavoritesFromJson() {
        val jsonString = """
            [] 
        """.trimIndent()
        if (jsonString.isBlank() || jsonString == "[]") {
            Snackbar.make(binding.root, "JSON string is empty. Paste data to import.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val typeToken = object : TypeToken<List<FavoriteMedia>>() {}.type
        val favoritesList: List<FavoriteMedia> = gson.fromJson(jsonString, typeToken)

        lifecycleScope.launch {
            favoritesList.forEach { database.favoriteDao().insert(it.copy(id = 0)) }
            Snackbar.make(binding.root, "Imported ${favoritesList.size} items.", Snackbar.LENGTH_SHORT).show()
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

    private fun loadLastOpenedMedia() {
        val prefs = getSharedPreferences("MediaPrefs", Context.MODE_PRIVATE)
        prefs.getString("lastUri", null)?.let { uriString ->
            val type = prefs.getString("lastType", "image")!!
            // Need to check if we still have permission
            val uri = Uri.parse(uriString)
            val hasPermission = contentResolver.persistedUriPermissions.any { it.uri == uri }
            if(hasPermission) {
                displayMedia(uri, type)
            }
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
