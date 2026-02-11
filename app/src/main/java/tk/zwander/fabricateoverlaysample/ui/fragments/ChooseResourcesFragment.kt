package tk.zwander.fabricateoverlaysample.ui.fragments

import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dongliu.apk.parser.ApkFile
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.MainActivity
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.databinding.FragmentResourceSelectionBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.ResourceListItem
import tk.zwander.fabricateoverlaysample.ui.adapters.SelectableResourceItemAdapter
import tk.zwander.fabricateoverlaysample.util.MarginItemDecoration
import tk.zwander.fabricateoverlaysample.util.getAppResources

class ChooseResourcesFragment : Fragment(), MainActivity.Searchable {
    private lateinit var binding: FragmentResourceSelectionBinding
    private lateinit var adapter: SelectableResourceItemAdapter
    private var allResourcesByType: Map<String, List<AvailableResourceItemData>> = mapOf()
    private val selectedItems = mutableSetOf<AvailableResourceItemData>()

    private var existingEntries: List<FabricatedOverlayEntry> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResourceSelectionBinding.inflate(inflater, container, false)

        adapter = SelectableResourceItemAdapter { item, checked ->
            if (checked) selectedItems.add(item) else selectedItems.remove(item)
        }
        // Initialize adapter's selection from fragment state (empty at start).
        adapter.setSelected(selectedItems)

        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = this@ChooseResourcesFragment.adapter
            setHasFixedSize(true)
            addItemDecoration(MarginItemDecoration())
        }

        // If the caller passed ApplicationInfo via arguments, load resources
        val info: ApplicationInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("appInfo", ApplicationInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable("appInfo") as? ApplicationInfo
        }

        // Read any existing entries to pre-populate selection
        @Suppress("UNCHECKED_CAST")
        existingEntries = (requireArguments().getParcelableArrayList<FabricatedOverlayEntry>("existing_entries") as? ArrayList<FabricatedOverlayEntry>)
            ?: listOf()

        info?.let { loadResources(it) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intercept back presses so we can implicitly confirm selections when navigating back.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bundle = Bundle().apply {
                    putParcelableArrayList(
                        KEY_SELECTED_ENTRIES,
                        ArrayList(selectedItems.map { FabricatedOverlayEntry(it.name, it.type, 0) })
                    )
                }
                parentFragmentManager.setFragmentResult(KEY_RESOURCES_SELECTED, bundle)
                parentFragmentManager.popBackStack()
            }
        })

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_resource_picker, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        // TODO
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadResources(info: ApplicationInfo) {
        binding.progress.isVisible = true
        val ctx = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            val resources = getAppResources(ctx, ApkFile(info.sourceDir))

            val flat = ArrayList<AvailableResourceItemData>()
            resources.forEach { (_, list) -> list.forEach { flat.add(it) } }

            allResourcesByType = resources

            val listItems = ArrayList<ResourceListItem>()
            resources.forEach { (type, list) ->
                listItems.add(ResourceListItem.Header(type))
                list.forEach { item -> listItems.add(ResourceListItem.Item(item)) }
            }

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                binding.progress.isVisible = false
                adapter.updateFull(listItems)
                // Pre-select items that were already present in `existingEntries`.
                if (existingEntries.isNotEmpty()) {
                    // existingEntries use FabricatedOverlayEntry.resourceName which is the fully-qualified
                    // resource name in other flows; match against AvailableResourceItemData.name.
                    val toSelect = flat.filter { f -> existingEntries.any { it.resourceName == f.name && it.resourceType == f.type } }
                    selectedItems.clear()
                    selectedItems.addAll(toSelect)
                    adapter.setSelected(selectedItems)
                } else {
                    // Make sure adapter reflects any currently selected items (none by default)
                    adapter.setSelected(selectedItems)
                }
            }
        }
    }

    /**
     * Filter the list by query. Host should call this from the ActionBar SearchView.
     */
    fun filter(query: String) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            // show all
            val listItems = ArrayList<ResourceListItem>()
            allResourcesByType.forEach { (type, list) ->
                listItems.add(ResourceListItem.Header(type))
                list.forEach { listItems.add(ResourceListItem.Item(it)) }
            }
            adapter.updateFull(listItems)
            adapter.setSelected(selectedItems)
            return
        }

        val filtered = ArrayList<ResourceListItem>()
        allResourcesByType.forEach { (type, list) ->
            val matches = list.filter { it.resourceName.lowercase().contains(q) }
            if (matches.isNotEmpty()) {
                filtered.add(ResourceListItem.Header(type))
                matches.forEach { filtered.add(ResourceListItem.Item(it)) }
            }
        }
        adapter.updateFull(filtered)
        adapter.setSelected(selectedItems)
    }

    // Called by MainActivity when the action bar SearchView emits text
    override fun onSearchQuery(q: String) {
        filter(q)
    }

    companion object {
        const val KEY_SELECTED_ENTRIES = "selected_entries"
        const val KEY_RESOURCES_SELECTED = "resources_selected"
    }
}