package tk.zwander.fabricateoverlaysample.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.listitem.ListItemViewHolder
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.BuildConfig
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.ItemSimpleHeaderBinding
import tk.zwander.fabricateoverlaysample.databinding.ItemRegisteredOverlayBinding
import tk.zwander.fabricateoverlaysample.util.OverlayDataManager
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showConfirmDialog

sealed class RegisteredListItem {
    data class Header(val title: String) : RegisteredListItem()
    data class Overlay(val info: OverlayInfo) : RegisteredListItem()
}

class RegisteredOverlaySectionAdapter(
    private var items: MutableList<RegisteredListItem>,
    private val onRemoved: () -> Unit,
    private val onEditClicked: ((OverlayInfo) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_OVERLAY = 1
    }

    class HeaderVH(val binding: ItemSimpleHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView = binding.tvHeader
    }

    class OverlayVH(val binding: ItemRegisteredOverlayBinding) : ListItemViewHolder(binding.root) {
        val ivEdit: MaterialButton = binding.ivEdit
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
                    ItemSimpleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

                // Show edit button only if backup exists
                val ctx = h.itemView.context
                val hasBackup = info.overlayName?.let {
                    OverlayDataManager.hasBackup(ctx, it)
                } ?: false
                h.ivEdit.visibility = if (hasBackup) View.VISIBLE else View.GONE

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

                // Handle edit button click
                h.ivEdit.setOnClickListener {
                    onEditClicked?.invoke(info)
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
                                // Also delete the backup
                                info.overlayName?.let {
                                    OverlayDataManager.deleteBackup(ctx, it)
                                }
                                onRemoved()
                            }
                        }
                    }
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
