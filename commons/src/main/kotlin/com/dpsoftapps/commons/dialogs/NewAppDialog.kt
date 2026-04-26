package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.core.text.HtmlCompat
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogTextviewBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff

class NewAppDialog(val activity: Activity, val packageName: String, val title: String, val packageName2: String, val title2: String) {
    init {
        val binding = DialogTextviewBinding.inflate(activity.layoutInflater)

        // Build a small localized-ish HTML snippet without using resource formatting (avoids locale placeholder mismatches)
        val url1 = "https://play.google.com/store/apps/details?id=$packageName"
        val url2 = "https://play.google.com/store/apps/details?id=$packageName2"
        val text = "Hey,<br><br> just letting you know that some new apps have been released recently:<br><br> " +
                "<a href='$url1'>$title</a><br><br><a href='$url2'>$title2</a><br><br>Thanks"

        binding.textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false)
            }
    }
}
