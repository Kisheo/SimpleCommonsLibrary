package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogRateStarsBinding
import com.dpsoftapps.commons.extensions.*

class RateStarsDialog(val activity: Activity) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogRateStarsBinding.inflate(activity.layoutInflater)
        binding.apply {
            val primaryColor = activity.getProperPrimaryColor()
            arrayOf(rateStar1, rateStar2, rateStar3, rateStar4, rateStar5).forEach {
                it.applyColorFilter(primaryColor)
            }

            rateStar1.setOnClickListener { dialogCancelled(true) }
            rateStar2.setOnClickListener { dialogCancelled(true) }
            rateStar3.setOnClickListener { dialogCancelled(true) }
            rateStar4.setOnClickListener { dialogCancelled(true) }
            rateStar5.setOnClickListener {
                activity.redirectToRateUs()
                dialogCancelled(true)
            }
        }

        activity.getAlertDialogBuilder()
            .setNegativeButton(R.string.later) { dialog, which -> dialogCancelled(false) }
            .setOnCancelListener { dialogCancelled(false) }
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun dialogCancelled(showThankYou: Boolean) {
        dialog?.dismiss()
        if (showThankYou) {
            activity.toast(R.string.thank_you)
            activity.baseConfig.wasAppRated = true
        }
    }
}
