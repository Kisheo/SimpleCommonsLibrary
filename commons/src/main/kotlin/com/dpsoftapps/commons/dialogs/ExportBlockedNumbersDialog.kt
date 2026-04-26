package com.dpsoftapps.commons.dialogs

import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.DialogExportBlockedNumbersBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.BLOCKED_NUMBERS_EXPORT_EXTENSION
import com.dpsoftapps.commons.helpers.ensureBackgroundThread
import java.io.File

class ExportBlockedNumbersDialog(
    val activity: BaseSimpleActivity,
    val path: String,
    val hidePath: Boolean,
    callback: (file: File) -> Unit,
) {
    private var realPath = if (path.isEmpty()) activity.internalStoragePath else path
    private val config = activity.baseConfig

    init {
        val binding = DialogExportBlockedNumbersBinding.inflate(activity.layoutInflater)
        binding.exportBlockedNumbersFolder.text = activity.humanizePath(realPath)
        binding.exportBlockedNumbersFilename.setText("${activity.getString(R.string.blocked_numbers)}_${activity.getCurrentFormattedDateTime()}")

        if (hidePath) {
            binding.exportBlockedNumbersFolderLabel.beGone()
            binding.exportBlockedNumbersFolder.beGone()
        } else {
            binding.exportBlockedNumbersFolder.setOnClickListener {
                FilePickerDialog(activity, realPath, false, showFAB = true) {
                    binding.exportBlockedNumbersFolder.text = activity.humanizePath(it)
                    realPath = it
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.export_blocked_numbers) { alertDialog ->
                    alertDialog.showKeyboard(binding.exportBlockedNumbersFilename)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val filename = binding.exportBlockedNumbersFilename.value
                        when {
                            filename.isEmpty() -> activity.toast(R.string.empty_name)
                            filename.isAValidFilename() -> {
                                val file = File(realPath, "$filename$BLOCKED_NUMBERS_EXPORT_EXTENSION")
                                if (!hidePath && file.exists()) {
                                    activity.toast(R.string.name_taken)
                                    return@setOnClickListener
                                }

                                ensureBackgroundThread {
                                    config.lastBlockedNumbersExportPath = file.absolutePath.getParentPath()
                                    callback(file)
                                    alertDialog.dismiss()
                                }
                            }
                            else -> activity.toast(R.string.invalid_name)
                        }
                    }
                }
            }
    }
}
