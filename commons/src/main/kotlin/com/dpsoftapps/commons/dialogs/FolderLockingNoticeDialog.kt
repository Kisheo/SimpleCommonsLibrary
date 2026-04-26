package com.dpsoftapps.commons.dialogs

import android.app.Activity
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogTextviewBinding
import com.dpsoftapps.commons.extensions.baseConfig
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff

class FolderLockingNoticeDialog(val activity: Activity, val callback: () -> Unit) {
    init {
        val binding = DialogTextviewBinding.inflate(activity.layoutInflater)
        binding.textView.text = activity.getString(R.string.lock_folder_notice)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.disclaimer)
            }
    }

    private fun dialogConfirmed() {
        activity.baseConfig.wasFolderLockingNoticeShown = true
        callback()
    }
}
