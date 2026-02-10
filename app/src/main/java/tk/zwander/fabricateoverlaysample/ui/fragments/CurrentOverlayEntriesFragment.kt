package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.ui.adapters.CurrentOverlayEntriesAdapter
import tk.zwander.fabricateoverlaysample.ui.elements.dialogs.ListAvailableResourcesDialogFragment
import tk.zwander.fabricateoverlaysample.ui.elements.dialogs.SaveOverlayDialogFragment
import tk.zwander.fabricateoverlaysample.databinding.FragmentCurrentOverlaysBinding

class CurrentOverlayEntriesFragment : Fragment() {
    private val entries = mutableListOf<FabricatedOverlayEntry>()
    private var appInfo: android.content.pm.ApplicationInfo? = null

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = arguments?.getParcelable("appInfo")

        parentFragmentManager.setFragmentResultListener("list_resource_added", this) { _, bundle ->
            val entry = bundle.getParcelable("entry") as? FabricatedOverlayEntry
            if (entry != null) {
                entries.add(entry)
                adapter.notifyItemInserted(entries.size - 1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentOverlaysBinding.inflate(inflater, container, false)

        binding.rvEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CurrentOverlayEntriesAdapter(entries).also {
                this@CurrentOverlayEntriesFragment.adapter = it
            }
        }

        binding.btnAdd.setOnClickListener {
            val frag = ListAvailableResourcesDialogFragment()
            val args = Bundle()
            args.putParcelable("appInfo", appInfo)
            frag.arguments = args
            frag.show(parentFragmentManager, "list_resources")
        }

        binding.btnSave.setOnClickListener {
            val frag = SaveOverlayDialogFragment()
            val args = Bundle()
            args.putParcelable("appInfo", appInfo)
            val parcelList = ArrayList<android.os.Parcelable>()
            entries.forEach { parcelList.add(it) }
            args.putParcelableArrayList("entries", parcelList)
            frag.arguments = args
            frag.show(parentFragmentManager, "save_overlay")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvEntries.adapter = null
    }
}
