package com.thenativecitizens.onlinewallpapereditor.ui.subcategory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentSubCategoryBinding


class SubCategoryFragment : Fragment() {

    private lateinit var binding: FragmentSubCategoryBinding
    private lateinit var subCategoryViewModel: SubCategoryViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sub_category, container, false)

        //Get the Arguments Bundle
        val args = SubCategoryFragmentArgs.fromBundle(requireArguments())
        binding.subCategoryName.text = args.subCategoryName


        //CategoryViewModel
        subCategoryViewModel = ViewModelProvider(this)[SubCategoryViewModel::class.java]

        //Call to fetch wallpapers for this SubCategory
        subCategoryViewModel.fetchWallpapers(args.subCategoryName, args.categoryName)

        //Wallpaper RecyclerView
        val layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        val adapter = WallpaperListAdapter(WallpaperListListener { wallpaperUrl ->
            findNavController().navigate(SubCategoryFragmentDirections
                .actionSubCategoryFragmentToPreviewEditFragment(wallpaperUrl, false))
        })
        binding.wallpaperList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }


        /**
         * ViewModel Observations
         */
        //Observe for the list of wallpaperUrls
        subCategoryViewModel.listOfWallpapers.observe(viewLifecycleOwner){
            it?.let {
                adapter.data = it.reversed()
                binding.executePendingBindings()
                //call to begin the two seconds loading to
                //allow Glide load images
                subCategoryViewModel.beginTwoSecsLoading()
            }
        }

        //Observe to know when the three seconds loading is done and
        //then hide the loading container
        subCategoryViewModel.isTwoSecsLoadingDone.observe(viewLifecycleOwner){
            if (it)
            //hide the loading progress
                binding.progressContainer.visibility = View.GONE
        }


        /**
         * OnClickListeners
         */
        //the Custom back button
        binding.customBackButton.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }
}