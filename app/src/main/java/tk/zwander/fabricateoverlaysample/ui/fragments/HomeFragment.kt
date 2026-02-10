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
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.FragmentHomeBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.RegisteredListItem
import tk.zwander.fabricateoverlaysample.ui.adapters.RegisteredOverlaySectionAdapter
import tk.zwander.fabricateoverlay.OverlayAPI

class HomeFragment : Fragment() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: RegisteredOverlaySectionAdapter
    private var cachedItems: List<RegisteredListItem> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.rvRegisteredOverlays.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RegisteredOverlaySectionAdapter(emptyList()) { loadOverlays(true) }.also {
                this@HomeFragment.adapter = it
            }
        }

        binding.fabApps.setOnClickListener {
            (activity as? tk.zwander.fabricateoverlaysample.MainActivity)?.navigateToAppList()
        }

        // If we have cached items for this fragment instance, display them without reloading
        if (cachedItems.isNotEmpty()) {
            adapter.update(cachedItems)
            binding.progressLoading.visibility = View.GONE
        } else {
            binding.progressLoading.visibility = View.VISIBLE
            binding.root.post {
                loadOverlays(false) {
                    binding.progressLoading.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.overlays)
        // Always reload overlays when resuming to ensure newly created overlays are displayed
        binding.progressLoading.visibility = View.VISIBLE
        loadOverlays(true) {
            binding.progressLoading.visibility = View.GONE
        }
    }

    private fun loadOverlays(forceReload: Boolean, onLoaded: (() -> Unit)? = null) {
        if (!forceReload && cachedItems.isNotEmpty()) return

        adapter.update(emptyList())

        OverlayAPI.getInstance(requireContext()) { api ->
            scope.launch {
                val grouped = withContext(Dispatchers.IO) {
                    api.getAllOverlays(-2 /* UserHandle.USER_CURRENT */).mapNotNull { (key, value) ->
                        val filtered = value.filter { item -> item.isFabricated && item.overlayName?.contains(requireContext().packageName) == true }
                        if (filtered.isEmpty()) null else (key to filtered)
                    }.toMap().toSortedMap { o1, o2 -> o1.compareTo(o2, true) }
                }

                val merged = ArrayList<RegisteredListItem>()
                grouped.forEach { (pkg, list) ->
                    merged.add(RegisteredListItem.Header(pkg))
                    list.sortedBy { it.overlayName }.forEach { info ->
                        merged.add(RegisteredListItem.Overlay(info))
                    }
                }

                cachedItems = merged
                adapter.update(merged)
                onLoaded?.invoke()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvRegisteredOverlays.adapter = null
        scope.cancel()
    }
}
