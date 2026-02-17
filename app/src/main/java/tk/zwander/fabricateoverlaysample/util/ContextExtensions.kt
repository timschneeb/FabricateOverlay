package tk.zwander.fabricateoverlaysample.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.databinding.DialogTextinputBinding

@SuppressLint("ObsoleteSdkInt")
fun Context.ensureHasOverlayPermission(ifTrue: () -> Unit = {}) {
    // UID 0, 1000, or on Android 12 with shell, we can manage overlays.
    if (ShizukuUtils.uid == 0 || ShizukuUtils.uid == 1000 ||
        (Build.VERSION.SDK_INT == Build.VERSION_CODES.S && ShizukuUtils.uid == 2000)) {
        ifTrue()
        return
    }

    showConfirmDialog(
        R.string.missing_permissions,
        R.string.shizuku_insufficient_privileges
    ) {
        packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
            ?.let(::startActivity)
    }
}

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.createProgressDialog(message: String): AlertDialog {
    val container = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    container.addView(
        ProgressBar(this).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(40.dpToPx(), 40.dpToPx()) .apply {
                rightMargin = 16.dpToPx()
            }
        }
    )
    container.addView(
        TextView(this).apply {
            text = message
            setTextAppearance(android.R.style.TextAppearance_Large)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    )

    return MaterialAlertDialogBuilder(this)
        .setTitle(null)
        .setView(container)
        .setCancelable(false)
        .create()
}


fun Context.showAlert(@StringRes title: Int, @StringRes message: Int, onConfirm: (() -> Unit) = {}) {
    showAlert(getString(title), getString(message), onConfirm)
}

fun Context.showAlert(e: Throwable) {
    MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.error))
        .setMessage(e.stackTraceToString())
        .setPositiveButton(android.R.string.ok, null)
        .show()
}

fun Context.showAlert(title: String, message: String, onConfirm: (() -> Unit) = {}) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
        .show()
}

fun Context.showInputAlert(
    layoutInflater: LayoutInflater,
    @StringRes title: Int,
    @StringRes hint: Int,
    value: String = "",
    isNumberInput: Boolean = false,
    callback: ((String) -> Unit)
) {
    showInputAlert(layoutInflater, getString(title), getString(hint), value, isNumberInput, callback)
}

fun Context.showInputAlert(
    layoutInflater: LayoutInflater,
    title: String?,
    hint: String?,
    value: String = "",
    isNumberInput: Boolean = false,
    callback: ((String) -> Unit)
) {
    val content = DialogTextinputBinding.inflate(layoutInflater).apply {
        textInputLayout.hint = hint
        text1.text = Editable.Factory.getInstance().newEditable(value)
        if(isNumberInput)
            text1.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(content.root)
        .setPositiveButton(android.R.string.ok) { inputDialog, _ ->
            (inputDialog as AlertDialog)
                .findViewById<TextView>(android.R.id.text1)
                ?.let {
                    callback.invoke(it.text.toString())
                }
        }
        .setNegativeButton(android.R.string.cancel) { _, _ -> }
        .create()
        .show()
}



fun Context.showConfirmDialog(@StringRes title: Int, @StringRes message: Int, onConfirm: (() -> Unit)) {
    showConfirmDialog(getString(title), getString(message), onConfirm)
}

fun Context.showConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
        .setNegativeButton(android.R.string.cancel) { _, _ -> }
        .show()
}

fun Context.toast(message: String, long: Boolean = true) = Toast.makeText(this, message,
    if(long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes message: Int, long: Boolean = true) = toast(getString(message), long)

fun Context.toast(e: Throwable, long: Boolean = true) =
    toast("$e: ${e.message}", long)
