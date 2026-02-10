package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import android.os.Build
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.R

fun Context.ensureHasOverlayPermission(ifTrue: () -> Unit = {}) {
    // UID 0, 1000, or on Android 12 with shell, we can manage overlays.
    if (ShizukuUtils.uid == 0 || ShizukuUtils.uid == 1000 ||
        (Build.VERSION.SDK_INT == Build.VERSION_CODES.S && ShizukuUtils.uid == 2000)) {
        ifTrue()
        return
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.missing_permissions))
        .setMessage(getString(R.string.shizuku_insufficient_privileges))
        .setPositiveButton(android.R.string.ok) { _, _ ->
            packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                ?.let(::startActivity)
        }
        .setNegativeButton(android.R.string.cancel, null)
        .setCancelable(false)
        .create()
        .show()
}
