package com.dpsoftapps.commons.dialogs

import android.app.Activity
import androidx.core.text.HtmlCompat
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.extensions.getAlertDialogBuilder
import com.dpsoftapps.commons.extensions.humanizePath
import com.dpsoftapps.commons.extensions.setupDialogStuff
import com.dpsoftapps.commons.databinding.DialogWritePermissionBinding
import com.dpsoftapps.commons.databinding.DialogWritePermissionOtgBinding

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
        val glide = Glide.with(activity.applicationContext)
        val crossFade = DrawableTransitionOptions.withCrossFade()

        when (mode) {
            Mode.Otg -> {
                val binding = DialogWritePermissionOtgBinding.inflate(activity.layoutInflater)
                binding.writePermissionsDialogOtgText.setText(R.string.confirm_usb_storage_access_text)
                glide.load(R.drawable.img_write_storage_otg).transition(crossFade).into(binding.writePermissionsDialogOtgImage)

                activity.getAlertDialogBuilder()
                    .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
                    .setOnCancelListener {
                        BaseSimpleActivity.funAfterSAFPermission?.invoke(false)
                        BaseSimpleActivity.funAfterSAFPermission = null
                    }
                    .apply {
                        activity.setupDialogStuff(binding.root, this, dialogTitle) { alertDialog ->
                            dialog = alertDialog
                        }
                    }
            }

            Mode.SdCard -> {
                val binding = DialogWritePermissionBinding.inflate(activity.layoutInflater)
                glide.load(R.drawable.img_write_storage).transition(crossFade).into(binding.writePermissionsDialogImage)
                glide.load(R.drawable.img_write_storage_sd).transition(crossFade).into(binding.writePermissionsDialogImageSd)

                activity.getAlertDialogBuilder()
                    .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
                    .setOnCancelListener {
                        BaseSimpleActivity.funAfterSAFPermission?.invoke(false)
                        BaseSimpleActivity.funAfterSAFPermission = null
                    }
                    .apply {
                        activity.setupDialogStuff(binding.root, this, dialogTitle) { alertDialog ->
                            dialog = alertDialog
                        }
                    }
            }

            is Mode.OpenDocumentTreeSDK30 -> {
                dialogTitle = R.string.confirm_folder_access_title
                val humanizedPath = activity.humanizePath(mode.path)
                val binding = DialogWritePermissionOtgBinding.inflate(activity.layoutInflater)
                binding.writePermissionsDialogOtgText.text = HtmlCompat.fromHtml(activity.getString(R.string.confirm_storage_access_android_text_specific, humanizedPath), HtmlCompat.FROM_HTML_MODE_LEGACY)
                glide.load(R.drawable.img_write_storage_sdk_30).transition(crossFade).into(binding.writePermissionsDialogOtgImage)

                binding.writePermissionsDialogOtgImage.setOnClickListener {
                    dialogConfirmed()
                }

                activity.getAlertDialogBuilder()
                    .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
                    .setOnCancelListener {
                        BaseSimpleActivity.funAfterSAFPermission?.invoke(false)
                        BaseSimpleActivity.funAfterSAFPermission = null
                    }
                    .apply {
                        activity.setupDialogStuff(binding.root, this, dialogTitle) { alertDialog ->
                            dialog = alertDialog
                        }
                    }
            }

            Mode.CreateDocumentSDK30 -> {
                dialogTitle = R.string.confirm_folder_access_title
                val binding = DialogWritePermissionOtgBinding.inflate(activity.layoutInflater)
                binding.writePermissionsDialogOtgText.text = HtmlCompat.fromHtml(activity.getString(R.string.confirm_create_doc_for_new_folder_text), HtmlCompat.FROM_HTML_MODE_LEGACY)
                glide.load(R.drawable.img_write_storage_create_doc_sdk_30).transition(crossFade).into(binding.writePermissionsDialogOtgImage)

                binding.writePermissionsDialogOtgImage.setOnClickListener {
                    dialogConfirmed()
                }

                activity.getAlertDialogBuilder()
                    .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
                    .setOnCancelListener {
                        BaseSimpleActivity.funAfterSAFPermission?.invoke(false)
                        BaseSimpleActivity.funAfterSAFPermission = null
                    }
                    .apply {
                        activity.setupDialogStuff(binding.root, this, dialogTitle) { alertDialog ->
                            dialog = alertDialog
                        }
                    }
            }
        }
    }

    private fun dialogConfirmed() {
        dialog?.dismiss()
        callback()
    }
}
