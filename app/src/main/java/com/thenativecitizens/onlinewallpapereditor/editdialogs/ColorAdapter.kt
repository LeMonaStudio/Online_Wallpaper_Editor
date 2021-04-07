package com.thenativecitizens.onlinewallpapereditor.editdialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListColorsBinding

class ColorAdapter(private val colorSelectionListener: ColorSelectionListener): RecyclerView.Adapter<ColorAdapter.ViewHolder>(){

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
        val colorString = data[position]
        holder.bind(colorString, colorSelectionListener)
    }


    class ViewHolder private constructor(private val binding: ListColorsBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListColorsBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(colorString: String, colorSelectionListener: ColorSelectionListener) {
            binding.colorName = colorString
            binding.clickListener = colorSelectionListener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class ColorSelectionListener(val clickListener: (color: String) -> Unit){
    fun onClick(color: String) = clickListener(color)
}