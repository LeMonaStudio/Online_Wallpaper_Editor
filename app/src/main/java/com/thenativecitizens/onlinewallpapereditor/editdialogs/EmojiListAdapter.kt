package com.thenativecitizens.onlinewallpapereditor.editdialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListEmojiViewBinding

class EmojiListAdapter(private val emojiSelectionListener: EmojiSelectionListener): RecyclerView.Adapter<EmojiListAdapter.ViewHolder>(){

    var data = listOf<String>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val ctx = holder.itemView.context
        val emojiString = data[position]
        holder.bind(emojiString, emojiSelectionListener)
    }


    class ViewHolder private constructor(private val binding: ListEmojiViewBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListEmojiViewBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(emojiString: String, emojiSelectionListener: EmojiSelectionListener) {
            binding.emojiString = emojiString
            binding.clickListener = emojiSelectionListener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class EmojiSelectionListener(val clickListener: (emoji: String) -> Unit){
    fun onClick(emoji: String) = clickListener(emoji)
}