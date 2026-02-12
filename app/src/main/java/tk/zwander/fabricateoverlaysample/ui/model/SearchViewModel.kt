package tk.zwander.fabricateoverlaysample.ui.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class SearchViewModel : ViewModel() {
    val searchQueryLive: MutableLiveData<String?> = MutableLiveData(null)
}
