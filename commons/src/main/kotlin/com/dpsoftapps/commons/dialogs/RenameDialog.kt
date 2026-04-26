package com.dpsoftapps.commons.dialogs

import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.adapters.RenameAdapter
import com.dpsoftapps.commons.databinding.DialogRenameBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.RENAME_PATTERN
import com.dpsoftapps.commons.helpers.RENAME_SIMPLE
import com.dpsoftapps.commons.views.MyViewPager

class RenameDialog(val activity: BaseSimpleActivity, val paths: ArrayList<String>, val useMediaFileExtension: Boolean, val callback: () -> Unit) {
    var dialog: AlertDialog? = null
    private val binding = DialogRenameBinding.inflate(LayoutInflater.from(activity))
    var tabsAdapter: RenameAdapter
    var viewPager: MyViewPager

    init {
        binding.apply {
            viewPager = dialogTabViewPager
            tabsAdapter = RenameAdapter(activity, paths)
            viewPager.adapter = tabsAdapter
            viewPager.onPageChangeListener {
                dialogTabLayout.getTabAt(it)!!.select()
            }
            viewPager.currentItem = activity.baseConfig.lastRenameUsed

            if (activity.baseConfig.isUsingSystemTheme) {
                dialogTabLayout.setBackgroundColor(activity.resources.getColor(R.color.you_dialog_background_color))
            } else {
                dialogTabLayout.setBackgroundColor(root.context.getProperBackgroundColor())
            }

            val textColor = root.context.getProperTextColor()
            dialogTabLayout.setTabTextColors(textColor, textColor)
            dialogTabLayout.setSelectedTabIndicatorColor(root.context.getProperPrimaryColor())

            if (activity.baseConfig.isUsingSystemTheme) {
                dialogTabLayout.setBackgroundColor(activity.resources.getColor(R.color.you_dialog_background_color))
            }

            dialogTabLayout.onTabSelectionChanged(tabSelectedAction = {
                viewPager.currentItem = when {
                    it.text.toString().equals(root.resources.getString(R.string.simple_renaming), true) -> RENAME_SIMPLE
                    else -> RENAME_PATTERN
                }
            })
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel) { dialog, which -> dismissDialog() }
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        tabsAdapter.dialogConfirmed(useMediaFileExtension, viewPager.currentItem) {
                            dismissDialog()
                            if (it) {
                                activity.baseConfig.lastRenameUsed = viewPager.currentItem
                                callback()
                            }
                        }
                    }
                }
            }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }
}
