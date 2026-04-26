package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.getStringsPackageName
import com.dpsoftapps.commons.extensions.launchViewIntent
import com.dpsoftapps.commons.extensions.setupDialogStuff
import com.dpsoftapps.commons.databinding.DialogTextviewBinding

class AppSideloadedDialog(val activity: Activity, val callback: () -> Unit) {
    private var dialog: AlertDialog? = null
    private val url = "https://play.google.com/store/apps/details?id=${activity.getStringsPackageName()}"

    init {
        val binding = DialogTextviewBinding.inflate(activity.layoutInflater)
        val text = String.format(activity.getString(R.string.sideloaded_app), url)
        binding.textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()

        activity.getAlertDialogBuilder()
            .setNegativeButton(R.string.cancel) { dialog, which -> negativePressed() }
            .setPositiveButton(R.string.download, null)
            .setOnCancelListener { negativePressed() }
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.app_corrupt) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        downloadApp()
                    }
                }
            }
    }

    private fun downloadApp() {
        activity.launchViewIntent(url)
    }

    private fun negativePressed() {
        dialog?.dismiss()
        callback()
    }
}
