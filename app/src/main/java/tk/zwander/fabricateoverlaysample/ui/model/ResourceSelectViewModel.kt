package tk.zwander.fabricateoverlaysample.ui.model

import androidx.lifecycle.MutableLiveData
import tk.zwander.fabricateoverlaysample.data.TriState
import tk.zwander.fabricateoverlaysample.ui.fragments.FilterBottomSheetFragment

class ResourceSelectViewModel : SearchViewModel() {
    var memberFilter: MutableMap<FilterBottomSheetFragment.Companion.Prefixes, TriState> = mutableMapOf()
    val memberFilterLive: MutableLiveData<MutableMap<FilterBottomSheetFragment.Companion.Prefixes, TriState>> = MutableLiveData(memberFilter)

    // Keep expanded header titles so we can restore expanded/collapsed state across rotations
    var expandedHeaders: MutableSet<String> = mutableSetOf()
    // Scroll state: pair(index, offset). Null when not set.
    val scrollStateLive: MutableLiveData<Pair<Int, Int>?> = MutableLiveData(null)
}
