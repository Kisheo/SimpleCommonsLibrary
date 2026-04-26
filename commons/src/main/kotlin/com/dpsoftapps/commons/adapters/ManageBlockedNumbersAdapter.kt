package com.dpsoftapps.commons.adapters

import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.activities.BaseSimpleActivity
import com.dpsoftapps.commons.databinding.ItemManageBlockedNumberBinding
import com.dpsoftapps.commons.extensions.copyToClipboard
import com.dpsoftapps.commons.extensions.deleteBlockedNumber
import com.dpsoftapps.commons.extensions.getPopupMenuTheme
import com.dpsoftapps.commons.extensions.getProperTextColor
import com.dpsoftapps.commons.interfaces.RefreshRecyclerViewListener
import com.dpsoftapps.commons.models.BlockedNumber
import com.dpsoftapps.commons.views.MyRecyclerView

class ManageBlockedNumbersAdapter(
    activity: BaseSimpleActivity, var blockedNumbers: ArrayList<BlockedNumber>, val listener: RefreshRecyclerViewListener?,
    recyclerView: MyRecyclerView, itemClick: (Any) -> Unit
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {
    init {
        setupDragListener(true)
    }

    override fun getActionMenuId() = R.menu.cab_blocked_numbers

    override fun prepareActionMode(menu: Menu) {
        menu.apply {
            findItem(R.id.cab_copy_number).isVisible = isOneItemSelected()
        }
    }

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            R.id.cab_copy_number -> copyNumberToClipboard()
            R.id.cab_delete -> deleteSelection()
        }
    }

    override fun getSelectableItemCount() = blockedNumbers.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = blockedNumbers.getOrNull(position)?.id?.toInt()

    override fun getItemKeyPosition(key: Int) = blockedNumbers.indexOfFirst { it.id.toInt() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(R.layout.item_manage_blocked_number, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blockedNumber = blockedNumbers[position]
        holder.bindView(blockedNumber, true, true) { itemView, _ ->
            setupView(itemView, blockedNumber)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = blockedNumbers.size

    private fun getSelectedItems() = blockedNumbers.filter { selectedKeys.contains(it.id.toInt()) } as ArrayList<BlockedNumber>

    private fun setupView(view: View, blockedNumber: BlockedNumber) {
        // use view binding for the item view
        val binding = ItemManageBlockedNumberBinding.bind(view)
        binding.manageBlockedNumberHolder.isSelected = selectedKeys.contains(blockedNumber.id.toInt())
        binding.manageBlockedNumberTitle.apply {
            text = blockedNumber.number
            setTextColor(textColor)
        }

        binding.overflowMenuIcon.drawable?.apply {
            mutate()
            setTint(activity.getProperTextColor())
        }

        binding.overflowMenuIcon.setOnClickListener {
            showPopupMenu(binding.overflowMenuAnchor, blockedNumber)
        }
    }

    private fun showPopupMenu(view: View, blockedNumber: BlockedNumber) {
        finishActMode()
        val theme = activity.getPopupMenuTheme()
        val contextTheme = ContextThemeWrapper(activity, theme)

        PopupMenu(contextTheme, view, Gravity.END).apply {
            inflate(getActionMenuId())
            setOnMenuItemClickListener { item ->
                val blockedNumberId = blockedNumber.id.toInt()
                when (item.itemId) {
                    R.id.cab_copy_number -> {
                        executeItemMenuOperation(blockedNumberId) {
                            copyNumberToClipboard()
                        }
                    }
                    R.id.cab_delete -> {
                        executeItemMenuOperation(blockedNumberId) {
                            deleteSelection()
                        }
                    }
                }
                true
            }
            show()
        }
    }

    private fun executeItemMenuOperation(blockedNumberId: Int, callback: () -> Unit) {
        selectedKeys.add(blockedNumberId)
        callback()
        selectedKeys.remove(blockedNumberId)
    }

    private fun copyNumberToClipboard() {
        val selectedNumber = getSelectedItems().firstOrNull() ?: return
        activity.copyToClipboard(selectedNumber.number)
        finishActMode()
    }

    private fun deleteSelection() {
        val deleteBlockedNumbers = ArrayList<BlockedNumber>(selectedKeys.size)
        val positions = getSelectedItemPositions()

        getSelectedItems().forEach {
            deleteBlockedNumbers.add(it)
            activity.deleteBlockedNumber(it.number)
        }

        blockedNumbers.removeAll(deleteBlockedNumbers)
        removeSelectedItems(positions)
        if (blockedNumbers.isEmpty()) {
            listener?.refreshItems()
        }
    }
}
