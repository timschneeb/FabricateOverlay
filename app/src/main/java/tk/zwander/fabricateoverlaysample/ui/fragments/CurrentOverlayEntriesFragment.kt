package tk.zwander.fabricateoverlaysample.ui.fragments

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.MainActivity
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.FragmentCurrentOverlaysBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.CurrentOverlayEntriesAdapter
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showInputAlert

class CurrentOverlayEntriesFragment : Fragment() {
    private val entries = mutableListOf<FabricatedOverlayEntry>()
    private lateinit var appInfo: ApplicationInfo

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Defer fragment result listeners until the view exists (registered in onViewCreated).
        appInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("appInfo", ApplicationInfo::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable("appInfo")!!
        }
        // Register result listeners on the fragment lifecycle so they are active while this fragment
        // exists in the FragmentManager (even when its view is destroyed while a child is on top).
        parentFragmentManager.setFragmentResultListener(ChooseResourcesFragment.KEY_RESOURCES_SELECTED, this) { _, bundle ->
            val selected = bundle.getParcelableArrayList<FabricatedOverlayEntry>(ChooseResourcesFragment.KEY_SELECTED_ENTRIES)
            android.util.Log.d("CurrentOverlayEntries", "Received fragment result: selected_entries size=${selected?.size}")
            if (selected != null) {
                // Build quick lookup maps for full and short names
                val selectedByFull = selected.associateBy { it.resourceName }
                val selectedByShort = selected.associateBy { it.resourceName.substringAfterLast('/') }

                // Use LinkedHashMap to preserve insertion order: first keep existing selected entries
                // in their original order (if they remain selected), then append any remaining selected
                // entries in the order the picker returned them. Keys are the full resourceName.
                val resultMap = LinkedHashMap<String, FabricatedOverlayEntry>()

                // Preserve original order for entries that remain selected (match by full or short name)
                for (old in entries) {
                    val fullMatch = selectedByFull[old.resourceName]
                    if (fullMatch != null) {
                        resultMap[fullMatch.resourceName] = fullMatch
                        continue
                    }

                    val short = old.resourceName.substringAfterLast('/')
                    val shortMatch = selectedByShort[short]
                    if (shortMatch != null) {
                        resultMap[shortMatch.resourceName] = shortMatch
                    }
                }

                // Append remaining selected entries (in picker order), avoiding duplicates
                for (s in selected) {
                    if (!resultMap.containsKey(s.resourceName)) {
                        resultMap[s.resourceName] = s
                    }
                }

                // Replace backing list with deduplicated selection
                entries.clear()
                entries.addAll(resultMap.values)
                if (::adapter.isInitialized) adapter.notifyDataSetChanged()
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
            adapter = CurrentOverlayEntriesAdapter(appInfo, entries).also {
                this@CurrentOverlayEntriesFragment.adapter = it
            }
        }

        binding.btnAdd.setOnClickListener {
            val current = if(entries.isEmpty()) null else ArrayList(entries)
            (activity as? MainActivity)?.navigateToResourcePicker(appInfo, current)
        }

        binding.btnSave.setOnClickListener {
            val ctx = requireContext()

            ctx.showInputAlert(layoutInflater, R.string.add_overlay, R.string.overlay_name) { input ->
                val name = input.filter { char -> (char.isLetterOrDigit() || char == '.' || char == '_') }
                    .replace(Regex("(_+)\\1"), "_")
                    .replace(Regex("(\\.+)\\1"), ".")
                    .replace("_.", "_")
                    .replace("._", ".")

                val fullName = "${ctx.packageName}.${appInfo.packageName}.$name"

                ctx.ensureHasOverlayPermission {
                    OverlayAPI.getInstance(ctx) { api ->
                        api.registerFabricatedOverlay(
                            FabricatedOverlay(
                                fullName,
                                appInfo.packageName,
                                OverlayAPI.servicePackage ?: "com.android.shell"
                            ).apply {
                                entries.values.forEach { e ->
                                    entries[e.resourceName] = e
                                }
                            }
                        )

                        // pop back to main fragment and set title
                        activity?.supportFragmentManager?.popBackStackImmediate(null, 0)
                        activity?.title = ctx.getString(R.string.overlays)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvEntries.adapter = null
    }
}
