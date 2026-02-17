package tk.zwander.fabricateoverlaysample.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.FabricatedOverlayWrapper
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.MainActivity
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.databinding.FragmentCurrentOverlaysBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.CurrentOverlayEntriesAdapter
import tk.zwander.fabricateoverlaysample.util.MarginItemDecoration
import tk.zwander.fabricateoverlaysample.util.OverlayDataManager
import tk.zwander.fabricateoverlaysample.util.TypedValueExt
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.getParcelableArrayListCompat
import tk.zwander.fabricateoverlaysample.util.getParcelableCompat
import tk.zwander.fabricateoverlaysample.util.showAlert
import tk.zwander.fabricateoverlaysample.util.showInputAlert
import tk.zwander.fabricateoverlaysample.util.toast
import java.lang.reflect.InvocationTargetException

class CurrentOverlayEntriesFragment : Fragment(), MainActivity.TitleProvider {
    private val entries = mutableListOf<AvailableResourceItemData>()
    private lateinit var appInfo: ApplicationInfo

    // Editing mode support
    private var editingOverlayName: String? = null

    private lateinit var adapter: CurrentOverlayEntriesAdapter
    private lateinit var binding: FragmentCurrentOverlaysBinding

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() { updateEmptyViewState() }
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) { updateEmptyViewState() }
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { updateEmptyViewState() }
    }

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
                bundle.getParcelableArrayListCompat<AvailableResourceItemData>(ResourceSelectionFragment.KEY_SELECTED_ENTRIES)
                    ?: return@setFragmentResultListener

            val resultMap = LinkedHashMap<String, AvailableResourceItemData>()
            // Keep any existing entries (prioritize local edits).
            for (old in entries) {
                resultMap[old.name] = old
            }

            // Append remaining selected entries (in picker order), avoiding duplicates.
            for (s in selected) {
                if (resultMap.containsKey(s.name)) {
                    continue
                }
                resultMap[s.name] = s
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
                it.registerAdapterDataObserver(dataObserver)
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
                        val id = FabricatedOverlayWrapper.generateOverlayIdentifier(
                            fullName,
                            OverlayAPI.servicePackage ?: "com.android.shell"
                        )
                        api.unregisterFabricatedOverlay(id)
                    }
                    catch (e: Exception) {
                        // No existing overlay, nothing to unregister
                        Timber.e(e, "No existing overlay to unregister")
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
                            Timber.d("Using target overlayable $it from existing overlay")
                        }

                    api.registerFabricatedOverlay(
                        FabricatedOverlayWrapper(
                            fullName,
                            appInfo.packageName,
                            OverlayAPI.servicePackage ?: "com.android.shell",
                            targetOverlayable
                        ).apply {
                            this@CurrentOverlayEntriesFragment
                                .entries
                                .forEach { e ->
                                    if (e.values.isEmpty()) {
                                        Timber.w("Skipping resource ${e.name} with no value")
                                        return@forEach
                                    }

                                    entries[e.name] = FabricatedOverlayEntry(
                                        e.name,
                                        e.type,
                                        e.values.first().data ?: 0,
                                        e.values.first().stringData
                                    )
                                }
                        }
                    )

                    try {
                        api.setEnabled(fullName, true, 0)
                    }
                    catch (e: InvocationTargetException) {
                        context?.showAlert(e.targetException)
                        Timber.e(e.targetException, "Failed to enable overlay")
                    }

                    // return to root fragment
                    activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                } catch (e: Exception) {
                    ctx.showAlert(e)
                    Timber.e(e, "Failed to register overlay")
                }
            }
        }
    }

    private fun handleEditEntry(position: Int, entry: AvailableResourceItemData) {
        val ctx = requireContext()
        val simpleName = entry.resourceName.substringAfterLast('/')
        val resolvedValue = entry.values.lastOrNull() ?: return

        when (entry.type) {
            TypedValue.TYPE_INT_BOOLEAN -> {
                // Flip boolean value
                entry.setValue(if (resolvedValue.data != 0) 0 else 1)
                adapter.notifyItemChanged(position)
            }
            TypedValue.TYPE_INT_COLOR_ARGB8,
            TypedValue.TYPE_INT_COLOR_RGB8,
            TypedValue.TYPE_INT_COLOR_ARGB4,
            TypedValue.TYPE_INT_COLOR_RGB4 -> {
                // Accept several hex colour formats: AARRGGBB, RRGGBB, ARGB (4-digit), RGB (3-digit).
                val hex = resolvedValue.displayString()
                ctx.showInputAlert(layoutInflater, simpleName, getString(R.string.edit_color_hint), hex) { input ->
                    // Parse hex input (allow # or 0x prefixes)
                    val cleaned = input.trim().removePrefix("#").removePrefix("0x").lowercase()

                    fun nibbleToByte(c: Char): Int = Integer.parseInt(c.toString(), 16) * 17

                    try {
                        val colorInt = when (cleaned.length) {
                            8 -> cleaned.toLong(16).toInt() // aarrggbb
                            6 -> {
                                // rrggbb -> add full alpha
                                val rgb = cleaned.toLong(16).toInt() and 0x00FFFFFF
                                (0xFF shl 24) or rgb
                            }
                            4 -> {
                                // argb -> each nibble expands to byte (x -> x*17)
                                val a = nibbleToByte(cleaned[0]) and 0xFF
                                val r = nibbleToByte(cleaned[1]) and 0xFF
                                val g = nibbleToByte(cleaned[2]) and 0xFF
                                val b = nibbleToByte(cleaned[3]) and 0xFF
                                (a shl 24) or (r shl 16) or (g shl 8) or b
                            }
                            3 -> {
                                // rgb -> expand to rrggbb and add full alpha
                                val r = nibbleToByte(cleaned[0]) and 0xFF
                                val g = nibbleToByte(cleaned[1]) and 0xFF
                                val b = nibbleToByte(cleaned[2]) and 0xFF
                                (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                            }
                            else -> throw IllegalArgumentException("Invalid color format: $input")
                        }

                        entry.setValue(colorInt)
                        adapter.notifyItemChanged(position)
                    } catch (e: Exception) {
                        ctx.showAlert(e)
                    }
                }
            }
            TypedValue.TYPE_INT_DEC, TypedValue.TYPE_INT_HEX -> {
                // Numeric input
                ctx.showInputAlert(layoutInflater, simpleName,
                    getString(R.string.edit_integer_hint), resolvedValue.data.toString(), true) { input ->
                    val num = input.toIntOrNull()
                    if (num == null) {
                        ctx.toast(getString(R.string.invalid_value))
                    } else {
                        entry.setValue(num)
                        adapter.notifyItemChanged(position)
                    }
                }
            }
            TypedValue.TYPE_DIMENSION -> {
                // Dimension input (e.g., "16dp", "20sp")
                val current = resolvedValue.displayString()
                ctx.showInputAlert(layoutInflater, simpleName, getString(R.string.edit_dimension_hint), current) { input ->
                    // Parse dimension input (allow unit suffix)
                    val regex = Regex("(-?\\d+\\.?\\d*)\\s*(\\w{2,})?")
                    val match = regex.matchEntire(input.trim())
                    if (match != null) {
                        val value = match.groupValues[1].toFloatOrNull()
                        val unit = match.groupValues[2]
                        Timber.d("Parsed dimension input: value=$value, unit=$unit")
                        if (value != null) {
                            val unit = when (unit) {
                                "px" -> TypedValue.COMPLEX_UNIT_PX
                                "dp", "dip" -> TypedValue.COMPLEX_UNIT_DIP
                                "sp" -> TypedValue.COMPLEX_UNIT_SP
                                "pt" -> TypedValue.COMPLEX_UNIT_PT
                                "in" -> TypedValue.COMPLEX_UNIT_IN
                                "mm" -> TypedValue.COMPLEX_UNIT_MM
                                else -> TypedValue.COMPLEX_UNIT_PX // default to pixels/unitless-float
                            }
                            entry.setValue(TypedValueExt.createComplexDimension(value, unit))
                            adapter.notifyItemChanged(position)
                        } else {
                            ctx.toast(getString(R.string.invalid_value))
                        }
                    } else {
                        ctx.toast(getString(R.string.invalid_value))
                    }
                }
            }
            TypedValue.TYPE_FRACTION -> {
                // Accept fraction input like "50%" or "50%p"
                val current = resolvedValue.displayString()
                ctx.showInputAlert(layoutInflater, simpleName, getString(R.string.edit_fraction_hint), current) { input ->
                    // Match number with optional % or %p suffix
                    val regex = Regex("(-?\\d+\\.?\\d*)\\s*(%p|%)?", RegexOption.IGNORE_CASE)
                    val match = regex.matchEntire(input.trim())
                    if (match != null) {
                        val num = match.groupValues[1].toFloatOrNull()
                        if (num != null) {
                            val unit = when (match.groupValues[2]) {
                                "%p" -> TypedValue.COMPLEX_UNIT_FRACTION_PARENT
                                else -> TypedValue.COMPLEX_UNIT_FRACTION
                            }

                            entry.setValue(
                                TypedValueExt.createComplexDimension(num / 100, unit)
                            )
                            adapter.notifyItemChanged(position)
                        } else {
                            ctx.toast(getString(R.string.invalid_value))
                        }
                    } else {
                        ctx.toast(getString(R.string.invalid_value))
                    }
                }
            }
            TypedValue.TYPE_FLOAT -> {
                val current = java.lang.Float.intBitsToFloat(resolvedValue.data ?: 0).toString()
                ctx.showInputAlert(layoutInflater, simpleName, null, current, true) { input ->
                    val num = input.trim().toFloatOrNull()
                    if (num == null) {
                        ctx.toast(getString(R.string.invalid_value))
                    } else {
                        try {
                            entry.setValue(java.lang.Float.floatToIntBits(num))
                            adapter.notifyItemChanged(position)
                        } catch (e: Exception) {
                            ctx.showAlert(e)
                        }
                    }
                }
            }
            TypedValue.TYPE_STRING -> {
                val current = resolvedValue.stringData ?: ""
                ctx.showInputAlert(layoutInflater, simpleName, null, current, false) { input ->
                    entry.setValue(input)
                    adapter.notifyItemChanged(position)
                }
            }
            else -> {
                ctx.toast("Unsupported: ${entry.type}")
            }
        }
    }

    private fun updateEmptyViewState() {
        val isEmpty = entries.isEmpty()
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvEntries.adapter?.unregisterAdapterDataObserver(dataObserver)
        binding.rvEntries.adapter = null
    }

    override fun toolbarTitle() = appInfo.loadLabel(requireContext().packageManager)
}
