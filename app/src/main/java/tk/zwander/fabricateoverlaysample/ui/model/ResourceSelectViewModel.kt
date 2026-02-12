package tk.zwander.fabricateoverlaysample.ui.model

import androidx.lifecycle.MutableLiveData
import tk.zwander.fabricateoverlaysample.data.TriState
import tk.zwander.fabricateoverlaysample.ui.fragments.FilterBottomSheetFragment

class ResourceSelectViewModel : SearchViewModel() {
    var memberFilter: MutableMap<FilterBottomSheetFragment.Companion.Prefixes, TriState> = mutableMapOf()
    val memberFilterLive: MutableLiveData<MutableMap<FilterBottomSheetFragment.Companion.Prefixes, TriState>> = MutableLiveData(memberFilter)
}
