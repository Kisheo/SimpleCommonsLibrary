package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AlertDialog
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogFeatureLockedBinding
import com.dpsoftapps.commons.extensions.*

class FeatureLockedDialog(val activity: Activity, val callback: () -> Unit) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogFeatureLockedBinding.inflate(activity.layoutInflater)
        binding.featureLockedImage.applyColorFilter(activity.getProperTextColor())

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase, null)
            .setNegativeButton(R.string.later) { dialog, which -> dismissDialog() }
            .setOnDismissListener { dismissDialog() }
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false) { alertDialog ->
                    dialog = alertDialog
                    binding.featureLockedDescription.text = Html.fromHtml(activity.getString(R.string.features_locked))
                    binding.featureLockedDescription.movementMethod = LinkMovementMethod.getInstance()

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        activity.launchPurchaseThankYouIntent()
                    }
                }
            }
    }

    fun dismissDialog() {
        dialog?.dismiss()
        callback()
    }
}
