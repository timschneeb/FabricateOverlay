package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import com.reandroid.apk.ApkModule
import timber.log.Timber
import java.io.File


class ApkParser {
    constructor(context: Context, packageName: String) {
        val path = getApkPath(context, packageName)
            ?: throw IllegalArgumentException("Failed to locate APK for package $packageName")

        val framework = File("/system/framework/framework-res.apk")
        val apk = File(path)
        module = ApkModule.loadApkFile(apk, if (framework != apk) framework else null)
    }

    val module: ApkModule

    companion object {
        private fun getApkPath(context: Context, packageName: String) = try {
            context.packageManager.getApplicationInfo(packageName, 0).publicSourceDir
        } catch (e: Throwable) {
            Timber.e(e, "Error getting APK path for package $packageName")
            null
        }
    }
}