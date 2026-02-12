package tk.zwander.fabricateoverlaysample.ui.model

import androidx.lifecycle.MutableLiveData
import tk.zwander.fabricateoverlaysample.data.ResPrefixes
import tk.zwander.fabricateoverlaysample.data.TriState

class ResourceSelectViewModel : SearchViewModel() {
    var memberFilter: MutableMap<ResPrefixes, TriState> = mutableMapOf()
    val memberFilterLive: MutableLiveData<MutableMap<ResPrefixes, TriState>> = MutableLiveData(memberFilter)

    var expandedHeaders: MutableSet<String> = mutableSetOf()
    // Scroll state: pair(index, offset). Null when not set.
    val scrollStateLive: MutableLiveData<Pair<Int, Int>?> = MutableLiveData(null)
}
