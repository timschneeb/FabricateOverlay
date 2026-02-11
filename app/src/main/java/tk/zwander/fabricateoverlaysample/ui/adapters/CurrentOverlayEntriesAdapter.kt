package tk.zwander.fabricateoverlaysample.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.listitem.ListItemViewHolder
import com.google.android.material.listitem.ListItemLayout
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.ItemCurrentOverlayEntryBinding

class CurrentOverlayEntriesAdapter(
    private var items: MutableList<FabricatedOverlayEntry>
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

        // Ensure ListItemLayout appearance is updated for this position (if present)
        val listItemLayout = holder.binding.root.findViewById<ListItemLayout?>(R.id.list_item_layout)
        listItemLayout?.updateAppearance(position, items.size)
    }

    override fun getItemCount(): Int = items.size
}
