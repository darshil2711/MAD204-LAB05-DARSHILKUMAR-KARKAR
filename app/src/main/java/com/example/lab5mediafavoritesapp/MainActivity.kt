/*
 * Course: MAD204-01 Java Development for MA - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: Main Activity to pick, display, save, and manage media favorites.
 */
package com.example.lab5mediafavoritesapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab5mediafavoritesapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FavoritesDatabase.getDatabase(this)
        setupRecyclerView()
        setupClickListeners()
        loadLastOpenedMedia()
    }


    /**
     * Handles the result from a single media picker.
     * It takes persistent permission and then displays the media.
     */
    private fun handlePickedMedia(uri: Uri?, type: String) {
        uri?.let {

            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            currentUri = it
            currentType = type
            displayMedia(it, type)
        }
    }

    /**
     * Handles the result from the multiple media picker.
     */
    private fun handleMultiplePickedMedia(uris: List<Uri>) {
        if (uris.isEmpty()) return

        lifecycleScope.launch {
            uris.forEach { uri ->
                val type = if (contentResolver.getType(uri)?.startsWith("image") == true) "image" else "video"
                // Take permission for each file
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                database.favoriteDao().insert(FavoriteMedia(uri = uri.toString(), type = type))
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

    private fun saveLastOpenedMedia(uri: Uri, type: String) {
        getSharedPreferences("MediaPrefs", Context.MODE_PRIVATE).edit().apply {
            putString("lastUri", uri.toString())
            putString("lastType", type)
            apply()
        }
    }

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
}
