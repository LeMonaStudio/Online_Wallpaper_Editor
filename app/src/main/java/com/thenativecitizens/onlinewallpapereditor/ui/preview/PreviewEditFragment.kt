package com.thenativecitizens.onlinewallpapereditor.ui.preview


import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentPreviewEditBinding
import com.thenativecitizens.onlinewallpapereditor.ui.dialogs.ChooseImageEditorDialog
import java.io.File
import java.io.IOException
import java.util.*


class PreviewEditFragment : Fragment() {

    private lateinit var binding: FragmentPreviewEditBinding
    private lateinit var bitmap: Bitmap
    private lateinit var previewViewModel: PreviewViewModel
    private val chooseImageEditorKey = "CHOOSE_IMAGE_EDITOR"
    //Permission request launcher
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //Fragment Argument
    private lateinit var imageUrl: String
    private var isDeviceImageSource = false
    //Gets result from activity to know if image was edited outside the app or not
    private lateinit var requestImageEditedResult: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    //The Active Permission been requested from the user
    private var activePermissionRequested = ""

    //If the User is performing an editing operation
    private var isPerformExternalEditing = false
    private var isPerformInAppEditing = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_edit, container, false)

        //ViewModel
        previewViewModel = ViewModelProvider(this)[PreviewViewModel::class.java]

        val args: PreviewEditFragmentArgs by navArgs()
        imageUrl = args.imageUrl
        isDeviceImageSource = args.isDeviceImageSource

        binding.imageClickedUrl = args.imageUrl
        binding.isDeviceImage = args.isDeviceImageSource

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                Snackbar.make(binding.root, "$activePermissionRequested permission granted.", Snackbar.LENGTH_SHORT).show()
                //if User was trying to edit an Image continue
                if (isPerformExternalEditing){
                    isPerformExternalEditing = false
                    beginExternalEdit()
                }
                if (isPerformInAppEditing){
                    isPerformInAppEditing = false
                    beginInAppEditing()
                }
            } else{
                Snackbar.make(binding.root, "$activePermissionRequested permission denied", Snackbar.LENGTH_SHORT).show()
            }
        }

        requestImageEditedResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            //content resolver
            val resolver = requireContext().contentResolver
            when(result.resultCode){
                RESULT_OK -> {
                    resolver.delete(imageUri!!, null, null)
                    Snackbar.make(binding.root, "A copy of this image has been saved on your device",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK"){
                            findNavController().popBackStack()
                        }
                        .show()
                }
                RESULT_CANCELED -> {
                    resolver.delete(imageUri!!, null, null)
                }
            }
        }


        /**
         * OnClicks
         */
        //When the Edit button is clicked, call the PhotoEditorView
        binding.editBtn.setOnClickListener {
            val dialog = ChooseImageEditorDialog()
            dialog.show(parentFragmentManager, "CHOOSE_IMAGE_EDITOR_DIALOG")
        }
        //When the Wallpaper btn is clicked
        binding.downloadBtn.setOnClickListener {
            //call the function to save the image to the device
            if (saveImage())
            //Notify the user the Image has been saved
            Snackbar.make(binding.root, "Success! Image saved", Snackbar.LENGTH_LONG).show()
        }


        /**
         * ParentFragmentManager Results
         */
        parentFragmentManager.setFragmentResultListener(
            chooseImageEditorKey,
            viewLifecycleOwner
        ) { requestKey, result ->
            if (requestKey == chooseImageEditorKey) {
                when (result.getInt("Edit With")) {
                    1 -> {
                        //begin in-app editing
                        beginInAppEditing()
                    }
                    2 -> {
                        //begin external editing
                        beginExternalEdit()
                    }
                }
            }
        }

        return binding.root
    }

    //Edits the app internally
    private fun beginInAppEditing() {
        isPerformInAppEditing = true
        //Continue with this app to edit
        findNavController().navigate(
            PreviewEditFragmentDirections
                .actionPreviewEditFragmentToEditFragment(
                    imageUrl,
                    isDeviceImageSource
                )
        )
    }

    //Edits an Image using other app
    private fun beginExternalEdit() {
        //Save the Image File for Edit
        if (saveImage()) {
            //let the UI know the user is trying to edit an image
            isPerformExternalEditing = true
            //Launch Intent to choose other image editor
            //on user's device
            //Send the Image to the device's image editor
            val editIntent = Intent(Intent.ACTION_EDIT)
            editIntent.apply {
                setDataAndType(imageUri, "image/*")
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            requestImageEditedResult.launch(editIntent)
        }
    }


    //Creates Image File
    private fun createImageFile(){
        //create the image file
        val paint = Paint()
        paint.color = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        val view = binding.clickedImage
        bitmap = Bitmap.createBitmap(view.width , view.height, Bitmap.Config.ARGB_8888)

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        //Draw
        val canvas = Canvas(bitmap)
        canvas.drawRect(rect, paint)
        view.draw(canvas)
    }


    //Saves the image to the device
    private fun saveImage(): Boolean {
        var isSaved = false

        //Check if permissions are required
        val mediaPermissionsRequired = previewViewModel.checkMediaPermissionsGranted()
        if(mediaPermissionsRequired.isEmpty()){
            //No permission is required
            //Create the Image File
            createImageFile()

            /**prepare the MediaStore API **/
            //content resolver
            val resolver = requireContext().contentResolver
            //prepare the details of the new image to be saved
            val imageDetails = ContentValues()
            val contentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                imageDetails.apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Editor/${UUID.randomUUID()}")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+"OnlineWallpaper&Editor")
                }
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                imageDetails.apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Editor/${UUID.randomUUID()}")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                }
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            //the new image's uri
            imageUri = resolver.insert(contentUri, imageDetails)

            try {
                imageUri?.apply {
                    //write or make the new image and put it in the Uri
                    val stream =  resolver.openOutputStream(this)
                    //compress the bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    //flush the stream
                    stream?.flush()
                    //close the stream
                    stream?.close()
                }
                isSaved = true
            } catch (exception: IOException){
                Snackbar.make(binding.root, "Unable to save Image to device", Snackbar.LENGTH_SHORT).show()
                isSaved = false
            }
        } else
            requestPermission(mediaPermissionsRequired)

        return isSaved
    }


    //called to request the necessary permissions from the user
    private fun requestPermission(permissionsList: List<String>) {
        for (permission in permissionsList){
            activePermissionRequested = getPermissionNameText(permission)
            if(shouldShowRequestPermissionRationale(permission)){
                Snackbar.make(binding.root, getString(R.string.media_access_permission, activePermissionRequested), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.permission_continue_snackbar_action_text){
                            //request the permission
                            requestPermissionLauncher.launch(permission)
                        }.show()
            } else{
                //request the permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }


    //Returns a text that can be easily understood by users as the required permission
    private fun getPermissionNameText(permission: String): String{
        return when(permission){
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Media Read Access"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Media Write Access"
            else -> ""
        }
    }
}








