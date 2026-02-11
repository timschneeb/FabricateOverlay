package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.FragmentHomeBinding
import tk.zwander.fabricateoverlaysample.ui.adapters.RegisteredListItem
import tk.zwander.fabricateoverlaysample.ui.adapters.RegisteredOverlaySectionAdapter
import tk.zwander.fabricateoverlaysample.util.MarginItemDecoration

class HomeFragment : Fragment() {
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
            adapter = RegisteredOverlaySectionAdapter(mutableListOf()) {
                loadOverlays(true)
            }.also {
                this@HomeFragment.adapter = it
            }
            addItemDecoration(MarginItemDecoration())
        }

        binding.fabApps.setOnClickListener {
            (activity as? tk.zwander.fabricateoverlaysample.MainActivity)?.navigateToAppList()
        }

        // If we have cached items for this fragment instance, display them without reloading
        if (cachedItems.isNotEmpty()) {
            adapter.update(cachedItems)
            binding.progressLoading.visibility = View.GONE
        } else {
            binding.root.post {
                loadOverlays(false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.overlays)
        // Always reload overlays when resuming to ensure newly created overlays are displayed
        loadOverlays(true)
    }

    private fun loadOverlays(forceReload: Boolean) {
        if (!forceReload && cachedItems.isNotEmpty())
            return

        binding.progressLoading.visibility = View.VISIBLE

        OverlayAPI.getInstance(requireContext()) { api ->
            try {
                viewLifecycleOwner.lifecycleScope.launch {
                    val grouped = withContext(Dispatchers.IO) {
                        api.getAllOverlays(-2 /* UserHandle.USER_CURRENT */)
                            .mapNotNull { (key, value) ->
                                val filtered = value.filter { item ->
                                    item.isFabricated && item.overlayName?.contains(requireContext().packageName) == true
                                }
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

                    binding.root.post {
                        binding.progressLoading.visibility = View.GONE
                    }
                }
            }
            catch (e: IllegalStateException) {
                // This can occur if the fragment is destroyed while loading overlays
                e.printStackTrace()
                binding.root.post {
                    binding.progressLoading.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvRegisteredOverlays.adapter = null
    }
}
