package tk.zwander.fabricateoverlaysample.ui.model

import androidx.lifecycle.MutableLiveData

class AppListViewModel : SearchViewModel() {
    val systemAppsOnlyLive: MutableLiveData<Boolean> = MutableLiveData(false)
}
