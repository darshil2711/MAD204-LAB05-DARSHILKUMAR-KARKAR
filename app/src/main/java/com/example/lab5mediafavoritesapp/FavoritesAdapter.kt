/*
 * Course: MAD204-01 Java Development for MA - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: RecyclerView adapter for displaying the list of favorite media.
 */
package com.example.lab5mediafavoritesapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(
    private val onItemClick: (FavoriteMedia) -> Unit,
    private val onDeleteClick: (FavoriteMedia) -> Unit
) : ListAdapter<FavoriteMedia, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onDeleteClick)
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mediaIcon: ImageView = itemView.findViewById(R.id.mediaIcon)
        private val mediaUriText: TextView = itemView.findViewById(R.id.mediaUriText)
        private val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)

        fun bind(
            media: FavoriteMedia,
            onItemClick: (FavoriteMedia) -> Unit,
            onDeleteClick: (FavoriteMedia) -> Unit
        ) {
            mediaUriText.text = Uri.parse(media.uri).lastPathSegment // Show a cleaner name
            mediaIcon.setImageResource(
                if (media.type == "image") R.drawable.ic_image else R.drawable.ic_video
            )
            itemView.setOnClickListener { onItemClick(media) }
            deleteButton.setOnClickListener { onDeleteClick(media) }
        }
    }
}

class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteMedia>() {
    override fun areItemsTheSame(oldItem: FavoriteMedia, newItem: FavoriteMedia) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FavoriteMedia, newItem: FavoriteMedia) = oldItem == newItem
}
