//text adapter for saved text summaries
package com.example.Text_Summarizer.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.modules.ViewSavedSummmeryScreen

data class Text(
    val id: Long,
    val title: String,
    val description: String,
    val date: String
)

class TextAdapter(private val onLongClick: (Text) -> Unit) : ListAdapter<Text, TextAdapter.TextViewHolder>(TextDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_card, parent, false)
        return TextViewHolder(view, onLongClick)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val text = getItem(position)
        holder.bind(text)
    }

    class TextViewHolder(itemView: View, private val onLongClick: (Text) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_card_title)
        private val description: TextView = itemView.findViewById(R.id.tv_card_description)
        private val date: TextView = itemView.findViewById(R.id.tv_card_date)

        fun bind(text: Text) {
            title.text = text.title
            description.text = text.description
            date.text = text.date

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ViewSavedSummmeryScreen::class.java)
                intent.putExtra("TEXT_ID", text.id)
                context.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                onLongClick(text)
                true
            }
        }
    }

    class TextDiffCallback : DiffUtil.ItemCallback<Text>() {
        override fun areItemsTheSame(oldItem: Text, newItem: Text): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Text, newItem: Text): Boolean {
            return oldItem == newItem
        }
    }
}