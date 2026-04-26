package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogFeatureLockedBinding
import com.dpsoftapps.commons.extensions.*

class FeatureLockedDialog(val activity: Activity, val callback: () -> Unit) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogFeatureLockedBinding.inflate(activity.layoutInflater)
        val view: View = binding.root
        binding.featureLockedImage.applyColorFilter(activity.getProperTextColor())

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase, null)
            .setNegativeButton(R.string.later) { dialog, which -> dismissDialog() }
            .setOnDismissListener { dismissDialog() }
            .apply {
                activity.setupDialogStuff(view, this, cancelOnTouchOutside = false) { alertDialog ->
                    dialog = alertDialog
                    binding.featureLockedDescription.text = HtmlCompat.fromHtml(activity.getString(R.string.features_locked), HtmlCompat.FROM_HTML_MODE_LEGACY)
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
