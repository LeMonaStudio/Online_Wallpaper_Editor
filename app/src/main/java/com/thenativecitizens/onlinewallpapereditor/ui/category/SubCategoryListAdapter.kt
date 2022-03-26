package com.thenativecitizens.onlinewallpapereditor.ui.category


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListSubCategoryViewBinding
import com.thenativecitizens.onlinewallpapereditor.model.SubCategory

class SubCategoryListAdapter(private val listener: SubCategoryListListener): RecyclerView.Adapter<SubCategoryListAdapter.ViewHolder>(){

    var data = listOf<SubCategory>()
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
        val subcategory = data[position]
        holder.bind(subcategory, listener)
    }


    class ViewHolder private constructor(private val binding: ListSubCategoryViewBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListSubCategoryViewBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(subCategory: SubCategory, listener: SubCategoryListListener) {
            binding.subCategory = subCategory
            binding.subCategoryName.text = subCategory.subCategoryName
            binding.subCategoryImagePlaceholderUrl = subCategory.subCategoryPlaceholderImageUrl
            binding.isDeviceImage = false
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class SubCategoryListListener(val clickListener: (subCategory: SubCategory) -> Unit){
    fun onClick(subCategory: SubCategory) = clickListener(subCategory)
}