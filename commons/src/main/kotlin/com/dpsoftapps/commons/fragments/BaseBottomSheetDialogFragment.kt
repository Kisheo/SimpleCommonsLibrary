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

    private var _binding: DialogBottomSheetBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogBottomSheetBinding.inflate(inflater, container, false)
        val context = requireContext()
        val config = context.baseConfig

        if (requireContext().isBlackAndWhiteTheme()) {
            binding.root.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bottom_sheet_bg_black, context.theme)
        } else if (!config.isUsingSystemTheme) {
            binding.root.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bottom_sheet_bg, context.theme).apply {
                (this as LayerDrawable).findDrawableByLayerId(R.id.bottom_sheet_background).applyColorFilter(context.getProperBackgroundColor())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getInt(BOTTOM_SHEET_TITLE).takeIf { it != 0 }
        val ctx = requireContext()
        binding.apply {
            bottomSheetTitle.setTextColor(ctx.getProperTextColor())
            bottomSheetTitle.setTextOrBeGone(title)
            setupContentView(bottomSheetContentHolder)
        }
    }

    abstract fun setupContentView(parent: ViewGroup)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BOTTOM_SHEET_TITLE = "title_string"
    }
}
