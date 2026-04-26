package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.core.text.HtmlCompat
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogDonateBinding
import com.dpsoftapps.commons.extensions.*

class DonateDialog(val activity: Activity) {
    init {
        val binding = DialogDonateBinding.inflate(activity.layoutInflater)
        binding.dialogDonateImage.applyColorFilter(activity.getProperTextColor())
        binding.dialogDonateText.text = HtmlCompat.fromHtml(activity.getString(R.string.donate_short), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.dialogDonateText.movementMethod = LinkMovementMethod.getInstance()
        binding.dialogDonateImage.setOnClickListener {
            activity.launchViewIntent(R.string.thank_you_url)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase) { dialog, which -> activity.launchViewIntent(R.string.thank_you_url) }
            .setNegativeButton(R.string.later, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false)
            }
    }
}
