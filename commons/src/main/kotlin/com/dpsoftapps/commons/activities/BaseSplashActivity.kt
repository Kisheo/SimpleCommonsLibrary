package com.dpsoftapps.commons.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.SIDELOADING_TRUE
import com.dpsoftapps.commons.helpers.SIDELOADING_UNCHECKED

abstract class BaseSplashActivity : AppCompatActivity() {
    abstract fun initActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (baseConfig.appSideloadingStatus == SIDELOADING_UNCHECKED) {
            if (checkAppSideloading()) {
                return
            }
        } else if (baseConfig.appSideloadingStatus == SIDELOADING_TRUE) {
            showSideloadingDialog()
            return
        }

        baseConfig.apply {
            if (isUsingAutoTheme) {
                val isUsingSystemDarkTheme = isUsingSystemDarkTheme()
                isUsingSharedTheme = false
                textColor = ResourcesCompat.getColor(resources, if (isUsingSystemDarkTheme) R.color.theme_dark_text_color else R.color.theme_light_text_color, theme)
                backgroundColor = ResourcesCompat.getColor(resources, if (isUsingSystemDarkTheme) R.color.theme_dark_background_color else R.color.theme_light_background_color, theme)
            }
        }

        if (!baseConfig.isUsingAutoTheme && !baseConfig.isUsingSystemTheme /* todo && isThankYouInstalled()*/) {
            getSharedTheme {
                if (it != null) {
                    baseConfig.apply {
                        wasSharedThemeForced = true
                        isUsingSharedTheme = true
                        wasSharedThemeEverActivated = true

                        textColor = it.textColor
                        backgroundColor = it.backgroundColor
                        primaryColor = it.primaryColor
                        accentColor = it.accentColor
                    }

                    if (baseConfig.appIconColor != it.appIconColor) {
                        baseConfig.appIconColor = it.appIconColor
                        checkAppIconColor()
                    }
                }
                initActivity()
            }
        } else {
            initActivity()
        }
    }
}
