package com.thenativecitizens.onlinewallpapereditor.ui.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListFiltersViewBinding

class FilterListAdapter(private val listener: FilterSelectionListener): RecyclerView.Adapter<FilterListAdapter.ViewHolder>(){

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
        val filter = data[position]
        holder.bind(filter, listener)
    }


    class ViewHolder private constructor(private val binding: ListFiltersViewBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListFiltersViewBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(filter: String, listener: FilterSelectionListener) {
            binding.filter = filter
            binding.listener = listener
            binding.filterName.text = filter
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class FilterSelectionListener(val clickListener: (filter: String) -> Unit){
    fun onClick(filter: String) = clickListener(filter)
}