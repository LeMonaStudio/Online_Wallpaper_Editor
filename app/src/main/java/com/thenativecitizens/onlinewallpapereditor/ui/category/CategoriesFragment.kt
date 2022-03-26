package com.thenativecitizens.onlinewallpapereditor.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentCategoriesBinding


class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesViewModel: CategoriesViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        //Get the Arguments Bundle
        val args = CategoriesFragmentArgs.fromBundle(requireArguments())

        //CategoryViewModel
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]

        //Display the CategoryName
        binding.categoryName.text = args.categoryName

        //fetch the list of subCategories for display
        categoriesViewModel.fetchSubCategoriesFor(args.categoryName)

        //SubCategoryList RecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val adapter = SubCategoryListAdapter(SubCategoryListListener { subCategory ->
            if (subCategory.imageUrlList.isNotEmpty()){
                //Navigate to the SubCategory Fragment to display images for the selected SubCategory
                findNavController().navigate(CategoriesFragmentDirections
                    .actionCategoriesFragmentToSubCategoryFragment(subCategory.subCategoryName, args.categoryName))
            } else {
                Snackbar.make(binding.root, "This subcategory is empty", Snackbar.LENGTH_SHORT).show()
            }
        })
        binding.subCategoryList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }


        /**
         * Observations
         */
        //Observe for the list of SubCategory fetched by CategoriesViewModel
        categoriesViewModel.subCategoryList.observe(viewLifecycleOwner){
            it?.let {
                //pass it to the adapter for display
                adapter.data = it
                binding.executePendingBindings()
                //call to begin the two seconds loading to
                //allow Glide load images
                categoriesViewModel.beginTwoSecsLoading()
            }
        }

        //Observe to know when the three seconds loading is done and
        //then hide the loading container
        categoriesViewModel.isTwoSecsLoadingDone.observe(viewLifecycleOwner){
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