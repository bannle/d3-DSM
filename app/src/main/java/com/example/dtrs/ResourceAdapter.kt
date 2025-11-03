package com.example.dtrs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import  android.widget.Button
import android.content.Intent
import android.net.Uri

class ResourceAdapter(
    private var resources: List<Resource>,
    private val onDeleteClick: (Resource) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgResource: ImageView = view.findViewById(R.id.imgResource)
        val titleTextView: TextView = view.findViewById(R.id.tvTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.tvDescription)
        val typeTextView: TextView = view.findViewById(R.id.tvType)
        val linkTextView: TextView = view.findViewById(R.id.tvLink)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resource, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resource = resources[position]

        holder.titleTextView.text = resource.title
        holder.descriptionTextView.text = resource.description
        holder.typeTextView.text = resource.type
        holder.linkTextView.text = "Ir al recurso"

        holder.linkTextView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.link))
            context.startActivity(intent)
        }

        Glide.with(holder.imgResource.context)
            .load(resource.image)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(holder.imgResource)

        holder.itemView.setOnClickListener {
            onItemClick?.onItemClick(resource)
        }


        holder.btnDelete.setOnClickListener {
            onDeleteClick(resource)
        }

        holder.btnUpdate.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpdateResourceActivity::class.java).apply {
                putExtra("id", resource.id)
                putExtra("title", resource.title)
                putExtra("description", resource.description)
                putExtra("type", resource.type)
                putExtra("link", resource.link)
                putExtra("image", resource.image)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = resources.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClick = listener
    }

    fun updateList(newList: List<Resource>) {
        resources = newList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(resource: Resource)
    }
}
