package com.thenativecitizens.onlinewallpapereditor.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.load.*
import com.thenativecitizens.onlinewallpapereditor.databinding.HomeFragmentBinding

class HomeFragment : Fragment(), LoadFragment.CreateFragmentListener {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var adapter: PageAdapter
    private lateinit var viewModel: FetchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        //ViewModel
        val application = requireNotNull(this.activity).application
        val viewModelFactory = FetchViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FetchViewModel::class.java)


        adapter = PageAdapter(this)
        binding.viewPager.adapter = adapter


        //Override onBackPressed for the back button
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner){
            requireActivity().finish()
        }

        //set up the ViewPager2 with TabLayout
        TabLayoutMediator(binding.tabs, binding.viewPager){tab, position ->
            when(position){
                0 -> tab.text = "Create"
                1 -> tab.text = "Wallpaper"
            }
        }.attach()


        return  binding.root
    }



    override fun onImageClickedForPreview(image: Image, isDeviceImageSource: Boolean) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPreviewEditFragment(isDeviceImageSource, image))
    }

    override fun onWallpaperBtnClicked() {
        binding.viewPager.currentItem = 1
    }

}