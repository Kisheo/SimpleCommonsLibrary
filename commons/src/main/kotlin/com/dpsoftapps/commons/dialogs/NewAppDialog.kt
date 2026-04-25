package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogTextviewBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff

class NewAppDialog(val activity: Activity, val packageName: String, val title: String, val packageName2: String, val title2: String) {
    init {
        val binding = DialogTextviewBinding.inflate(activity.layoutInflater)
        val text = String.format(
            activity.getString(R.string.new_app),
            "https://play.google.com/store/apps/details?id=$packageName", title,
            "https://play.google.com/store/apps/details?id=$packageName2", title2
        )
        binding.textView.text = Html.fromHtml(text)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false)
            }
    }
}
