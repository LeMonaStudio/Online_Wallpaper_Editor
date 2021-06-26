package com.thenativecitizens.onlinewallpapereditor.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.DialogFilterBinding

class FilterDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFilterBinding
    private val filterClickedKey = "FILTER CLICKED"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, container, false)

        //Get the String-Array for filters
        val filterTypes = resources.getStringArray(R.array.filter_list)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val adapter = FilterListAdapter(FilterSelectionListener{filter ->
            val bundle = Bundle()
            bundle.putString("FilterName", filter)
            //Dismiss the Dialog
            dismiss()
            parentFragmentManager.setFragmentResult(filterClickedKey, bundle)
        })

        adapter.data = filterTypes.asList()
        binding.filterList.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }


        return binding.root
    }

}