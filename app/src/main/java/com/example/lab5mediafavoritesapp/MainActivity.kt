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