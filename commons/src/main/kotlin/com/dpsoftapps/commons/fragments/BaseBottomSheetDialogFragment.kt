package com.dpsoftapps.commons.fragments

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogBottomSheetBinding
import com.dpsoftapps.commons.extensions.*

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogBottomSheetBinding.inflate(inflater, container, false)
        val view = binding.root
        val context = requireContext()
        val config = context.baseConfig

        if (requireContext().isBlackAndWhiteTheme()) {
            view.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bottom_sheet_bg_black, context.theme)
        } else if (!config.isUsingSystemTheme) {
            view.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bottom_sheet_bg, context.theme).apply {
                (this as LayerDrawable).findDrawableByLayerId(R.id.bottom_sheet_background).applyColorFilter(context.getProperBackgroundColor())
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getInt(BOTTOM_SHEET_TITLE).takeIf { it != 0 }
        val binding = DialogBottomSheetBinding.bind(view)
        binding.bottomSheetTitle.setTextColor(binding.root.context.getProperTextColor())
        binding.bottomSheetTitle.setTextOrBeGone(title)
        setupContentView(binding.bottomSheetContentHolder)
    }

    abstract fun setupContentView(parent: ViewGroup)

    companion object {
        const val BOTTOM_SHEET_TITLE = "title_string"
    }
}
