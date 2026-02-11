package tk.zwander.fabricateoverlaysample.ui.fragments

import android.annotation.SuppressLint
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen for selections returned from the resource picker
        parentFragmentManager.setFragmentResultListener(ChooseResourcesFragment.KEY_RESOURCES_SELECTED, viewLifecycleOwner) { _, bundle ->
            val selected = bundle.getParcelableArrayList<FabricatedOverlayEntry>(ChooseResourcesFragment.KEY_SELECTED_ENTRIES)
            if (selected != null) {
                // Prepare lookup sets: full names and short names (after '/')
                val selectedByFull = selected.associateBy { it.resourceName }.toMutableMap()
                val selectedShortToFull = selected.mapNotNull { e ->
                    val short = e.resourceName.substringAfterLast('/')
                    short to e
                }.toMap().toMutableMap()

                val newList = mutableListOf<FabricatedOverlayEntry>()

                // Keep entries that match either full name or short name; use the selected entry version
                for (old in entries) {
                    val fullMatch = selectedByFull.remove(old.resourceName)
                    if (fullMatch != null) {
                        newList.add(fullMatch)
                        continue
                    }

                    val oldShort = old.resourceName.substringAfterLast('/')
                    val shortMatch = selectedShortToFull.remove(oldShort)
                    if (shortMatch != null) {
                        newList.add(shortMatch)
                    }
                }

                // Append any remaining newly selected entries
                for ((_, v) in selectedByFull) newList.add(v)
                for ((_, v) in selectedShortToFull) newList.add(v)

                // Replace backing list with the new selection (this removes items the user unselected)
                entries.clear()
                entries.addAll(newList)
                if (::adapter.isInitialized) adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvEntries.adapter = null
    }
}
