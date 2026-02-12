package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.util.Log
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
                val searchView = (searchItem.actionView as SearchView)

                // Keep the search query in sync and restore previous query
                val currentQuery = vm.searchQueryLive.value ?: ""
                searchView.setQuery(currentQuery, false)
                searchView.isSubmitButtonEnabled = true

                Log.e("SearchableBaseFragment", "Restoring search query: '$currentQuery'")

                vm.searchQueryLive.observe(viewLifecycleOwner) { q ->
                    val text = q ?: ""
                    if (searchView.query.toString() != text) {
                        searchView.setQuery(text, false)
                    }
                }

                // If there's an existing query, ensure the search is expanded so it stays visible
                if (currentQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.isIconified = false
                    searchView.post {
                        // Make sure text is present after expansion
                        val text = vm.searchQueryLive.value ?: ""
                        if (searchView.query.toString() != text) {
                            searchView.setQuery(text, false)
                        }
                        searchView.requestFocus()
                    }
                }

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        vm.searchQueryLive.value = query ?: ""
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
