package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.DialogFragment
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.DialogSaveOverlayBinding
import tk.zwander.fabricateoverlaysample.util.ensureHasOverlayPermission

class SaveOverlayDialogFragment : DialogFragment() {
    private lateinit var binding: DialogSaveOverlayBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val info = requireArguments().getParcelable<android.content.pm.ApplicationInfo>("appInfo")!!

        val raw = requireArguments().getParcelableArrayList<android.os.Parcelable>("entries")
        val entriesList = ArrayList<FabricatedOverlayEntry>()
        raw?.forEach { p -> if (p is FabricatedOverlayEntry) entriesList.add(p) }

        val ctx = requireContext()

        // Inflate the dialog layout via view binding
        binding = DialogSaveOverlayBinding.inflate(layoutInflater)
        val et = binding.etOverlayName

        val builder = MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.add_overlay)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = et.text.toString().filter { char -> (char.isLetterOrDigit() || char == '.' || char == '_') }
                    .replace(Regex("(_+)\\1"), "_")
                    .replace(Regex("(\\.+)\\1"), ".")
                    .replace("_.", "_")
                    .replace("._", ".")

                val fullName = "${ctx.packageName}.${info.packageName}.$name"

                ctx.ensureHasOverlayPermission {
                    OverlayAPI.getInstance(ctx) { api ->
                        api.registerFabricatedOverlay(
                            FabricatedOverlay(
                                fullName,
                                info.packageName,
                                OverlayAPI.servicePackage ?: "com.android.shell"
                            ).apply {
                                entriesList.forEach { e ->
                                    entries[e.resourceName] = e
                                }
                            }
                        )

                        // pop back to main fragment and set title
                        activity?.supportFragmentManager?.popBackStackImmediate(null, 0)
                        activity?.title = ctx.getString(R.string.overlays)
                    }
                }
            }

        return builder.create()
    }
}
