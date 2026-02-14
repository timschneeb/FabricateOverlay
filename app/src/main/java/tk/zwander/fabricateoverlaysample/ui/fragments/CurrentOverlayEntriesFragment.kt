package tk.zwander.fabricateoverlaysample.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import tk.zwander.fabricateoverlaysample.util.ApkParser
import tk.zwander.fabricateoverlaysample.util.MarginItemDecoration
import tk.zwander.fabricateoverlaysample.util.OverlayDataManager
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.getParcelableArrayListCompat
import tk.zwander.fabricateoverlaysample.util.getParcelableCompat
import tk.zwander.fabricateoverlaysample.util.showAlert
import tk.zwander.fabricateoverlaysample.util.showInputAlert
import tk.zwander.fabricateoverlaysample.util.toast
import java.lang.reflect.InvocationTargetException

class CurrentOverlayEntriesFragment : Fragment(), MainActivity.TitleProvider {
    private val entries = mutableListOf<FabricatedOverlayEntry>()
    private lateinit var appInfo: ApplicationInfo

    // Editing mode support
    private var editingOverlayName: String? = null

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = requireArguments().getParcelableCompat<ApplicationInfo>("appInfo")!!

        // Check if we're in editing mode
        editingOverlayName = requireArguments().getString("editingOverlayName")

        // If editing, load the backed-up entries
        if (editingOverlayName != null) {
            val backedUpEntries = OverlayDataManager.loadOverlayEntries(
                requireContext(),
                editingOverlayName!!
            )
            if (backedUpEntries != null) {
                entries.addAll(backedUpEntries)
            }
        }

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
            if (::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
                updateEmptyViewState()
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
            adapter = CurrentOverlayEntriesAdapter(appInfo, entries, onEditRequested = { pos, entry ->
                handleEditEntry(pos, entry)
            }).also {
                this@CurrentOverlayEntriesFragment.adapter = it
            }
            addItemDecoration(MarginItemDecoration())
        }

        updateEmptyViewState()

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

            val currentEditing = editingOverlayName
            if (currentEditing != null) {
                saveOverlay(currentEditing)
            } else {
                // New overlay: prompt for a name, then register
                ctx.showInputAlert(layoutInflater, R.string.add_overlay, R.string.overlay_name) { input ->
                    val name = input.filter { char -> (char.isLetterOrDigit() || char == '.' || char == '_') }
                        .replace(Regex("(_+)\\1"), "_")
                        .replace(Regex("(\\.+)\\1"), ".")
                        .replace("_.", "_")
                        .replace("._", ".")

                    saveOverlay("${ctx.packageName}.${appInfo.packageName}.$name")
                }
            }
        }

        return binding.root
    }

    private fun saveOverlay(fullName: String) {
        val ctx = context ?: return
        ctx.ensureHasOverlayPermission {
            OverlayAPI.getInstance(ctx) { api ->
                try {
                    // Save backup to SharedPreferences
                    OverlayDataManager.saveOverlayEntries(ctx, fullName, entries.toList())

                    try {
                        // Attempt to get existing overlay, if it doesn't exist we'll get an exception
                        val id = api.getOverlayInfoByIdentifier(
                            FabricatedOverlay.generateOverlayIdentifier(
                                fullName,
                                OverlayAPI.servicePackage ?: "com.android.shell"
                            ),
                            0
                        )
                        api.unregisterFabricatedOverlay(id)
                    }
                    catch (e: Exception) {
                        // No existing overlay, nothing to unregister
                        Log.e("CurrentOverlayEntriesFragment", "No existing overlay to unregister", e)
                    }

                    val realPackage = try {
                        ApkParser(ctx, appInfo.packageName).realPackage?.also {
                            Log.d("CurrentOverlayEntriesFragment", "Using real package name $it")
                        }
                    } catch (e: Exception) {
                        Log.e("CurrentOverlayEntriesFragment", "Failed to parse APK for real package name, falling back to manifest package name", e)
                        ctx.toast("Exception while parsing APK: ${e.message}")
                    }

                    val childOverlays =
                        api.getAllOverlays(0)
                            .filter { it.key == appInfo.packageName }
                            .values
                            .firstOrNull()

                    /*
                     * Necessary for some apps like com.google.android.permissioncontroller.
                     * It doesn't define any <overlayable> itself, but it has another overlay with
                     * greater priority that declares a non-existant overlayableTargetName.
                     *
                     * TODO: We do not take actual <overlayable> definitions into account at the moment.
                     */
                    val targetOverlayable = childOverlays
                        ?.firstOrNull { it.targetOverlayableName != null && it.isEnabled }
                        ?.targetOverlayableName
                        ?.also {
                            Log.d("FabricateOverlay", "Using target overlayable $it from existing overlay")
                        }

                    api.registerFabricatedOverlay(
                        FabricatedOverlay(
                            fullName,
                            appInfo.packageName,
                            OverlayAPI.servicePackage ?: "com.android.shell",
                            targetOverlayable
                        ).apply {
                            this@CurrentOverlayEntriesFragment
                                .entries
                                .forEach { e ->
                                    if (realPackage != null && !e.resourceName.startsWith("$realPackage:")) {
                                        e.resourceName = e.resourceName.replaceFirst("${appInfo.packageName}:", "$realPackage:")
                                    }
                                    entries[e.resourceName] = e
                                }
                        }
                    )

                    try {
                        api.setEnabled(fullName, true, 0)
                    }
                    catch (e: InvocationTargetException) {
                        context?.showAlert(e.targetException)
                        Log.e("CurrentOverlayEntriesFragment", "Failed to enable overlay", e.targetException)
                    }

                    // return to root fragment
                    activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                } catch (e: Exception) {
                    ctx.showAlert(e)
                }
            }
        }
    }

    private fun handleEditEntry(position: Int, entry: FabricatedOverlayEntry) {
        val ctx = requireContext()
        val simpleName = entry.resourceName.substringAfterLast('/')
        when (entry.resourceType) {
            TypedValue.TYPE_INT_BOOLEAN -> {
                // Flip boolean value
                entry.resourceValue = if (entry.resourceValue != 0) 0 else 1
                adapter.notifyItemChanged(position)
                updateEmptyViewState()
            }
            TypedValue.TYPE_INT_COLOR_ARGB8 -> {
                // Color as hex string
                val hex = String.format("#%08X", entry.resourceValue)
                ctx.showInputAlert(layoutInflater, simpleName, getString(R.string.edit_color_hint), hex) { input ->
                    // Parse hex input (allow # prefix)
                    val cleaned = input.trim().removePrefix("#")
                    try {
                        val value = cleaned.toLong(16).toInt()
                        entry.resourceValue = value
                        adapter.notifyItemChanged(position)
                        updateEmptyViewState()
                    } catch (e: Exception) {
                        ctx.showAlert(e)
                    }
                }
            }
            TypedValue.TYPE_INT_DEC, TypedValue.TYPE_DIMENSION -> {
                // Numeric input
                ctx.showInputAlert(layoutInflater, simpleName, null, entry.resourceValue.toString(), true) { input ->
                    val num = input.toIntOrNull()
                    if (num == null) {
                        ctx.toast(getString(R.string.invalid_value))
                    } else {
                        entry.resourceValue = num
                        adapter.notifyItemChanged(position)
                        updateEmptyViewState()
                    }
                }
            }
            else -> {
                // Fallback: open simple text input to accept integer
                // TODO
                ctx.showInputAlert(layoutInflater, simpleName, null, entry.resourceValue.toString(), true) { input ->
                    val num = input.toIntOrNull()
                    if (num == null) {
                        ctx.toast(getString(R.string.invalid_value))
                    } else {
                        entry.resourceValue = num
                        adapter.notifyItemChanged(position)
                        updateEmptyViewState()
                    }
                }
            }
        }
    }

    private fun updateEmptyViewState() {
        val isEmpty = entries.isEmpty()
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvEntries.adapter = null
    }

    override fun toolbarTitle() = appInfo.loadLabel(requireContext().packageManager)
}
