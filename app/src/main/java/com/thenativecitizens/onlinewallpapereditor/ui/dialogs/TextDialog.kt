package com.thenativecitizens.onlinewallpapereditor.ui.dialogs


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogEditTextBinding



class TextDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogEditTextBinding

    private var enteredText = "Add text to display"
    private var selectedColorCode = 0

    private val textEditKey = "TEXT"
    private val textPrepareEdit = "PREPARE TEXT"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.dialog_edit_text, null, false)

        val bundle = Bundle()

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
                "teal" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.teal)
                "purple" -> selectedColorCode = ContextCompat.getColor(requireContext(), R.color.purple)
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

        //When the user is done adding text and clicks the done button
        binding.doneBtn.setOnClickListener {
            bundle.putString("text", enteredText)
            bundle.putInt("textColor", selectedColorCode)
            parentFragmentManager.setFragmentResult(
                textEditKey, bundle
            )
            dismiss()
        }

        return binding.root
    }
}