package tk.zwander.fabricateoverlaysample.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.MainActivity
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.FragmentCurrentOverlaysBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.CurrentOverlayEntriesAdapter
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.getParcelableArrayListCompat
import tk.zwander.fabricateoverlaysample.util.getParcelableCompat
import tk.zwander.fabricateoverlaysample.util.showAlert
import tk.zwander.fabricateoverlaysample.util.showInputAlert

class CurrentOverlayEntriesFragment : Fragment(), MainActivity.TitleProvider {
    private val entries = mutableListOf<FabricatedOverlayEntry>()
    private lateinit var appInfo: ApplicationInfo

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = requireArguments().getParcelableCompat<ApplicationInfo>("appInfo")!!

        parentFragmentManager.setFragmentResultListener(ResourceSelectionFragment.KEY_RESOURCES_SELECTED, this) { _, bundle ->
            val selected =
                bundle.getParcelableArrayListCompat<FabricatedOverlayEntry>(ResourceSelectionFragment.KEY_SELECTED_ENTRIES)
                    ?: return@setFragmentResultListener

            val resultMap = LinkedHashMap<String, FabricatedOverlayEntry>()
            for (old in entries) {
                val fullMatch = selected.associateBy { it.resourceName }[old.resourceName]
                if (fullMatch != null) {
                    resultMap[fullMatch.resourceName] = fullMatch
                    continue
                }

                val short = old.resourceName.substringAfterLast('/')
                val shortMatch = selected.associateBy { it.resourceName.substringAfterLast('/') }[short]
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
            if (::adapter.isInitialized)
                adapter.notifyDataSetChanged()
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

            if (entries.isEmpty()) {
                ctx.showAlert(R.string.overlay_no_entries, R.string.overlay_no_entries_summary)
                return@setOnClickListener
            }

            ctx.showInputAlert(layoutInflater, R.string.add_overlay, R.string.overlay_name) { input ->
                val name = input.filter { char -> (char.isLetterOrDigit() || char == '.' || char == '_') }
                    .replace(Regex("(_+)\\1"), "_")
                    .replace(Regex("(\\.+)\\1"), ".")
                    .replace("_.", "_")
                    .replace("._", ".")

                val fullName = "${ctx.packageName}.${appInfo.packageName}.$name"

                ctx.ensureHasOverlayPermission {
                    OverlayAPI.getInstance(ctx) { api ->
                        try {
                            api.registerFabricatedOverlay(
                                FabricatedOverlay(
                                    fullName,
                                    appInfo.packageName,
                                    OverlayAPI.servicePackage ?: "com.android.shell"
                                ).apply {
                                    this@CurrentOverlayEntriesFragment.entries.forEach { e ->
                                        entries[e.resourceName] = e
                                    }
                                }
                            )

                            // return to root fragment
                            activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                        catch (e: Exception) {
                            ctx.showAlert(e)
                        }
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

    override fun toolbarTitle() = appInfo.loadLabel(requireContext().packageManager)
}
