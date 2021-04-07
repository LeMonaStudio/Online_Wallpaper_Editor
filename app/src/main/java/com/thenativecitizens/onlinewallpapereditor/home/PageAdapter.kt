package com.thenativecitizens.onlinewallpapereditor.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thenativecitizens.onlinewallpapereditor.load.LoadFragment
import com.thenativecitizens.onlinewallpapereditor.load.WallpaperFragment

class PageAdapter (fragment: Fragment): FragmentStateAdapter(fragment){

    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) LoadFragment() else WallpaperFragment()
    }
}