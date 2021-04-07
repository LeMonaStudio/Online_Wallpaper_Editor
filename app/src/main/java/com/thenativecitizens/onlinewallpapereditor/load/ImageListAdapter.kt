package com.thenativecitizens.onlinewallpapereditor.load


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListFetchedImagesBinding

class ImageListAdapter(private val imageListListener: ImageListListener?) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>(){

    var data = listOf<Image>()
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
        val image = data[position]
        holder.bind(image, imageListListener)
    }


    class ViewHolder private constructor(private val binding: ListFetchedImagesBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListFetchedImagesBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(image: Image, imageListListener: ImageListListener?) {
            binding.fetchedImage = image
            if(imageListListener != null) {
                binding.clickListener = imageListListener
            }
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class ImageListListener(val clickListener: (image: Image) -> Unit){
    fun onClick(image: Image) = clickListener(image)
}