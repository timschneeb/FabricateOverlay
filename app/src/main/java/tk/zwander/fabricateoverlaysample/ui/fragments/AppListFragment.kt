package tk.zwander.fabricateoverlaysample.ui.fragments

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlaysample.MainActivity
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.LoadedApplicationInfo
import tk.zwander.fabricateoverlaysample.databinding.FragmentAppListBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.AppListAdapter
import tk.zwander.fabricateoverlaysample.ui.model.AppListViewModel

class AppListFragment : SearchableBaseFragment<AppListViewModel>(AppListViewModel::class), MainActivity.TitleProvider {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var allApps: List<LoadedApplicationInfo> = listOf()
    private lateinit var adapter: AppListAdapter
    private lateinit var binding: FragmentAppListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppListBinding.inflate(inflater, container, false)

        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AppListAdapter(emptyList()) { loaded ->
                // Navigate to current overlays via activity helper
                (activity as? MainActivity)?.navigateToCurrentOverlays(loaded.info)
            }.also {
                this@AppListFragment.adapter = it
            }
        }

        // If we already loaded apps in this fragment instance, don't show the spinner
        if (allApps.isNotEmpty()) {
            binding.progressApps.visibility = View.GONE
            updateList()
        } else {
            binding.progressApps.visibility = View.VISIBLE
        }

        val vm = ViewModelProvider(requireActivity())[AppListViewModel::class.java]
        vm.searchQueryLive.observe(viewLifecycleOwner) {
            updateList()
        }

        // Add menu provider so we can toggle "system apps only" from the action bar
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_app_filter, menu)
                val systemItem = menu.findItem(R.id.action_system_apps_only)
                systemItem?.isChecked = vm.systemAppsOnlyLive.value == true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_system_apps_only -> {
                        val newState = !menuItem.isChecked
                        menuItem.isChecked = newState
                        vm.systemAppsOnlyLive.value = newState
                        updateList()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val ctx = requireContext()
        if (allApps.isEmpty()) {
            scope.launch(Dispatchers.IO) {
                val apps = ctx.packageManager.getInstalledApplications(0)
                    .asSequence()
                    .filterNot { it.isResourceOverlay }
                    .mapNotNull { appInfo ->
                        try {
                            LoadedApplicationInfo(
                                appInfo.loadLabel(ctx.packageManager).toString(),
                                appInfo.loadIcon(ctx.packageManager),
                                appInfo
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                    .sorted()
                    .toList()

                allApps = apps

                withContext(Dispatchers.Main) {
                    binding.progressApps.visibility = View.GONE
                    updateList()
                }
            }
        }

        return binding.root
    }

    private fun updateList() {
        val vm = ViewModelProvider(requireActivity())[AppListViewModel::class.java]
        val query = vm.searchQueryLive.value
        val systemOnly = vm.systemAppsOnlyLive.value == true

        var filtered = if (query.isNullOrEmpty())
            allApps
        else
            allApps.filter { app -> app.label.contains(query, true) || app.info.packageName.contains(query, true) }

        if (systemOnly) {
            filtered = filtered.filter { it.info.flags and ApplicationInfo.FLAG_SYSTEM != 0 }
        }

        adapter.update(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvApps.adapter = null
        scope.cancel()
    }

    override fun toolbarTitle() = requireContext().getString(R.string.apps)
}
