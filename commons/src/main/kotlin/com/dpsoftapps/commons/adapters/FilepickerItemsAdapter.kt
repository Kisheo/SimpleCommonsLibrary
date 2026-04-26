package com.dpsoftapps.commons.adapters

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.ItemFilepickerListBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.getFilePlaceholderDrawables
import com.dpsoftapps.commons.models.FileDirItem
import com.dpsoftapps.commons.views.MyRecyclerView
import java.util.*

class FilepickerItemsAdapter(
    activity: BaseSimpleActivity, val fileDirItems: List<FileDirItem>, recyclerView: MyRecyclerView,
    itemClick: (Any) -> Unit
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick), RecyclerViewFastScroller.OnPopupTextUpdate {

    private lateinit var fileDrawable: Drawable
    private lateinit var folderDrawable: Drawable
    private var fileDrawables = HashMap<String, Drawable>()
    private val hasOTGConnected = activity.hasOTGConnected()
    private var fontSize = 0f
    private val cornerRadius = resources.getDimension(R.dimen.rounded_corner_radius_small).toInt()
    private val dateFormat = activity.baseConfig.dateFormat
    private val timeFormat = activity.getTimeFormat()

    init {
        initDrawables()
        fontSize = activity.getTextSize()
    }

    override fun getActionMenuId() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(R.layout.item_filepicker_list, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileDirItem = fileDirItems[position]
        holder.bindView(fileDirItem, true, false) { itemView, adapterPosition ->
            setupView(itemView, fileDirItem)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = fileDirItems.size

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {}

    override fun getSelectableItemCount() = fileDirItems.size

    override fun getIsItemSelectable(position: Int) = false

    override fun getItemKeyPosition(key: Int) = fileDirItems.indexOfFirst { it.path.hashCode() == key }

    override fun getItemSelectionKey(position: Int) = fileDirItems[position].path.hashCode()

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (!activity.isDestroyed && !activity.isFinishing) {
            // Use ViewBinding to access the image view on the recycled holder
            val binding = ItemFilepickerListBinding.bind(holder.itemView)
            Glide.with(activity).clear(binding.listItemIcon)
        }
    }

    private fun setupView(view: View, fileDirItem: FileDirItem) {
        val binding = ItemFilepickerListBinding.bind(view)
        binding.apply {
            listItemName.text = fileDirItem.name
            listItemName.setTextColor(textColor)
            listItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)

            listItemDetails.setTextColor(textColor)
            listItemDetails.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)

            if (fileDirItem.isDirectory) {
                listItemIcon.setImageDrawable(folderDrawable)
                listItemDetails.text = getChildrenCnt(fileDirItem)
            } else {
                listItemDetails.text = fileDirItem.size.formatSize()
                val path = fileDirItem.path
                val extKey = fileDirItem.name.substringAfterLast('.').lowercase(Locale.getDefault())
                val placeholder = fileDrawables.getOrElse(extKey, { fileDrawable })
                val options = RequestOptions()
                    .signature(fileDirItem.getKey())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .error(placeholder)

                // itemToLoad can be a Drawable (for apk icons), a String path, or a Uri for SAF
                var itemToLoad: Any = if (fileDirItem.name.endsWith(".apk", true)) {
                    val packageInfo = activity.packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
                    if (packageInfo != null) {
                        try {
                            // Prefer to get the application icon from the PackageManager using package name
                            activity.packageManager.getApplicationIcon(packageInfo.packageName)
                        } catch (e: Exception) {
                            // Fallback to the file path if we can't load the icon
                            path
                        }
                    } else {
                        path
                    }
                } else {
                    path
                }

                if (!activity.isDestroyed && !activity.isFinishing) {
                    if (activity.isRestrictedSAFOnlyRoot(path)) {
                        itemToLoad = activity.getAndroidSAFUri(path)
                    } else if (hasOTGConnected && itemToLoad is String && activity.isPathOnOTG(itemToLoad)) {
                        itemToLoad = itemToLoad.getOTGPublicPath(activity)
                    }

                    // itemToLoad may be a Drawable, Uri, or String - Glide supports all of them
                    if (itemToLoad.toString().isGif()) {
                        Glide.with(activity).asBitmap().load(itemToLoad).apply(options).into(listItemIcon)
                    } else {
                        Glide.with(activity)
                            .load(itemToLoad)
                            .transition(withCrossFade())
                            .apply(options)
                            .transform(CenterCrop(), RoundedCorners(cornerRadius))
                            .into(listItemIcon)
                    }
                }
            }
        }
    }

    private fun getChildrenCnt(item: FileDirItem): String {
        val children = item.children
        return activity.resources.getQuantityString(R.plurals.items, children, children)
    }

    private fun initDrawables() {
        folderDrawable = resources.getColoredDrawableWithColor(R.drawable.ic_folder_vector, textColor)
        folderDrawable.alpha = 180
        fileDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_file_generic, null) ?: throw IllegalStateException("Drawable missing")
        fileDrawables = getFilePlaceholderDrawables(activity)
    }

    override fun onChange(position: Int) = fileDirItems.getOrNull(position)?.getBubbleText(activity, dateFormat, timeFormat) ?: ""
}
