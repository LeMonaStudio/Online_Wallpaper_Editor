package com.thenativecitizens.onlinewallpapereditor.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogChooseImageEditorBinding


class ChooseImageEditorDialog : DialogFragment() {

    private lateinit var binding: DialogChooseImageEditorBinding
    private val chooseImageEditorKey = "CHOOSE_IMAGE_EDITOR"


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_choose_image_editor, null, false)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val bundle = Bundle()

        //OnClicks
        binding.continueWithAppBtn.setOnClickListener {
            //Tell the PreviewFragment to continue with this app
            bundle.putInt("Edit With", 1)
            parentFragmentManager.setFragmentResult(chooseImageEditorKey, bundle)
            //close the dialog
            dialog.dismiss()
        }

        binding.chooseImageEditorBtn.setOnClickListener {
            //Tell the PreviewFragment to continue with this app
            bundle.putInt("Edit With", 2)
            parentFragmentManager.setFragmentResult(chooseImageEditorKey, bundle)
            //close the dialog
            dialog.dismiss()
        }


        return dialog
    }
}