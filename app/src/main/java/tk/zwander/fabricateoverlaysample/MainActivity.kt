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
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.databinding.ActivityMainBinding
import tk.zwander.fabricateoverlaysample.ui.fragments.AppListFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.CurrentOverlayEntriesFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.HomeFragment
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission
import tk.zwander.fabricateoverlaysample.util.showAlert

@SuppressLint("PrivateApi")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
            invalidateOptionsMenu()
        }

        ensureHasOverlayPermission()
    }

    // Navigation helpers used by fragments
    fun navigateToAppList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AppListFragment())
            .addToBackStack(null)
            .commit()

        title = getString(R.string.apps)
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

        title = appInfo.loadLabel(packageManager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        // only show search when the current fragment supports it
        val frag = supportFragmentManager.findFragmentById(R.id.fragment_container)
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
        val frag = supportFragmentManager.findFragmentById(R.id.fragment_container)
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