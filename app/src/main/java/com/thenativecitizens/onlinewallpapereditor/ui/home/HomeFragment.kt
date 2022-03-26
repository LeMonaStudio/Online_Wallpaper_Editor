package com.thenativecitizens.onlinewallpapereditor.ui.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.HomeFragmentBinding


class HomeFragment : Fragment(){

    private lateinit var binding: HomeFragmentBinding
    private lateinit var homeViewModel: HomeViewModel
    //Result Launcher to pick image(s) from the user's device
    private lateinit var pickImageContract: ActivityResultLauncher<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        //ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        //Override onBackPressed for the back button
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val adapter = CategoryListAdapter(CategoryListListener { category ->
            when{
                category.subCategoryList.size >= 2 -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoriesFragment(category.categoryName))
                }
                category.subCategoryList.size == 1 -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSubCategoryFragment(category.subCategoryList[0], category.categoryName))
                }
                else ->{
                    Snackbar.make(binding.root, "This category is empty", Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        //Register for Activity Result to Pick Images from the device
        pickImageContract = registerForActivityResult(ActivityResultContracts.GetContent()){
            //hide the Progress Container
            binding.progressContainer.visibility = View.GONE
            if (it != null){
                //Send the Uri to the EditFragment
                findNavController().navigate(HomeFragmentDirections
                    .actionHomeFragmentToEditFragment(it.toString(), true))
            }
        }


        binding.categoryList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }


        /**
         * ViewModel Observations
         */
        //Observe for the list of categories fetched from the database
        homeViewModel.wallpaperCategoryList.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                //Pass data to the CategoryListAdapter
                adapter.data = it
                binding.executePendingBindings()
                //call to begin the two seconds loading to
                //allow Glide load images
                homeViewModel.beginTwoSecsLoading()
            }
        }

        //Observe to know when the three seconds loading is done and
        //then hide the loading container
        homeViewModel.isTwoSecsLoadingDone.observe(viewLifecycleOwner){
            if (it){
                binding.progressContainer.visibility = View.GONE
                //Now show the device Images fab
                binding.fab.visibility = View.VISIBLE
            }
        }

        /**
         * OnClicks
         */
        //when the FAB is clicked
        binding.fab.setOnClickListener {
            //allow user pick image from their device
            pickImageContract.launch("image/*")
        }



        return binding.root
    }
}