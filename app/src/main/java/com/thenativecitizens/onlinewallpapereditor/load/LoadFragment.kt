package com.thenativecitizens.onlinewallpapereditor.load

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentLoadBinding

class LoadFragment : Fragment() {

    private lateinit var binding: FragmentLoadBinding
    private lateinit var viewModel: FetchViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    interface CreateFragmentListener{
        fun onImageClickedForPreview(image: Image, isDeviceImageSource: Boolean)
        fun onWallpaperBtnClicked()
    }

    //instance of the listener
    private lateinit var createFragmentListener: CreateFragmentListener



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_load, container, false)

        //ViewModel
        val application = requireNotNull(this.activity).application
        val viewModelFactory = FetchViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FetchViewModel::class.java)

        //toggle ImageList's visibility immediately this view is created
        toggleImageList(false)


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                //enable the upload button
                binding.uploadBtn.isEnabled = true
                viewModel.fetchDeviceImages()
            } else{
                //enable the upload button
                binding.uploadBtn.isEnabled = true
                val text = getString(R.string.permission_not_granted)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }


        //A GridLayoutManager for the ImageList RecyclerView
        val layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        //the ImageViewList Adapter
        val adapter = ImageListAdapter(ImageListListener {clickedImage ->
            createFragmentListener = parentFragment as CreateFragmentListener
            createFragmentListener.onImageClickedForPreview(clickedImage, true)
        })

        binding.imageList.layoutManager = layoutManager
        binding.imageList.adapter = adapter

        //Observe for when the device's images has been fetched by user clicking on the upload btn
        viewModel.deviceImageList.observe(viewLifecycleOwner){deviceImages ->
            deviceImages?.let {
                if(it.size >= 1) adapter.data = it
                toggleImageList((it.size >= 1))
            }
        }


        //When the Upload button is clicked
        binding.uploadBtn.setOnClickListener {
            //disable the button
            it.isEnabled = false
            //Check if READ_ACCESS_PERMISSION is granted
            if(viewModel.checkMediaAccessPermissionsGranted()){
                //Fetch all the images in the device's gallery
                //by calling appropriate method from the ViewModel
                viewModel.fetchDeviceImages()
                //enable the upload button
                binding.uploadBtn.isEnabled = true
            }
            else {
                requestPermission()
            }
        }

        //Returns the view to default by emptying the list
        binding.returnBtn.setOnClickListener {
            //Empty the loaded list of device Images
            viewModel.emptyLoadedDeviceImageList()
        }

        //When the wallpaper button is clicked
        binding.wallpapersBtn.setOnClickListener {
            //Jump to the wallpaper fragment
            createFragmentListener = parentFragment as CreateFragmentListener
            createFragmentListener.onWallpaperBtnClicked()
        }



        return binding.root
    }

    //Toggle the ImageList RecyclerView's visibility
    private fun toggleImageList(showImageList: Boolean) {
        when(showImageList){
            true -> {
                binding.imageListContainer.visibility = View.VISIBLE
                binding.returnBtn.visibility = View.VISIBLE
                binding.actionBtnContainer.visibility = View.GONE
            }
            false -> {
                binding.imageListContainer.visibility = View.GONE
                binding.returnBtn.visibility = View.GONE
                binding.actionBtnContainer.visibility = View.VISIBLE
            }
        }
    }


    //called to request the necessary permissions from the user
    private fun requestPermission() {
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            //enable the upload button
            binding.uploadBtn.isEnabled = true
            Snackbar.make(binding.root, R.string.media_access_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.permission_continue_snackbar_action_text){
                    //request the permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
        } else{
            //request the permission
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

}