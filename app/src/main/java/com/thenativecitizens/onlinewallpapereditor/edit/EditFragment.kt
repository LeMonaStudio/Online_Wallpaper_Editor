package com.thenativecitizens.onlinewallpapereditor.edit

import android.Manifest
import android.content.ContentValues
import android.graphics.*
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.EditFragmentBinding
import com.thenativecitizens.onlinewallpapereditor.editdialogs.BrushDialog
import com.thenativecitizens.onlinewallpapereditor.editdialogs.EmojiDialog
import com.thenativecitizens.onlinewallpapereditor.editdialogs.TextDialog
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import java.util.*
import kotlin.collections.ArrayList

class EditFragment : Fragment() {

    private lateinit var binding: EditFragmentBinding
    private lateinit var viewModel: EditViewModel

    //PermissionLauncher
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //PhotoEditorLibrary
    private lateinit var photoEditor: PhotoEditor

    private var brushEditKey = "BRUSH"
    private var brushPrepareEditKey = "PREPARE BRUSH"
    private val textEditKey = "TEXT"
    private val textPrepareEdit = "PREPARE TEXT"

    private val emojiUnicodeKey = "EMOJI UNICODE"
    private val emojiClickedKey = "EMOJI CLICKED"

    private var selectedBrushSize = 16
    private var selectedColorCode: Int = 0
    private var selectedColorOpacity = 100
    private var userTextInput = "Long press to edit"
    private lateinit var photoEditorRootView: View

    /*override fun onStart() {
        super.onStart()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }*/



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val editViewModelFactory = EditViewModelFactory(application)
        viewModel = ViewModelProvider(this, editViewModelFactory).get(EditViewModel::class.java)

        //Argument
        val args: EditFragmentArgs by navArgs()

        args.image?.let {
            //Pass the Image
            binding.imageClicked = args.image
        }


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                val text = getString(R.string.permission_granted)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                saveImage()
            } else{
                val text = getString(R.string.permission_not_granted)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        selectedColorCode = ContextCompat.getColor(requireContext(), R.color.teal_200)
        //Initialize the PhotoEditor
        initializePhotoEditor()


        //Get the String-Array that would be sent as RecyclerView data
        val editActionTypes = resources.getStringArray(R.array.edit_action_types)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val adapter = EditActionListAdapter(EditActionListener { editType ->
            binding.currentEditingAction.text = editType
            when(editType){
                //Decide what to do in the PhotoEditor
                "Brush" -> {
                    with(photoEditor){
                        setBrushDrawingMode(true) //Activates the Brush for draw
                        brushSize = selectedBrushSize.toFloat()
                        setOpacity(selectedColorOpacity)
                        brushColor = selectedColorCode
                    }
                    showBrushDialog()
                }
                "Eraser" -> {
                    //Set brush eraser
                    if(photoEditor.brushDrawableMode == true)
                        photoEditor.brushEraser()
                }
                "Text" -> {
                    //Call the TextDialog
                    photoEditor.addText(userTextInput, selectedColorCode)
                    //Add textListener to the photoEditor
                    photoEditor.addListener()
                }
                "Emoji" -> {
                    //Load the emoji unicode
                    val unicode: ArrayList<String> = PhotoEditor.getEmojis(requireContext())
                    //Call the EmojiDialog
                    showEmojiDialog(unicode)
                }
            }
        })

        //Set LayoutManager and Adapter for the Edit Action Types recyclerview
        binding.editActionList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
            adapter.data = editActionTypes.asList()
        }


        //When the user decides to cancel the editing
        binding.cancelEditBtn.setOnClickListener {
            findNavController().navigate(EditFragmentDirections.actionEditFragmentToHomeFragment())
        }

        //When the undo or redo button is clicked
        binding.apply {
            redoBtn.setOnClickListener {
                //Redo PhotoEditor's last undo
                photoEditor.redo()
            }
            undoBtn.setOnClickListener {
                //Undo the PhotoEditor last edit
                photoEditor.undo()
            }
        }

        //When the User wants to save the edited image
        binding.downloadImageBtn.setOnClickListener {
            if (viewModel.checkWritePermission())
                saveImage()
            else //Ask for the necessary permission
                requestPermission()
        }



        //Listener for user's brush settings such as brush thickness, color and opacity
        //to be used to draw on the Image
        parentFragmentManager.setFragmentResultListener(
            brushEditKey, this,
            {resultKey, result ->
                if(resultKey == brushEditKey){
                    selectedBrushSize = result.getInt("BrushSize", selectedBrushSize)
                    selectedColorOpacity = result.getInt("Opacity", selectedColorOpacity)
                    selectedColorCode = result.getInt("BrushColorCode", selectedColorCode)
                    with(photoEditor){
                        setBrushDrawingMode(true) //Activates the Brush for draw
                        brushSize = selectedBrushSize.toFloat()
                        setOpacity(selectedColorOpacity)
                        brushColor = selectedColorCode
                    }
                }
            }
        )

        //Listening for the result user's text input to be added to the image
        parentFragmentManager.setFragmentResultListener(
            textEditKey, this,
            {requestKey, result ->
                if(requestKey == textEditKey){
                    userTextInput = result.getString("text", userTextInput)
                    selectedColorCode = result.getInt("textColor", selectedColorCode)
                    photoEditor.editText(photoEditorRootView , userTextInput, selectedColorCode)
                }
            }
        )

        //Listening for user's selected emoji to be displayed and added to the Image
        parentFragmentManager.setFragmentResultListener(
            emojiClickedKey, this,
            {requestKey, result ->
                if(requestKey == emojiClickedKey){
                    val emojiSelectedUnicode = result.getString("emoji")
                    photoEditor.addEmoji(emojiSelectedUnicode)
                }
            }
        )


        return binding.root
    }

    private fun saveImage() {
        Snackbar.make(binding.root, "Saving image...", Snackbar.LENGTH_SHORT).show()

        //create the image file
        val paint = Paint()
        paint.color = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        val view = binding.photoEditorView
        val bitmap = Bitmap.createBitmap(view.width , view.height, Bitmap.Config.ARGB_8888)

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val canvas = Canvas(bitmap)
        canvas.drawRect(rect, paint)
        view.draw(canvas)

        /**prepare the MediaStore API **/
        //content resolver
        val resolver = requireContext().contentResolver
        //get the collection of images on the device internal memory
        val imageCollections =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else{
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        //prepare the details of the new image to be saved
        val imageDetails = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Editor/${UUID.randomUUID()}")
        }
        //the new image's uri
        val imageUri = resolver.insert(imageCollections, imageDetails)

        imageUri?.apply {
            //write or make the new image and put it in the Uri
            val stream =  resolver.openOutputStream(this)
            //compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            //flush the stream
            stream?.flush()
            //close the stream
            stream?.close()

            //Notify the user
            Snackbar.make(binding.root, "Success! You saved $this", Snackbar.LENGTH_LONG).show()
        }
    }


    private fun showBrushDialog() {
        val bundle = Bundle()
        bundle.putInt("Brush", selectedBrushSize)
        bundle.putInt("Opacity", selectedColorOpacity)
        bundle.putInt("ColorCode", selectedColorCode)
        parentFragmentManager.setFragmentResult(
            brushPrepareEditKey, bundle
        )
        val dialog = BrushDialog()
        dialog.show(parentFragmentManager, "Brush Dialog")
    }

    private fun showTextDialog(){
        val bundle = Bundle()
        bundle.putString("text", userTextInput)
        bundle.putInt("textColor", selectedColorCode)
        parentFragmentManager.setFragmentResult(
            textPrepareEdit, bundle
        )
        val dialog = TextDialog()
        dialog.show(parentFragmentManager, "Text Dialog")
    }

    private fun showEmojiDialog(unicode: ArrayList<String>) {
        val bundle = Bundle()
        bundle.putStringArrayList("unicode", unicode)
        parentFragmentManager.setFragmentResult(
            emojiUnicodeKey, bundle
        )
        val dialog = EmojiDialog()
        dialog.show(parentFragmentManager, "Emoji")
    }


    //Called to initialize the photo editor library
    private fun initializePhotoEditor() {
        photoEditor = PhotoEditor.Builder(requireContext(), binding.photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto))
            .setDefaultEmojiTypeface(ResourcesCompat.getFont(requireContext(), R.font.baumans))
            .build()
    }

    //Adds a listener to the PhotoEditor
    private fun PhotoEditor.addListener(){
        this.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
                photoEditorRootView = rootView!!
                showTextDialog()
            }
            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: ViewType?) {}
            override fun onStopViewChangeListener(viewType: ViewType?) {}
        })
    }

    //called to request the necessary permissions from the user
    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //means the current device runs on API Level 23 or greater
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Snackbar.make(binding.root, R.string.media_access_permission, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permission_continue_snackbar_action_text){
                        //request the permission
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }.show()
            } else{
                //request the permission
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            //request the permission
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}