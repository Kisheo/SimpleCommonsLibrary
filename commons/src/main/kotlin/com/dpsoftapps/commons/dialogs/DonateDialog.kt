package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogDonateBinding
import com.dpsoftapps.commons.extensions.*

class DonateDialog(val activity: Activity) {
    init {
        val binding = DialogDonateBinding.inflate(activity.layoutInflater)
        binding.dialogDonateImage.applyColorFilter(activity.getProperTextColor())
        binding.dialogDonateText.text = Html.fromHtml(activity.getString(R.string.donate_short))
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
