package com.example.recommendationapp.presentation.map.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.BottomSheetMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetMapBinding
    lateinit var behavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    companion object {
        const val TAG = "MapBottomSheet"
        private const val TAG_ADD = "${MapFragment.TAG} ADD"
        private const val TAG_ERROR = "${MapFragment.TAG} ERROR"
        private const val TAG_PROGRESS = "${MapFragment.TAG} PROGRESS"

        fun newInstance(): MapBottomSheet {
            return MapBottomSheet()
        }
    }
}