package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.core.text.HtmlCompat
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogPurchaseThankYouBinding
import com.dpsoftapps.commons.extensions.*

class PurchaseThankYouDialog(val activity: Activity) {
    init {
        val binding = DialogPurchaseThankYouBinding.inflate(activity.layoutInflater)
        var text = activity.getString(R.string.purchase_thank_you)
        if (activity.baseConfig.appId.removeSuffix(".debug").endsWith(".pro")) {
            text += "<br><br>${activity.getString(R.string.shared_theme_note)}"
        }

        binding.purchaseThankYou.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.purchaseThankYou.movementMethod = LinkMovementMethod.getInstance()
        binding.purchaseThankYou.removeUnderlines()

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase) { dialog, which -> activity.launchPurchaseThankYouIntent() }
            .setNegativeButton(R.string.later, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false)
            }
    }
}
