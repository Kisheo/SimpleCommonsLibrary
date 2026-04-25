package com.dpsoftapps.commons.dialogs

import android.view.animation.AnimationUtils
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.DialogCallConfirmationBinding
import com.dpsoftapps.commons.extensions.applyColorFilter
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.getProperTextColor
import com.dpsoftapps.commons.extensions.setupDialogStuff

class CallConfirmationDialog(val activity: BaseSimpleActivity, val callee: String, private val callback: () -> Unit) {
    private val binding = DialogCallConfirmationBinding.inflate(activity.layoutInflater)

    init {
        binding.callConfirmPhone.applyColorFilter(activity.getProperTextColor())
        activity.getAlertDialogBuilder()
            .setNegativeButton(R.string.cancel, null)
            .apply {
                val title = String.format(activity.getString(R.string.confirm_calling_person), callee)
                activity.setupDialogStuff(binding.root, this, titleText = title) { alertDialog ->
                    binding.callConfirmPhone.apply {
                        startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake_pulse_animation))
                        setOnClickListener {
                            callback.invoke()
                            alertDialog.dismiss()
                        }
                    }
                }
            }
    }
}
