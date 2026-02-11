package tk.zwander.fabricateoverlaysample.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.listitem.ListItemViewHolder
import com.google.android.material.listitem.ListItemLayout
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.BuildConfig
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.ItemRegisteredHeaderBinding
import tk.zwander.fabricateoverlaysample.databinding.ItemRegisteredOverlayBinding
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showConfirmDialog

sealed class RegisteredListItem {
    data class Header(val title: String) : RegisteredListItem()
    data class Overlay(val info: OverlayInfo) : RegisteredListItem()
}

class RegisteredOverlaySectionAdapter(
    private var items: MutableList<RegisteredListItem>,
    private val onRemoved: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_OVERLAY = 1
    }

    class HeaderVH(val binding: ItemRegisteredHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView = binding.tvHeader
    }

    class OverlayVH(val binding: ItemRegisteredOverlayBinding) : ListItemViewHolder(binding.root) {
        val ivDelete: MaterialButton = binding.ivDelete
        val tvName: TextView = binding.tvName
        val cbEnabled: CheckBox = binding.cbEnabled
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is RegisteredListItem.Header -> TYPE_HEADER
            is RegisteredListItem.Overlay -> TYPE_OVERLAY
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
                OverlayVH(
                    ItemRegisteredOverlayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is RegisteredListItem.Header -> {
                (holder as HeaderVH).tvTitle.text = item.title
            }
            is RegisteredListItem.Overlay -> {
                val info = item.info
                val h = holder as OverlayVH

                // Compute section-local position/count and bind/update appearance
                val (sectionPosition, sectionCount) = getSectionInfo(position)

                if (sectionCount > 0 && sectionPosition >= 0) {
                    h.bind(sectionPosition, sectionCount)
                } else {
                    // fallback to bind to full list when section calculation failed
                    h.bind(position, items.size)
                }

                // Only display actual name
                h.tvName.text = info.overlayName
                    ?.replace(BuildConfig.APPLICATION_ID, "")
                    ?.replace(info.targetPackageName, "")
                    ?.trimStart('.')
                h.cbEnabled.isChecked = info.isEnabled

                // Item click toggles enabled state
                h.itemView.setOnClickListener {
                    h.itemView.context.ensureHasOverlayPermission {
                        OverlayAPI.getInstance(h.itemView.context) { api ->
                            val overlayId = FabricatedOverlay.generateOverlayIdentifier(
                                info.overlayName,
                                info.packageName
                            )

                            api.setEnabled(overlayId, !info.isEnabled, 0)

                            items[position] = RegisteredListItem.Overlay(
                                api.getOverlayInfoByIdentifier(overlayId, 0)
                            )
                            notifyItemChanged(position)
                        }
                    }
                }

                // Make checkbox clicks also trigger the item click handler for consistent behavior
                h.cbEnabled.setOnClickListener {
                    h.itemView.performClick()
                }

                h.ivDelete.setOnClickListener {
                    val ctx = h.itemView.context
                    ctx.ensureHasOverlayPermission {
                        ctx.showConfirmDialog(
                            R.string.delete_overlay,
                            R.string.delete_confirmation
                        ) {
                            OverlayAPI.getInstance(ctx) { api ->
                                api.unregisterFabricatedOverlay(
                                    FabricatedOverlay.generateOverlayIdentifier(
                                        info.overlayName,
                                        OverlayAPI.servicePackage ?: "com.android.shell"
                                    )
                                )
                                onRemoved()
                            }
                        }
                    }
                }

                // Ensure ListItemLayout appearance is updated for this position (if present)
                val listItemLayout = h.binding.root.findViewById<ListItemLayout?>(R.id.list_item_layout)
                if (sectionCount > 0 && sectionPosition >= 0) {
                    listItemLayout?.updateAppearance(sectionPosition, sectionCount)
                } else {
                    listItemLayout?.updateAppearance(position, items.size)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<RegisteredListItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    // Helper that returns a Pair(sectionPosition, sectionCount) for the given adapter position.
    // sectionPosition is the 0-based index within the section (first overlay after a header is 0).
    private fun getSectionInfo(position: Int): Pair<Int, Int> {
        var headerIndex = -1
        for (i in position downTo 0) {
            if (items[i] is RegisteredListItem.Header) {
                headerIndex = i
                break
            }
        }
        val sectionFirst = if (headerIndex >= 0) headerIndex + 1 else 0
        var sectionCount = 0
        for (i in sectionFirst until items.size) {
            if (items[i] is RegisteredListItem.Overlay) sectionCount++ else break
        }
        val sectionPosition = position - sectionFirst

        return Pair(sectionPosition, sectionCount)
    }
}
