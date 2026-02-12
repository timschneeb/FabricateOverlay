package tk.zwander.fabricateoverlaysample.ui.fragments

import android.content.pm.ApplicationInfo
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
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
import tk.zwander.fabricateoverlaysample.data.TriState
import tk.zwander.fabricateoverlaysample.databinding.FragmentResourceSelectionBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.ResourceListItem
import tk.zwander.fabricateoverlaysample.ui.adapters.SelectableResourceItemAdapter
import tk.zwander.fabricateoverlaysample.ui.model.ResourceSelectViewModel
import tk.zwander.fabricateoverlaysample.util.FilterPrefs
import tk.zwander.fabricateoverlaysample.util.MarginItemDecoration
import tk.zwander.fabricateoverlaysample.util.getAppResources
import tk.zwander.fabricateoverlaysample.util.getParcelableArrayListCompat
import tk.zwander.fabricateoverlaysample.util.getParcelableCompat

class ResourceSelectionFragment : SearchableBaseFragment<ResourceSelectViewModel>(ResourceSelectViewModel::class), MainActivity.TitleProvider {
    private lateinit var binding: FragmentResourceSelectionBinding
    private lateinit var adapter: SelectableResourceItemAdapter
    private var allResourcesByType: Map<String, List<AvailableResourceItemData>> = mapOf()
    private val selectedItems = mutableSetOf<AvailableResourceItemData>()

    private var existingEntries: List<FabricatedOverlayEntry> = listOf()
    private var resultPosted = false

    // Post the currently selected entries to the parent fragment once.
    private fun postSelections() {
        if (resultPosted) return
        val toPost = ArrayList(selectedItems.map { FabricatedOverlayEntry(it.name, it.type, 0) })
        val bundle = Bundle().apply {
            putParcelableArrayList(KEY_SELECTED_ENTRIES, toPost)
        }
        parentFragmentManager.setFragmentResult(KEY_RESOURCES_SELECTED, bundle)
        resultPosted = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vm = ViewModelProvider(requireActivity())[ResourceSelectViewModel::class.java]
        binding = FragmentResourceSelectionBinding.inflate(inflater, container, false)

        // Load persisted filters into VM on first creation so initial load applies saved filters.
        if (vm.memberFilter.isEmpty()) {
            val loaded = FilterPrefs.loadFilters(requireContext())
            vm.memberFilter.clear()
            vm.memberFilter.putAll(loaded)
            vm.memberFilterLive.value = vm.memberFilter
        }

        // Create adapter and update ViewModel when headers are toggled so we can persist expansion state
        adapter = SelectableResourceItemAdapter({ item, checked ->
            if (checked) selectedItems.add(item) else selectedItems.remove(item)
        }, onHeaderToggled = { header, isExpanded ->
            if (isExpanded)
                vm.expandedHeaders.add(header)
            else
                vm.expandedHeaders.remove(header)
            // Persist current scroll as well when header toggles
            saveScrollStateToVm()
        })
        // Initialize adapter's selection from fragment state (empty at start).
        adapter.setSelected(selectedItems)

        // Observe filter and search changes and recompute the adapter contents
        vm.memberFilterLive.observe(viewLifecycleOwner) {
            updateAdapterForCurrentFilters(vm)
        }
        vm.searchQueryLive.observe(viewLifecycleOwner) {
            updateAdapterForCurrentFilters(vm)
        }

        updateAdapterForCurrentFilters(vm)

        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = this@ResourceSelectionFragment.adapter
            setHasFixedSize(true)
            addItemDecoration(MarginItemDecoration())
        }

        // If the caller passed ApplicationInfo via arguments, load resources
        val info = requireArguments().getParcelableCompat<ApplicationInfo>("appInfo")

        // Read any existing entries to pre-populate selection
        existingEntries = requireArguments()
            .getParcelableArrayListCompat<FabricatedOverlayEntry>("existing_entries")
            ?: listOf()

        info?.let(::loadResources)
        return binding.root
    }

    // Returns true if the item passes the prefix-based filter in vm
    private fun passesPrefixFilter(item: AvailableResourceItemData, vm: ResourceSelectViewModel): Boolean {
        val filter = vm.memberFilter
        // Treat prefixes marked EXCLUDE as highest priority: if any exclude prefix matches, reject.
        val excluded = filter.filterValues { it == TriState.EXCLUDE }.keys
        if (excluded.any { prefixes -> prefixes.prefixes.any { p -> item.name.startsWith(p, ignoreCase = true) } }) {
            return false
        }

        // If there are any INCLUDE prefixes, only include items that match at least one INCLUDE prefix.
        val included = filter.filterValues { it == TriState.INCLUDE }.keys
        if (included.isNotEmpty()) {
            return included.any { prefixes -> prefixes.prefixes.any { p -> item.name.startsWith(p, ignoreCase = true) } }
        }

        // Otherwise, include by default.
        return true
    }

    private fun applyFilters(items: List<AvailableResourceItemData>, vm: ResourceSelectViewModel): List<AvailableResourceItemData> {
        val q = vm.searchQueryLive.value?.trim()?.lowercase()
        return items.filter { item ->
            if (!passesPrefixFilter(item, vm)) return@filter false
            if (!q.isNullOrBlank()) {
                return@filter item.resourceName.lowercase().startsWith(q)
            }
            true
        }
    }

    // Rebuild adapter contents using the current filters and search query in vm.
    private fun updateAdapterForCurrentFilters(vm: ResourceSelectViewModel) {
        val grouped = linkedMapOf<String, List<AvailableResourceItemData>>()
        allResourcesByType.forEach { (type, list) ->
            val filtered = applyFilters(list, vm)
            if (filtered.isNotEmpty()) grouped[type] = filtered
        }

        val listItems = ArrayList<ResourceListItem>()
        grouped.forEach { (type, list) ->
            listItems.add(ResourceListItem.Header(type))
            list.forEach { listItems.add(ResourceListItem.Item(it)) }
        }

        adapter.updateFull(listItems)
        adapter.setSelected(selectedItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intercept back presses so we can implicitly confirm selections when navigating back.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                postSelections()
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
                        FilterBottomSheetFragment().show(parentFragmentManager, "filters")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadResources(info: ApplicationInfo) {
        binding.progress.isVisible = true
        binding.rv.isVisible = false

        val ctx = requireContext()
        lifecycleScope.launch(Dispatchers.IO) {
            val resources = getAppResources(ctx, ApkFile(info.sourceDir))

            val flat = ArrayList<AvailableResourceItemData>()
            resources.forEach { (_, list) -> list.forEach { flat.add(it) } }

            allResourcesByType = resources

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                binding.progress.isVisible = false
                binding.rv.isVisible = true

                // Pre-select items that were already present in `existingEntries`.
                if (existingEntries.isNotEmpty()) {
                    // existingEntries use FabricatedOverlayEntry.resourceName which is the fully-qualified
                    // resource name in other flows; match against AvailableResourceItemData.name.
                    val toSelect = flat.filter { f -> existingEntries.any { it.resourceName == f.name && it.resourceType == f.type } }
                    selectedItems.clear()
                    selectedItems.addAll(toSelect)
                }

                // Apply current filters/search (ViewModel will update observers when changed)
                val mainVm = ViewModelProvider(requireActivity())[ResourceSelectViewModel::class.java]
                updateAdapterForCurrentFilters(mainVm)

                // Restore expanded headers state from ViewModel
                adapter.setExpandedHeaders(mainVm.expandedHeaders)

                // Restore scroll position if we have one
                mainVm.scrollStateLive.value?.let { (index, offset) ->
                    val lm = binding.rv.layoutManager as? LinearLayoutManager
                    lm?.scrollToPositionWithOffset(index, offset)
                }
            }
        }
    }

    private fun saveScrollStateToVm() {
        val lm = binding.rv.layoutManager as? LinearLayoutManager ?: return
        val first = lm.findFirstVisibleItemPosition()
        val child = binding.rv.getChildAt(0) ?: return
        val offset = child.top - binding.rv.paddingTop
        val vm = ViewModelProvider(requireActivity())[ResourceSelectViewModel::class.java]
        vm.scrollStateLive.value = Pair(first, offset)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Persist scroll state when fragment is being destroyed (config changes are ignored)
        saveScrollStateToVm()
        if (isRemoving && !requireActivity().isChangingConfigurations) {
            postSelections()
        }
    }

    override fun toolbarTitle() = requireContext().getString(R.string.resources_select)

    companion object {
        const val KEY_SELECTED_ENTRIES = "selected_entries"
        const val KEY_RESOURCES_SELECTED = "resources_selected"
    }
}