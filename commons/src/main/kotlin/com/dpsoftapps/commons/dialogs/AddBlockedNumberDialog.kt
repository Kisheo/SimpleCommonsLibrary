package com.dpsoftapps.commons.dialogs

import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.DialogAddBlockedNumberBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.models.BlockedNumber

class AddBlockedNumberDialog(val activity: BaseSimpleActivity, val originalNumber: BlockedNumber? = null, val callback: () -> Unit) {
    init {
        val binding = DialogAddBlockedNumberBinding.inflate(activity.layoutInflater).apply {
            if (originalNumber != null) {
                addBlockedNumberEdittext.setText(originalNumber.number)
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.addBlockedNumberEdittext)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        var newBlockedNumber = binding.addBlockedNumberEdittext.value
                        if (originalNumber != null && newBlockedNumber != originalNumber.number) {
                            activity.deleteBlockedNumber(originalNumber.number)
                        }

                        if (newBlockedNumber.isNotEmpty()) {
                            // in case the user also added a '.' in the pattern, remove it
                            if (newBlockedNumber.contains(".*")) {
                                newBlockedNumber = newBlockedNumber.replace(".*", "*")
                            }
                            activity.addBlockedNumber(newBlockedNumber)
                        }

                        callback()
                        alertDialog.dismiss()
                    }
                }
            }
    }
}
