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
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.adapters.CurrentOverlayEntriesAdapter
import tk.zwander.fabricateoverlaysample.ui.fragments.ListAvailableResourcesDialogFragment
import tk.zwander.fabricateoverlaysample.databinding.FragmentCurrentOverlaysBinding
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showInputAlert
import kotlin.collections.forEach
import kotlin.collections.set

class CurrentOverlayEntriesFragment : Fragment() {
    private val entries = mutableListOf<FabricatedOverlayEntry>()
    private lateinit var appInfo: ApplicationInfo

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = requireArguments().getParcelable("appInfo")!!

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
