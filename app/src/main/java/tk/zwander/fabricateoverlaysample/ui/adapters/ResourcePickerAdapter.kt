package tk.zwander.fabricateoverlaysample.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.databinding.ItemRegisteredHeaderBinding
import tk.zwander.fabricateoverlaysample.databinding.ItemResourcePickerBinding

sealed class ResourceListItem {
    data class Header(val title: String) : ResourceListItem()
    data class Item(val data: AvailableResourceItemData) : ResourceListItem()
}

class ResourcePickerAdapter(
    private var items: List<ResourceListItem> = listOf(),
    private val onClick: (AvailableResourceItemData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    class HeaderVH(val binding: ItemRegisteredHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    class ItemVH(val binding: ItemResourcePickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ResourceListItem.Header -> TYPE_HEADER
            is ResourceListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                HeaderVH(
                    ItemRegisteredHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> {
                ItemVH(
                    ItemResourcePickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val listItem = items[position]) {
            is ResourceListItem.Header -> (holder as HeaderVH).binding.tvHeader.text = listItem.title
            is ResourceListItem.Item -> {
                val h = holder as ItemVH
                h.binding.tvName.text = listItem.data.resourceName
                h.binding.tvVal.text = listItem.data.values.joinToString(", ")
                h.itemView.setOnClickListener { onClick(listItem.data) }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<ResourceListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
