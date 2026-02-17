package tk.zwander.fabricateoverlaysample

import android.app.Application
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


class OverlayApp : Application() {
    override fun onCreate() {
        HiddenApiBypass.setHiddenApiExemptions("")
        plant(DebugTree())
        super.onCreate()
    }
}