package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dongliu.apk.parser.ApkFile
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.ui.adapters.ResourceListItem
import tk.zwander.fabricateoverlaysample.ui.adapters.ResourcePickerAdapter
import tk.zwander.fabricateoverlaysample.util.getAppResources
import tk.zwander.fabricateoverlaysample.databinding.DialogResourcePickerBinding

class ListAvailableResourcesDialogFragment : DialogFragment() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var binding: DialogResourcePickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val info = requireArguments().getParcelable<android.content.pm.ApplicationInfo>("appInfo")!!
        val ctx = requireContext()

        binding = DialogResourcePickerBinding.inflate(layoutInflater)

        val adapter = ResourcePickerAdapter(emptyList()) { item ->
            // TODO: prompt for value if not boolean/number/color/etc... also put that into a helper file so we can reuse it for editing entries in the CurrentOverlayEntriesFragment
            val defaultVal = when (item.type) {
                android.util.TypedValue.TYPE_INT_BOOLEAN -> if (item.values.firstOrNull() == "true") 1 else 0
                android.util.TypedValue.TYPE_INT_COLOR_ARGB8,
                android.util.TypedValue.TYPE_INT_DEC,
                android.util.TypedValue.TYPE_DIMENSION -> item.values.firstOrNull()?.toIntOrNull() ?: 0
                else -> 0
            }
            parentFragmentManager.setFragmentResult("list_resource_added", Bundle().apply {
                putParcelable("entry", FabricatedOverlayEntry(item.resourceName, item.type, defaultVal))
            })
            dismiss()
        }

        binding.rv.apply {
            layoutManager = LinearLayoutManager(ctx)
            this.adapter = adapter
            setHasFixedSize(true)
        }

        val dialog = MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.resources)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.show()

        // load resources off main thread
        binding.progress.visibility = View.VISIBLE
        scope.launch(Dispatchers.IO) {
            val resources = getAppResources(ctx, ApkFile(info.sourceDir))

            val flat = ArrayList<AvailableResourceItemData>()
            resources.forEach { (_, list) -> list.forEach { flat.add(it) } }

            val listItems = ArrayList<ResourceListItem>()
            resources.forEach { (type, list) ->
                listItems.add(ResourceListItem.Header(type))
                list.forEach { item -> listItems.add(ResourceListItem.Item(item)) }
            }

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                binding.progress.visibility = View.GONE
                adapter.update(listItems)
            }
        }

        // simple search/filtering
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString()?.trim()?.lowercase() ?: ""
                if (q.isEmpty()) {
                    // reload full list by triggering a reload
                    // we'll simply re-run the loading block synchronously on the main thread using cached list
                    // (for simplicity, call the IO loader again)
                    scope.launch(Dispatchers.IO) {
                        val resources = getAppResources(ctx, ApkFile(info.sourceDir))
                        val listItems = ArrayList<ResourceListItem>()
                        resources.forEach { (type, list) ->
                            listItems.add(ResourceListItem.Header(type))
                            list.forEach { item -> listItems.add(ResourceListItem.Item(item)) }
                        }
                        withContext(Dispatchers.Main) { if (isAdded) adapter.update(listItems) }
                    }
                } else {
                    scope.launch(Dispatchers.IO) {
                        val resources = getAppResources(ctx, ApkFile(info.sourceDir))
                        val filtered = ArrayList<ResourceListItem>()
                        resources.forEach { (type, list) ->
                            val matches = list.filter { it.resourceName.lowercase().contains(q) }
                            if (matches.isNotEmpty()) {
                                filtered.add(ResourceListItem.Header(type))
                                matches.forEach { filtered.add(ResourceListItem.Item(it)) }
                            }
                        }
                        withContext(Dispatchers.Main) { if (isAdded) adapter.update(filtered) }
                    }
                }
            }
        })

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
