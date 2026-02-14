package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import android.util.Log
import com.reandroid.apk.ApkModule
import java.io.File


class ApkParser {
    constructor(context: Context, packageName: String) {
        val path = getApkPath(context, packageName)
            ?: throw IllegalArgumentException("Failed to locate APK for package $packageName")

        val framework = File("/system/framework/framework-res.apk")
        val apk = File(path)
        module = ApkModule.loadApkFile(apk, if (framework != apk) framework else null)
        realPackage = module.androidManifest
            .manifestElement
            .getElements { it.name == "original-package" }.asSequence().firstOrNull()
            ?.searchAttributeByName("android:name")
            ?.valueString

    }

    val module: ApkModule
    val realPackage: String?

    companion object {
        private fun getApkPath(context: Context, packageName: String) = try {
            context.packageManager.getApplicationInfo(packageName, 0).publicSourceDir
        } catch (e: Throwable) {
            Log.e("ApkParser", "Error getting APK path for package $packageName", e)
            null
        }
    }
}