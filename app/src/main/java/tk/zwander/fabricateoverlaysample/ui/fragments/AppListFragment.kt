package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

class AppListFragment : Fragment(), MainActivity.Searchable, MainActivity.TitleProvider {
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
            adapter.update(allApps)
        } else {
            binding.progressApps.visibility = View.VISIBLE
        }

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
                    adapter.update(apps)
                }
            }
        }

        return binding.root
    }

    override fun onSearchQuery(q: String) {
        val filtered = if (q.isEmpty()) allApps else allApps.filter { it.label.contains(q, true) || it.info.packageName.contains(q, true) }
        adapter.update(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvApps.adapter = null
        scope.cancel()
    }

    override fun toolbarTitle() = requireContext().getString(R.string.apps)
}
