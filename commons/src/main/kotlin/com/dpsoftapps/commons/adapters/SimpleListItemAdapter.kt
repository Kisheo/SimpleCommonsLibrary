package com.dpsoftapps.commons.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.ItemSimpleListBinding
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.models.SimpleListItem

open class SimpleListItemAdapter(val activity: Activity, val onItemClicked: (SimpleListItem) -> Unit) :
    ListAdapter<SimpleListItem, SimpleListItemAdapter.SimpleItemViewHolder>(SimpleListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemViewHolder {
        val view = activity.layoutInflater.inflate(R.layout.item_simple_list, parent, false)
        return SimpleItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimpleItemViewHolder, position: Int) {
        val route = getItem(position)
        holder.bindView(route)
    }

    open inner class SimpleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: SimpleListItem) {
            setupSimpleListItem(itemView, item, onItemClicked)
        }
    }

    private class SimpleListItemDiffCallback : DiffUtil.ItemCallback<SimpleListItem>() {
        override fun areItemsTheSame(oldItem: SimpleListItem, newItem: SimpleListItem): Boolean {
            return SimpleListItem.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: SimpleListItem, newItem: SimpleListItem): Boolean {
            return SimpleListItem.areContentsTheSame(oldItem, newItem)
        }
    }
}

fun setupSimpleListItem(view: View, item: SimpleListItem, onItemClicked: (SimpleListItem) -> Unit) {
    val binding = ItemSimpleListBinding.bind(view)
    val color = if (item.selected) {
        view.context.getProperPrimaryColor()
    } else {
        view.context.getProperTextColor()
    }

    binding.bottomSheetItemTitle.setText(item.textRes)
    binding.bottomSheetItemTitle.setTextColor(color)
    binding.bottomSheetItemIcon.setImageResourceOrBeGone(item.imageRes)
    binding.bottomSheetItemIcon.applyColorFilter(color)
    binding.bottomSheetSelectedIcon.beVisibleIf(item.selected)
    binding.bottomSheetSelectedIcon.applyColorFilter(color)

    view.setOnClickListener {
        onItemClicked(item)
    }
}
