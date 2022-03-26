package com.thenativecitizens.onlinewallpapereditor.ui.subcategory


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.databinding.ListWallpapersViewBinding
import com.thenativecitizens.onlinewallpapereditor.model.Wallpaper

class WallpaperListAdapter(private val wallpaperListListener: WallpaperListListener?) :
    RecyclerView.Adapter<WallpaperListAdapter.ViewHolder>(){

    var data = listOf<Wallpaper>()
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
        val wallpaper = data[position]
        holder.bind(wallpaper, wallpaperListListener)
    }


    class ViewHolder private constructor(private val binding: ListWallpapersViewBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListWallpapersViewBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(wallpaper: Wallpaper, wallpaperListListener: WallpaperListListener?) {
            binding.wallpaperUrl = wallpaper.wallpaperUrl
            binding.isDeviceImage = false
            binding.clickListener = wallpaperListListener
            binding.executePendingBindings()
        }
    }
}


//OnClickListener for the RecyclerView
class WallpaperListListener(val clickListener: (wallpaperUrl: String) -> Unit){
    fun onClick(wallpaperUrl: String) = clickListener(wallpaperUrl)
}