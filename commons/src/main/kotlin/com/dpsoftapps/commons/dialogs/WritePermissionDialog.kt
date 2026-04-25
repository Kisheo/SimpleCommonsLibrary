package com.dpsoftapps.commons.dialogs

import android.app.Activity
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.DialogWritePermissionBinding
import com.dpsoftapps.commons.databinding.DialogWritePermissionOtgBinding
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.humanizePath
import com.dpsoftapps.commons.extensions.setupDialogStuff

class WritePermissionDialog(activity: Activity, val mode: Mode, val callback: () -> Unit) {
    sealed class Mode {
        object Otg : Mode()
        object SdCard : Mode()
        data class OpenDocumentTreeSDK30(val path: String) : Mode()
        object CreateDocumentSDK30 : Mode()
    }

    private var dialog: AlertDialog? = null

    init {
        var dialogTitle = R.string.confirm_storage_access_title
        val glide = Glide.with(activity)
        val crossFade = DrawableTransitionOptions.withCrossFade()

        val view = if (mode == Mode.SdCard) {
            val binding = DialogWritePermissionBinding.inflate(activity.layoutInflater)
            glide.load(R.drawable.img_write_storage).transition(crossFade).into(binding.writePermissionsDialogImage)
            glide.load(R.drawable.img_write_storage_sd).transition(crossFade).into(binding.writePermissionsDialogImageSd)
            binding.root
        } else {
            val binding = DialogWritePermissionOtgBinding.inflate(activity.layoutInflater)
            when (mode) {
                Mode.Otg -> {
                    binding.writePermissionsDialogOtgText.setText(R.string.confirm_usb_storage_access_text)
                    glide.load(R.drawable.img_write_storage_otg).transition(crossFade).into(binding.writePermissionsDialogOtgImage)
                }
                is Mode.OpenDocumentTreeSDK30 -> {
                    dialogTitle = R.string.confirm_folder_access_title
                    val humanizedPath = activity.humanizePath(mode.path)
                    binding.writePermissionsDialogOtgText.text =
                        Html.fromHtml(activity.getString(R.string.confirm_storage_access_android_text_specific, humanizedPath))
                    glide.load(R.drawable.img_write_storage_sdk_30).transition(crossFade).into(binding.writePermissionsDialogOtgImage)
                    binding.writePermissionsDialogOtgImage.setOnClickListener { dialogConfirmed() }
                }
                Mode.CreateDocumentSDK30 -> {
                    dialogTitle = R.string.confirm_folder_access_title
                    binding.writePermissionsDialogOtgText.text = Html.fromHtml(activity.getString(R.string.confirm_create_doc_for_new_folder_text))
                    glide.load(R.drawable.img_write_storage_create_doc_sdk_30).transition(crossFade).into(binding.writePermissionsDialogOtgImage)
                    binding.writePermissionsDialogOtgImage.setOnClickListener { dialogConfirmed() }
                }
                else -> {}
            }
            binding.root
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setOnCancelListener {
                BaseSimpleActivity.funAfterSAFPermission?.invoke(false)
                BaseSimpleActivity.funAfterSAFPermission = null
            }
            .apply {
                activity.setupDialogStuff(view, this, dialogTitle) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun dialogConfirmed() {
        dialog?.dismiss()
        callback()
    }
}
