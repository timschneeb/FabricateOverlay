package tk.zwander.fabricateoverlaysample.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tk.zwander.fabricateoverlaysample.data.LoadedApplicationInfo
import tk.zwander.fabricateoverlaysample.databinding.ItemAppBinding

class AppListAdapter(
    private var items: List<LoadedApplicationInfo>,
    private val onClick: (LoadedApplicationInfo) -> Unit
) : RecyclerView.Adapter<AppListAdapter.VH>() {

    class VH(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.ivIcon.setImageDrawable(item.icon)
        holder.binding.tvLabel.text = item.label
        holder.binding.tvPackage.text = item.info.packageName
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<LoadedApplicationInfo>) {
        items = newItems
        notifyDataSetChanged()
    }
}
