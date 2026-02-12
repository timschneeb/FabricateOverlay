package tk.zwander.fabricateoverlaysample.ui.adapters

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.listitem.ListItemViewHolder
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.databinding.ItemCurrentOverlayEntryBinding

class CurrentOverlayEntriesAdapter(
    private val appInfo: ApplicationInfo,
    private var items: MutableList<FabricatedOverlayEntry>,
    private val onEditRequested: ((position: Int, entry: FabricatedOverlayEntry) -> Unit)? = null
) : RecyclerView.Adapter<CurrentOverlayEntriesAdapter.VH>() {

    class VH(val binding: ItemCurrentOverlayEntryBinding) : ListItemViewHolder(binding.root) {
        val ivDelete: MaterialButton = binding.ivDelete
        val tvName: TextView = binding.tvName
        val tvValue: TextView = binding.tvValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCurrentOverlayEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        // Bind material ListItemViewHolder to enable segmented appearance handling
        holder.bind(position, items.size)

        holder.tvName.text = item.resourceName
            .replace(appInfo.packageName, "")
            .trim(':')
        holder.tvValue.text = item.resourceValue.toString()
        holder.ivDelete.setOnClickListener {
            val idx = items.indexOf(item)
            if (idx >= 0) {
                items.removeAt(idx)
                notifyItemRemoved(idx)
                // update appearance of surrounding items to ensure correct segmented corners
                notifyItemRangeChanged(0, items.size)
            }
        }

        // Tap the item to edit its value (fragment will handle input UI)
        holder.binding.root.setOnClickListener {
            val idx = holder.bindingAdapterPosition
            if (idx != RecyclerView.NO_POSITION) {
                onEditRequested?.invoke(idx, items[idx])
            }
        }

        // Ensure ListItemLayout appearance is updated for this position (if present)
        holder.binding.listItemLayout.updateAppearance(position, items.size)
    }

    override fun getItemCount(): Int = items.size
}
