package com.dpsoftapps.commons.dialogs

import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.DialogRenameItemsBinding
import com.dpsoftapps.commons.extensions.*

// used at renaming folders
class RenameItemsDialog(val activity: BaseSimpleActivity, val paths: ArrayList<String>, val callback: () -> Unit) {
    init {
        var ignoreClicks = false
        val binding = DialogRenameItemsBinding.inflate(activity.layoutInflater)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.rename) { alertDialog ->
                    alertDialog.showKeyboard(binding.renameItemsValue)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (ignoreClicks) {
                            return@setOnClickListener
                        }

                        val valueToAdd = binding.renameItemsValue.text.toString()
                        val append = binding.renameItemsRadioGroup.checkedRadioButtonId == binding.renameItemsRadioAppend.id

                        if (valueToAdd.isEmpty()) {
                            callback()
                            alertDialog.dismiss()
                            return@setOnClickListener
                        }

                        if (!valueToAdd.isAValidFilename()) {
                            activity.toast(R.string.invalid_name)
                            return@setOnClickListener
                        }

                        val validPaths = paths.filter { activity.getDoesFilePathExist(it) }
                        val sdFilePath = validPaths.firstOrNull { activity.isPathOnSD(it) } ?: validPaths.firstOrNull()
                        if (sdFilePath == null) {
                            activity.toast(R.string.unknown_error_occurred)
                            alertDialog.dismiss()
                            return@setOnClickListener
                        }

                        activity.handleSAFDialog(sdFilePath) {
                            if (!it) {
                                return@handleSAFDialog
                            }

                            ignoreClicks = true
                            var pathsCnt = validPaths.size
                            for (path in validPaths) {
                                val fullName = path.getFilenameFromPath()
                                var dotAt = fullName.lastIndexOf(".")
                                if (dotAt == -1) {
                                    dotAt = fullName.length
                                }

                                val name = fullName.substring(0, dotAt)
                                val extension = if (fullName.contains(".")) ".${fullName.getFilenameExtension()}" else ""

                                val newName = if (append) {
                                    "$name$valueToAdd$extension"
                                } else {
                                    "$valueToAdd$fullName"
                                }

                                val newPath = "${path.getParentPath()}/$newName"

                                if (activity.getDoesFilePathExist(newPath)) {
                                    continue
                                }

                                activity.renameFile(path, newPath, true) { success, _ ->
                                    if (success) {
                                        pathsCnt--
                                        if (pathsCnt == 0) {
                                            callback()
                                            alertDialog.dismiss()
                                        }
                                    } else {
                                        ignoreClicks = false
                                        alertDialog.dismiss()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }
}
