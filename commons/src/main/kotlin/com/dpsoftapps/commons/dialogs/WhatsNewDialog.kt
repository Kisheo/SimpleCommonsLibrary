package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.view.LayoutInflater
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogWhatsNewBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff
import com.dpsoftapps.commons.models.Release

class WhatsNewDialog(val activity: Activity, val releases: List<Release>) {
    init {
        val binding = DialogWhatsNewBinding.inflate(LayoutInflater.from(activity))
        binding.whatsNewContent.text = getNewReleases()

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.whats_new, cancelOnTouchOutside = false)
            }
    }

    private fun getNewReleases(): String {
        val sb = StringBuilder()

        releases.forEach {
            val parts = activity.getString(it.textId).split("\n").map(String::trim)
            parts.forEach {
                sb.append("- $it\n")
            }
        }

        return sb.toString()
    }
}
