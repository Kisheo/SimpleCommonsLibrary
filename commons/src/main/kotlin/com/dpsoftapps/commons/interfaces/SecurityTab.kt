package com.dpsoftapps.commons.interfaces

import androidx.fragment.app.FragmentActivity
import com.dpsoftapps.commons.views.MyScrollView

interface SecurityTab {
    fun initTab(
        requiredHash: String,
        listener: HashListener,
        scrollView: MyScrollView,
        biometricPromptHost: FragmentActivity,
        showBiometricAuthentication: Boolean
    )

    fun visibilityChanged(isVisible: Boolean)
}
