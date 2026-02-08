package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.ui.pages.AppListPage
import tk.zwander.fabricateoverlaysample.ui.pages.CurrentOverlayEntriesListPage
import tk.zwander.fabricateoverlaysample.ui.pages.HomePage

@SuppressLint("PrivateApi")
class MainActivity : AppCompatActivity() {
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
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.shizuku_not_set_up)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .apply {
                setOnShowListener {
                    findViewById<TextView>(Class.forName("com.android.internal.R\$id").getField("message").getInt(null))
                        ?.movementMethod = LinkMovementMethod()
                }
            }
            .show()
    }

    private fun init() {
        setContent {
            MaterialTheme(
                colors = darkColors()
            ) {
                Surface {
                    var appInfoArg by remember {
                        mutableStateOf<ApplicationInfo?>(null)
                    }
                    val navController = rememberNavController()
                    val activity = LocalActivity.current

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            activity?.setTitle(R.string.overlays)

                            HomePage(navController)
                        }
                        composable("app_list") {
                            activity?.setTitle(R.string.apps)

                            AppListPage(navController)
                        }
                        composable(
                            route = "list_overlays"
                        ) {
                            // Try to read appInfo from the previous back stack entry's SavedStateHandle
                            val prevEntry = navController.previousBackStackEntry
                            val saved = prevEntry?.savedStateHandle?.getLiveData<ApplicationInfo>("appInfo")
                            saved?.value?.let {
                                appInfoArg = it
                                // Clear it to avoid stale data on future navigations
                                prevEntry.savedStateHandle.remove<ApplicationInfo>("appInfo")
                            }

                            activity?.title = appInfoArg?.loadLabel(activity.packageManager)

                            // If appInfoArg is null, show nothing or navigate back to list
                            if (appInfoArg != null) {
                                CurrentOverlayEntriesListPage(
                                    navController,
                                    appInfoArg!!
                                )
                            } else {
                                // Fallback: navigate back to app list to avoid crash
                                LaunchedEffect(Unit) {
                                    navController.navigate("app_list") {
                                        // pop the current entry to avoid stacking
                                        popUpTo("main")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}