package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.view.View
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.R.id.conflict_dialog_radio_keep_both
import com.dpsoftapps.commons.R.id.conflict_dialog_radio_merge
import com.dpsoftapps.commons.R.id.conflict_dialog_radio_skip
import com.dpsoftapps.commons.databinding.DialogFileConflictBinding
import com.dpsoftapps.commons.extensions.baseConfig
import com.dpsoftapps.commons.extensions.beVisibleIf
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff
import com.dpsoftapps.commons.helpers.CONFLICT_KEEP_BOTH
import com.dpsoftapps.commons.helpers.CONFLICT_MERGE
import com.dpsoftapps.commons.helpers.CONFLICT_OVERWRITE
import com.dpsoftapps.commons.helpers.CONFLICT_SKIP
import com.dpsoftapps.commons.models.FileDirItem

class FileConflictDialog(
    val activity: Activity, val fileDirItem: FileDirItem, val showApplyToAllCheckbox: Boolean,
    val callback: (resolution: Int, applyForAll: Boolean) -> Unit
) {
    private val binding = DialogFileConflictBinding.inflate(activity.layoutInflater)

    init {
        binding.apply {
            val stringBase = if (fileDirItem.isDirectory) R.string.folder_already_exists else R.string.file_already_exists
            conflictDialogTitle.text = String.format(activity.getString(stringBase), fileDirItem.name)
            conflictDialogApplyToAll.setChecked(activity.baseConfig.lastConflictApplyToAll)
            conflictDialogApplyToAll.beVisibleIf(showApplyToAllCheckbox)
            binding.root.findViewById<View>(R.id.conflict_dialog_divider).beVisibleIf(showApplyToAllCheckbox)
            conflictDialogRadioMerge.beVisibleIf(fileDirItem.isDirectory)

            val resolutionButton = when (activity.baseConfig.lastConflictResolution) {
                CONFLICT_OVERWRITE -> conflictDialogRadioOverwrite
                CONFLICT_MERGE -> conflictDialogRadioMerge
                else -> conflictDialogRadioSkip
            }
            resolutionButton.setChecked(true)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        val resolution = when (binding.conflictDialogRadioGroup.checkedRadioButtonId) {
            conflict_dialog_radio_skip -> CONFLICT_SKIP
            conflict_dialog_radio_merge -> CONFLICT_MERGE
            conflict_dialog_radio_keep_both -> CONFLICT_KEEP_BOTH
            else -> CONFLICT_OVERWRITE
        }

        val applyToAll = binding.conflictDialogApplyToAll.isChecked()
        activity.baseConfig.apply {
            lastConflictApplyToAll = applyToAll
            lastConflictResolution = resolution
        }

        callback(resolution, applyToAll)
    }
}
