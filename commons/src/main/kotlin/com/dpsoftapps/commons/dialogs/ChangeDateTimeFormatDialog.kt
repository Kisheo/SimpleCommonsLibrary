package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.format.DateFormat
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.R.id.*
import com.dpsoftapps.commons.extensions.baseConfig
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.setupDialogStuff
import com.dpsoftapps.commons.helpers.*
import com.dpsoftapps.commons.databinding.DialogChangeDateTimeFormatBinding
import java.util.*

class ChangeDateTimeFormatDialog(val activity: Activity, val callback: () -> Unit) {
    private val binding = DialogChangeDateTimeFormatBinding.inflate(activity.layoutInflater)
    private val sampleTS = 1613422500000    // February 15, 2021

    init {
        binding.apply {
            changeDateTimeDialogRadioOne.text = formatDateSample(DATE_FORMAT_ONE)
            changeDateTimeDialogRadioTwo.text = formatDateSample(DATE_FORMAT_TWO)
            changeDateTimeDialogRadioThree.text = formatDateSample(DATE_FORMAT_THREE)
            changeDateTimeDialogRadioFour.text = formatDateSample(DATE_FORMAT_FOUR)
            changeDateTimeDialogRadioFive.text = formatDateSample(DATE_FORMAT_FIVE)
            changeDateTimeDialogRadioSix.text = formatDateSample(DATE_FORMAT_SIX)
            changeDateTimeDialogRadioSeven.text = formatDateSample(DATE_FORMAT_SEVEN)
            changeDateTimeDialogRadioEight.text = formatDateSample(DATE_FORMAT_EIGHT)

            changeDateTimeDialog24Hour.setChecked(activity.baseConfig.use24HourFormat)

            val formatButton = when (activity.baseConfig.dateFormat) {
                DATE_FORMAT_ONE -> changeDateTimeDialogRadioOne
                DATE_FORMAT_TWO -> changeDateTimeDialogRadioTwo
                DATE_FORMAT_THREE -> changeDateTimeDialogRadioThree
                DATE_FORMAT_FOUR -> changeDateTimeDialogRadioFour
                DATE_FORMAT_FIVE -> changeDateTimeDialogRadioFive
                DATE_FORMAT_SIX -> changeDateTimeDialogRadioSix
                DATE_FORMAT_SEVEN -> changeDateTimeDialogRadioSeven
                else -> changeDateTimeDialogRadioEight
            }
            formatButton.setChecked(true)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        activity.baseConfig.dateFormat = when (binding.changeDateTimeDialogRadioGroup.checkedRadioButtonId) {
            change_date_time_dialog_radio_one -> DATE_FORMAT_ONE
            change_date_time_dialog_radio_two -> DATE_FORMAT_TWO
            change_date_time_dialog_radio_three -> DATE_FORMAT_THREE
            change_date_time_dialog_radio_four -> DATE_FORMAT_FOUR
            change_date_time_dialog_radio_five -> DATE_FORMAT_FIVE
            change_date_time_dialog_radio_six -> DATE_FORMAT_SIX
            change_date_time_dialog_radio_seven -> DATE_FORMAT_SEVEN
            else -> DATE_FORMAT_EIGHT
        }

        activity.baseConfig.use24HourFormat = binding.changeDateTimeDialog24Hour.isChecked()
        callback()
    }

    private fun formatDateSample(format: String): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = sampleTS
        return DateFormat.format(format, cal).toString()
    }
}
