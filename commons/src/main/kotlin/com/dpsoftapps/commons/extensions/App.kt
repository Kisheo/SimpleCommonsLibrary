package com.dpsoftapps.commons.extensions

import android.app.Application
import com.dpsoftapps.commons.helpers.isNougatPlus
import java.util.*

fun Application.checkUseEnglish() {
    if (baseConfig.useEnglish && !isNougatPlus()) {
        val conf = resources.configuration
        conf.locale = Locale.ENGLISH
        resources.updateConfiguration(conf, resources.displayMetrics)
    }
}
