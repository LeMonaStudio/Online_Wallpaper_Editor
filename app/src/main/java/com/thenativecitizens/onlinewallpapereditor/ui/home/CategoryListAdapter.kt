package com.thenativecitizens.onlinewallpapereditor.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListCategoryViewBinding
import com.thenativecitizens.onlinewallpapereditor.util.Category



class CategoryListAdapter(private val listener: CategoryListListener): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>(){

    var data = listOf<Category>()
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
        val ctx = holder.itemView.context
        val category = data[position]
        holder.bind(category, listener, ctx)
    }


    class ViewHolder private constructor(private val binding: ListCategoryViewBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListCategoryViewBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(category: Category, listener: CategoryListListener, ctx: Context) {
            binding.category = category
            binding.categoryName.text = category.categoryName
            binding.isDeviceImage = false
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class CategoryListListener(val clickListener: (category: Category) -> Unit){
    fun onClick(category: Category) = clickListener(category)
}