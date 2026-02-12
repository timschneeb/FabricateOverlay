package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.model.SearchViewModel
import kotlin.reflect.KClass

/**
 * Base fragment that wires up a SearchView action into the toolbar via a MenuProvider.
 * Subclasses should call [setupSearchMenu] (usually from onViewCreated) to attach the menu.
 */
abstract class SearchableBaseFragment<T : SearchViewModel>(
    val viewModelClass: KClass<T>
) : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchMenu()
    }

    protected fun setupSearchMenu() {
        val menuHost: MenuHost = requireActivity()
        val vm = ViewModelProvider(requireActivity())[viewModelClass.java] as SearchViewModel

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
                val searchItem = menu.findItem(R.id.action_search)
                (searchItem.actionView as SearchView).apply {
                    setQuery(vm.searchQueryLive.value ?: "", false)
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            vm.searchQueryLive.value = query ?: ""
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            vm.searchQueryLive.value = newText ?: ""
                            return true
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
