package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.DialogNewAppsIconsBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.launchViewIntent
import com.dpsoftapps.commons.extensions.setupDialogStuff

class NewAppsIconsDialog(val activity: Activity) {
    init {
        val binding = DialogNewAppsIconsBinding.inflate(activity.layoutInflater)
        val dialerUrl = "https://play.google.com/store/apps/details?id=com.simplemobiletools.dialer"
        val smsMessengerUrl = "https://play.google.com/store/apps/details?id=com.simplemobiletools.smsmessenger"
        val voiceRecorderUrl = "https://play.google.com/store/apps/details?id=com.simplemobiletools.voicerecorder"

        val text = String.format(
            activity.getString(R.string.new_app),
            dialerUrl, activity.getString(R.string.simple_dialer),
            smsMessengerUrl, activity.getString(R.string.simple_sms_messenger),
            voiceRecorderUrl, activity.getString(R.string.simple_voice_recorder)
        )

        binding.newAppsText.text = Html.fromHtml(text)
        binding.newAppsText.movementMethod = LinkMovementMethod.getInstance()

        binding.newAppsDialer.setOnClickListener { activity.launchViewIntent(dialerUrl) }
        binding.newAppsSmsMessenger.setOnClickListener { activity.launchViewIntent(smsMessengerUrl) }
        binding.newAppsVoiceRecorder.setOnClickListener { activity.launchViewIntent(voiceRecorderUrl) }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = false)
            }
    }
}
