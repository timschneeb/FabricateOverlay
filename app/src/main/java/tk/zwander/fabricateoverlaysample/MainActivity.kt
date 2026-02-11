package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.databinding.ActivityMainBinding
import tk.zwander.fabricateoverlaysample.ui.fragments.AppListFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.ChooseResourcesFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.CurrentOverlayEntriesFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.HomeFragment
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showAlert

@SuppressLint("PrivateApi")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Fragments can implement this to provide a toolbar title that the activity will use
    interface TitleProvider {
        fun toolbarTitle(): CharSequence?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.setHiddenApiExemptions("L")

        if (!ShizukuUtils.shizukuAvailable) {
            showShizukuDialog()
            return
        }

        if (ShizukuUtils.hasShizukuPermission(this)) {
            init()
        } else {
            ShizukuUtils.requestShizukuPermission(this) { granted ->
                if (granted) {
                    init()
                } else {
                    showShizukuDialog()
                }
            }
        }
    }

    private fun showShizukuDialog() {
        showAlert(R.string.error, R.string.shizuku_not_set_up) {
            finish()
        }
    }

    private fun init() {
        // Switch to view-based layout (fragment container)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Setup toolbar as action bar
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> { leftMargin = insets.left; topMargin = insets.top; rightMargin = insets.right }
            WindowInsetsCompat.CONSUMED
        }

        // Load the home fragment
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commitNow()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
            invalidateOptionsMenu()
            // Keep the action bar title in sync with the current top-most fragment
            updateTitleFromTopFragment()
        }

        // Try to set the title from the currently visible fragment on startup
        updateTitleFromTopFragment()

        ensureHasOverlayPermission()
    }

    private fun updateTitleFromTopFragment() {
        // Find the top-most visible fragment and ask it for a title if it provides one
        val frag = supportFragmentManager.fragments.asReversed().firstOrNull { it != null }
        title = if (frag is TitleProvider) {
            frag.toolbarTitle()
        } else {
            // Fallback to the app name
            getString(R.string.app_name)
        }
    }

    // Navigation helpers used by fragments
    fun navigateToAppList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AppListFragment())
            .addToBackStack(null)
            .commit()
        invalidateOptionsMenu()
    }

    fun navigateToCurrentOverlays(appInfo: ApplicationInfo) {
        val frag = CurrentOverlayEntriesFragment()
        val args = Bundle()
        args.putParcelable("appInfo", appInfo)
        frag.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, frag)
            .addToBackStack(null)
            .commit()
    }

    // Navigate to the resources picker for the given app. Optionally pass existing selected entries
    fun navigateToResourcePicker(appInfo: ApplicationInfo, existingEntries: ArrayList<FabricatedOverlayEntry>? = null) {
        val frag = ChooseResourcesFragment()
        val args = Bundle()
        args.putParcelable("appInfo", appInfo)
        if (existingEntries != null) {
            args.putParcelableArrayList("existing_entries", existingEntries)
        }
        frag.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, frag)
            .addToBackStack(null)
            .commit()

        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        // only show search when the current top-most visible fragment supports it
        val frag = supportFragmentManager.fragments.asReversed().firstOrNull { it != null && it.isVisible }
        searchItem.isVisible = frag is Searchable

        val sv = searchItem.actionView as? SearchView
        sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                forwardSearchQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                forwardSearchQuery(newText ?: "")
                return true
            }
        })
        return true
    }

    private fun forwardSearchQuery(q: String) {
        // Find the top-most visible fragment that implements Searchable and forward the query
        val frag = supportFragmentManager.fragments.asReversed().firstOrNull { it != null && it.isVisible }
        if (frag is Searchable) frag.onSearchQuery(q)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onSupportNavigateUp()
    }

    interface Searchable {
        fun onSearchQuery(q: String)
    }
}