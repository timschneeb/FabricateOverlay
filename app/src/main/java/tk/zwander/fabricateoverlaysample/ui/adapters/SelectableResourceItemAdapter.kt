package tk.zwander.fabricateoverlaysample.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.databinding.ItemExpandableHeaderBinding
import tk.zwander.fabricateoverlaysample.databinding.ItemResourceSelectableBinding

sealed class ResourceListItem {
    data class Header(val title: String) : ResourceListItem()
    data class Item(val data: AvailableResourceItemData) : ResourceListItem()
}

class SelectableResourceItemAdapter(
    private val onSelectChanged: (AvailableResourceItemData, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Keep the full flattened list (headers + items), and compute a visible subset
    private var fullItems: List<ResourceListItem> = listOf()
    private var visibleItems: MutableList<ResourceListItem> = mutableListOf()
    private val expanded = mutableSetOf<String>()
    private val selected = mutableSetOf<AvailableResourceItemData>()

    // Headers that have children (items), computed in updateFull
    private val headersWithChildren = mutableSetOf<String>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    inner class HeaderVH(val binding: ItemExpandableHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val title = binding.tvHeader.text.toString()
                toggle(title)
            }
        }
    }

    class ItemVH(val binding: ItemResourceSelectableBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (visibleItems[position]) {
            is ResourceListItem.Header -> TYPE_HEADER
            is ResourceListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            // Use expandable header that contains a chevron ImageView (ivChevron).
            HeaderVH(
                ItemExpandableHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            ItemVH(
                ItemResourceSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val listItem = visibleItems[position]) {
            is ResourceListItem.Header -> {
                val h = holder as HeaderVH
                h.binding.tvHeader.text = listItem.title
                val isExpanded = expanded.contains(listItem.title)
                // Only show chevron if this header has children (computed in updateFull)
                val hasChildren = headersWithChildren.contains(listItem.title)
                h.binding.ivChevron.isVisible = hasChildren
                h.binding.ivChevron.rotation = if (isExpanded) 180f else 0f
            }
            is ResourceListItem.Item -> {
                val h = holder as ItemVH
                h.binding.tvName.text = listItem.data.resourceName
                h.binding.tvVal.text = listItem.data.values.joinToString(", ")
                h.binding.root.setOnClickListener {
                    Log.e("ResourcePicker", "Item clicked: ${listItem.data.resourceName}")
                    h.binding.cbSelect.performClick()
                }

                // Avoid triggering recycled listeners: clear listener before programmatic change
                h.binding.cbSelect.setOnCheckedChangeListener(null)

                h.binding.cbSelect.isChecked = selected.contains(listItem.data)
                h.binding.cbSelect.setOnCheckedChangeListener { _, checked ->
                    if (checked) selected.add(listItem.data) else selected.remove(listItem.data)
                    onSelectChanged(listItem.data, checked)
                }

                // Update segmented appearance (ListItemLayout) using section-local info.
                val (sectionPosition, sectionCount) = getSectionInfo(position)
                val listItemLayout = h.binding.listItemLayout
                if (sectionCount > 0 && sectionPosition >= 0) {
                    listItemLayout.updateAppearance(sectionPosition, sectionCount)
                } else {
                    // fallback to whole visible list
                    listItemLayout.updateAppearance(position, visibleItems.size)
                }
            }
        }
    }

    override fun getItemCount(): Int = visibleItems.size

    // Helper: compute section-local position and count for visibleItems list
    private fun getSectionInfo(position: Int): Pair<Int, Int> {
        var headerIndex = -1
        for (i in position downTo 0) {
            if (visibleItems[i] is ResourceListItem.Header) {
                headerIndex = i
                break
            }
        }
        val sectionFirst = if (headerIndex >= 0) headerIndex + 1 else 0
        var sectionCount = 0
        for (i in sectionFirst until visibleItems.size) {
            if (visibleItems[i] is ResourceListItem.Item) sectionCount++ else break
        }
        val sectionPosition = position - sectionFirst
        return Pair(sectionPosition, sectionCount)
    }

    @Suppress("NotifyDataSetChanged")
    fun updateFull(newItems: List<ResourceListItem>) {
        fullItems = newItems
        // Compute which headers actually have children so we can show the chevron only when useful.
        headersWithChildren.clear()
        var currentHeader: String? = null
        var countForHeader = 0
        for (it in fullItems) {
            when (it) {
                is ResourceListItem.Header -> {
                    if (currentHeader != null && countForHeader > 0) headersWithChildren.add(currentHeader)
                    currentHeader = it.title
                    countForHeader = 0
                }
                is ResourceListItem.Item -> {
                    countForHeader++
                }
            }
        }
        if (currentHeader != null && countForHeader > 0) headersWithChildren.add(currentHeader)

        rebuildVisible()
        notifyDataSetChanged()
    }

    private fun rebuildVisible() {
        visibleItems.clear()
        var currentHeader: String? = null
        for (it in fullItems) {
            when (it) {
                is ResourceListItem.Header -> {
                    currentHeader = it.title
                    visibleItems.add(it)
                }
                is ResourceListItem.Item -> {
                    // if there is no header (shouldn't happen) or header is expanded, include the item
                    if (currentHeader == null || expanded.contains(currentHeader)) {
                        visibleItems.add(it)
                    }
                }
            }
        }
    }

    private fun toggle(headerTitle: String) {
        if (expanded.contains(headerTitle)) {
            expanded.remove(headerTitle)
        } else {
            expanded.add(headerTitle)
        }
        rebuildVisible()
        notifyDataSetChanged()
    }

    // Allow external callers (e.g., fragment) to set currently selected items if needed
    fun setSelected(items: Set<AvailableResourceItemData>) {
        selected.clear()
        selected.addAll(items)
        notifyDataSetChanged()
    }

    fun getSelected(): Set<AvailableResourceItemData> = selected.toSet()
}
