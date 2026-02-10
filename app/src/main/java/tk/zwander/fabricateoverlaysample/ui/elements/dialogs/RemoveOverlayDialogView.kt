package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission

object RemoveOverlayDialogView {
    fun show(context: Context, info: OverlayInfo, onChange: () -> Unit) {
        context.ensureHasOverlayPermission {
            MaterialAlertDialogBuilder(context)
                .setMessage(context.getString(R.string.delete_confirmation))
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    OverlayAPI.getInstance(context) { api ->
                        api.unregisterFabricatedOverlay(
                            FabricatedOverlay.generateOverlayIdentifier(
                                info.overlayName,
                                OverlayAPI.servicePackage ?: "com.android.shell"
                            )
                        )
                        onChange()
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }
}
