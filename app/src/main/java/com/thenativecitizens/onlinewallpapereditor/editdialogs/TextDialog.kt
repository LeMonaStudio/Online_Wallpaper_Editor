package com.thenativecitizens.onlinewallpapereditor.editdialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogEditTextBinding

class TextDialog: DialogFragment() {

    private lateinit var binding: DialogEditTextBinding

    private var enteredText = "Add text to display"
    private var selectedColorCode = 0

    private val textEditKey = "TEXT"
    private val textPrepareEdit = "PREPARE TEXT"


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.dialog_edit_text, null, false)

        val bundle = Bundle()

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        parentFragmentManager.setFragmentResultListener(
            textPrepareEdit, this,
            {requestKey, result ->
                if(requestKey == textPrepareEdit){
                    enteredText = result.getString("text", enteredText)
                    binding.userText.setText(enteredText, TextView.BufferType.EDITABLE)
                    selectedColorCode = result.getInt("textColor")
                    binding.userText.setTextColor(selectedColorCode)
                }
            }
        )

        //Array of color names
        val arrayOfColorNames = resources.getStringArray(R.array.color_list)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val adapter = ColorAdapter(ColorSelectionListener{
            when(it){
                "black" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.black)
                "teal" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.teal_200)
                "purple" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.purple_500)
                "crimson" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.crimson)
                "blue" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.blue)
                "dark blue" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.darK_blue)
                "chartreuse" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.chartreuse)
                "brown" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.brown)
                "dark olive green" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.dark_olive_green)
                "dark green" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.dark_green)
                "green" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.green)
                "dark slate blue" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.dark_slate_blue)
                "dark sea green" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.dark_sea_green)
                "white" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.white)
                "yellow" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.yellow)
                "orange" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.orange)
                "indigo" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.indigo)
                "magenta" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.magenta)
                "violet" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.violet)
                "sky blue" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.sky_blue)
                "red" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.red)
                "papaya whip" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.papaya_whip)
            }
            binding.userText.setTextColor(selectedColorCode)
        })


        //Set the recyclerView details
        binding.colorList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
            adapter.data = arrayOfColorNames.asList()
        }



        //When the user entered text changes
        binding.userText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                enteredText = s.toString()
            }

        })

        //Whne the user is done adding text and clicks the done button
        binding.doneBtn.setOnClickListener {
            bundle.putString("text", enteredText)
            bundle.putInt("textColor", selectedColorCode)
            parentFragmentManager.setFragmentResult(
                textEditKey, bundle
            )
            dialog.dismiss()
        }




        return dialog
    }
}