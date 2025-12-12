/*
 * Course: MAD204 - Lab 5
 * Student: Darshilkumar Karkar (A00203357)
 * Date: 2025-12-11
 * Description: RecyclerView Adapter for displaying favorites.
 */

package com.example.lab5mediafavoritesapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(
    private val onDeleteClick: (FavoriteMedia) -> Unit,
    private val onItemClick: (FavoriteMedia) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    private var items = listOf<FavoriteMedia>()

    fun submitList(newItems: List<FavoriteMedia>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val type: TextView = view.findViewById(R.id.itemType)
        val delete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.type.text = "Type: ${item.type.uppercase()}"

        try {
            holder.icon.setImageURI(Uri.parse(item.uri))
        } catch (e: Exception) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.delete.setOnClickListener { onDeleteClick(item) }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}