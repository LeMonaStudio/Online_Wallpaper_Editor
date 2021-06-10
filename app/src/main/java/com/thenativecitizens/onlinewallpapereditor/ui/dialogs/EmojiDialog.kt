package com.thenativecitizens.onlinewallpapereditor.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogEditEmojisBinding
import java.util.ArrayList


class EmojiDialog: DialogFragment() {
    private lateinit var binding: DialogEditEmojisBinding
    private val emojiUnicodeKey = "EMOJI UNICODE"
    private val emojiClickedKey = "EMOJI CLICKED"

    private lateinit var arrayOfEmojiUnicode: List<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_edit_emojis, null, false)

        //AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        //The Dialog
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //A GridLayoutManager for the ImageList RecyclerView
        val layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        //the EmojiListAdapter
        val adapter = EmojiListAdapter(EmojiSelectionListener{ emojiUnicode ->
            val bundle = Bundle()
            bundle.putString("emoji", emojiUnicode)
            parentFragmentManager.setFragmentResult(
                emojiClickedKey, bundle
            )
            dialog.dismiss()
        })

        binding.emojiList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        //Get the ArrayList of EmojiUnicodes
        parentFragmentManager.setFragmentResultListener(
            emojiUnicodeKey, this,
            {requestKey, result ->
                if(requestKey == emojiUnicodeKey){
                    val arrayList: ArrayList<String>? = result.getStringArrayList("unicode")
                    arrayOfEmojiUnicode = arrayList!!.toList()
                    adapter.data = arrayOfEmojiUnicode
                }
            }
        )



        return dialog
    }
}