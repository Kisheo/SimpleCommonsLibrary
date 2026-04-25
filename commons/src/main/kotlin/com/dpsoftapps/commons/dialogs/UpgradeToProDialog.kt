package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogUpgradeToProBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.launchUpgradeToProIntent
import com.dpsoftapps.commons.extensions.launchViewIntent
import com.dpsoftapps.commons.extensions.setupDialogStuff

class UpgradeToProDialog(val activity: Activity) {

    init {
        val binding = DialogUpgradeToProBinding.inflate(activity.layoutInflater)
        binding.upgradeToPro.text = activity.getString(R.string.upgrade_to_pro_long)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.upgrade) { dialog, which -> upgradeApp() }
            .setNeutralButton(R.string.more_info, null)     // do not dismiss the dialog on pressing More Info
            .setNegativeButton(R.string.later, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.upgrade_to_pro, cancelOnTouchOutside = false) { alertDialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        moreInfo()
                    }
                }
            }
    }

    private fun upgradeApp() {
        activity.launchUpgradeToProIntent()
    }

    private fun moreInfo() {
        activity.launchViewIntent("https://simplemobiletools.com/upgrade_to_pro")
    }
}
