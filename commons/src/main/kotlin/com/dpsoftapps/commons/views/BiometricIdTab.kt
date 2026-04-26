package com.dpsoftapps.commons.views

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dpsoftapps.commons.databinding.TabBiometricIdBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.DARK_GREY
import com.dpsoftapps.commons.interfaces.HashListener
import com.dpsoftapps.commons.interfaces.SecurityTab

class BiometricIdTab(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs), SecurityTab {
    private lateinit var hashListener: HashListener
    private lateinit var biometricPromptHost: FragmentActivity
    private lateinit var binding: TabBiometricIdBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = TabBiometricIdBinding.bind(this)
        context.updateTextColors(binding.biometricLockHolder)
        val textColor = if (context.isWhiteTheme()) {
            DARK_GREY
        } else {
            context.getProperPrimaryColor().getContrastColor()
        }

        binding.openBiometricDialog.setTextColor(textColor)
        binding.openBiometricDialog.setOnClickListener {
            (biometricPromptHost as? FragmentActivity)?.showBiometricPrompt(successCallback = hashListener::receivedHash)
        }
    }

    override fun initTab(
        requiredHash: String,
        listener: HashListener,
        scrollView: MyScrollView,
        biometricPromptHost: FragmentActivity,
        showBiometricAuthentication: Boolean
    ) {
        this.biometricPromptHost = biometricPromptHost
        hashListener = listener
        if (showBiometricAuthentication) {
            binding.openBiometricDialog.performClick()
        }
    }

    override fun visibilityChanged(isVisible: Boolean) {}
}
