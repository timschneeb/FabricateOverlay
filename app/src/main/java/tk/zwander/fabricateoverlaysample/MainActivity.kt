package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.databinding.ActivityMainBinding
import tk.zwander.fabricateoverlaysample.ui.fragments.AppListFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.CurrentOverlayEntriesFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.HomeFragment
import tk.zwander.fabricateoverlaysample.ui.fragments.ResourceSelectionFragment
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
            updateTitleFromTopFragment()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
        updateTitleFromTopFragment()
        ensureHasOverlayPermission()
    }

    private fun updateTitleFromTopFragment() {
        // Find the top-most visible fragment that is hosted in the main container and ask it for a title
        val frag = supportFragmentManager.fragments.asReversed().firstOrNull { it != null && it.id == R.id.fragment_container }
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
        val frag = ResourceSelectionFragment()
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
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onSupportNavigateUp()
    }
}