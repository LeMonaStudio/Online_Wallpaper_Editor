package com.thenativecitizens.onlinewallpapereditor.preview


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentPreviewEditBinding

class PreviewEditFragment : Fragment() {

    private lateinit var binding: FragmentPreviewEditBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_edit, container, false)

        val args: PreviewEditFragmentArgs by navArgs()

        //If permission was declined at any point
        args.clickedImage?.let {
            binding.imageClicked = it
            binding.appBarTitle.text = it.imageName
            //If the image was fetched from the device for preview
            //disable the Download btn as the Image is saved on device already
            if(args.isDeviceImageSource) binding.downloadBtn.isEnabled = false
        }

        //When the Edit button is clicked, call the PhotoEditorView
        binding.editBtn.setOnClickListener {
            findNavController().navigate(PreviewEditFragmentDirections.actionPreviewEditFragmentToEditFragment(args.clickedImage))
        }

        //When the Wallpaper btn is clicked
        binding.downloadBtn.setOnClickListener {
            //Check for Permission
        }


        return binding.root
    }
}



//This function sets the clicked image as device wallpaper
/*private fun setAsWallpaper(){
    val wallpaperManager = WallpaperManager.getInstance(requireContext())
    try {
        //val resolver: ContentResolver = requireContext().contentResolver
        //val inputStream = resolver.openInputStream(currentUri)
        val drawable: BitmapDrawable = binding.clickedImage.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        wallpaperManager.setBitmap(bitmap).let {
            Snackbar.make(binding.root, getString(R.string.success_prompt), Snackbar.LENGTH_SHORT).show()
        }
     } catch (ioe:Exception){
         Snackbar.make(binding.root, R.string.unable_to_set_wallpaper, Snackbar.LENGTH_LONG).show()
     }
}*/

//To request for permission to save an image as device's wallpaper
/*requestWallpaperPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
    if(isGranted){
        setAsWallpaper()
    } else{
        val text = getString(R.string.wallpaper_permission_denied)
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}*/