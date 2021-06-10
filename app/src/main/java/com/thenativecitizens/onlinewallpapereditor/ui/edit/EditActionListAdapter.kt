package com.thenativecitizens.onlinewallpapereditor.ui.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.EditBtnViewBinding

class EditActionListAdapter(private val editActionListener: EditActionListener) : RecyclerView.Adapter<EditActionListAdapter.ViewHolder>(){

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
        val editType = data[position]
        holder.bind(editType, editActionListener)
    }


    class ViewHolder private constructor(private val binding: EditBtnViewBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = EditBtnViewBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(editActionType: String, editActionListener: EditActionListener) {
            binding.editType = editActionType
            binding.editActionListener = editActionListener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class EditActionListener(val clickListener: (editType: String) -> Unit){
    fun onClick(editType: String) = clickListener(editType)
}