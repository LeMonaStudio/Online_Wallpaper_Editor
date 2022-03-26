package com.thenativecitizens.onlinewallpapereditor.ui.dialogs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogEditBrushBinding



class BrushDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogEditBrushBinding
    private var selectedBrushSize = 0
    private var selectedColorCode: Int = 0
    private var selectedColorOpacity = 0

    private var brushEditKey = "BRUSH"
    private var brushPrepareEditKey = "PREPARE BRUSH"

    private val bundle = Bundle()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_edit_brush, null, false)


        //All colors user can draw with
        val black = ContextCompat.getColor(requireContext(), R.color.black)
        val purple = ContextCompat.getColor(requireContext(), R.color.purple)
        val teal = ContextCompat.getColor(requireContext(), R.color.teal)

        //Array of color names
        val arrayOfColorNames = resources.getStringArray(R.array.color_list)

        parentFragmentManager.setFragmentResultListener(
            brushPrepareEditKey, this
        ) { requestKey, result ->
            if (requestKey == brushPrepareEditKey) {
                selectedBrushSize = result.getInt("Brush", binding.brushSizeSeek.progress)
                binding.brushSizeSeek.progress = selectedBrushSize
                selectedColorOpacity = result.getInt("Opacity", binding.opacitySeek.progress)
                binding.opacitySeek.progress = selectedColorOpacity
                selectedColorCode = result.getInt("ColorCode", teal)
            }
        }

        //Defaults
        selectedBrushSize = binding.brushSizeSeek.progress
        selectedColorOpacity = binding.opacitySeek.progress

        binding.brushSizeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedBrushSize = progress
                binding.brushSizeSeekText.text = progress.toString()
                bundle.putInt("BrushSize", selectedBrushSize)
                parentFragmentManager.setFragmentResult(
                    brushEditKey, bundle
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.opacitySeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedColorOpacity = progress
                binding.opacitySeekText.text = progress.toString()
                bundle.putInt("Opacity", selectedColorOpacity)
                parentFragmentManager.setFragmentResult(
                    brushEditKey, bundle
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val adapter = ColorAdapter(ColorSelectionListener{
            when(it){
                "black" -> selectedColorCode = black
                "teal" -> selectedColorCode = teal
                "purple" -> selectedColorCode = purple
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
            bundle.putInt("BrushSize", selectedBrushSize)
            bundle.putInt("Opacity", selectedColorOpacity)
            bundle.putInt("BrushColorCode", selectedColorCode)
            parentFragmentManager.setFragmentResult(
                brushEditKey, bundle
            )
            dismiss()
        })
        binding.colorList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
            adapter.data = arrayOfColorNames.asList()
        }

        return binding.root
    }
}