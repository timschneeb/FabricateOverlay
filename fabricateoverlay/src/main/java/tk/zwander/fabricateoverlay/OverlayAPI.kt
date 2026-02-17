@file:Suppress("unused")

package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.om.FabricatedOverlay
import android.content.om.IOverlayManager
import android.content.om.OverlayIdentifier
import android.content.om.OverlayManagerTransaction
import android.os.FabricatedOverlayInternal
import android.os.FabricatedOverlayInternalEntry
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import tk.zwander.fabricateoverlay.OverlayAPI.Companion.getInstance
import tk.zwander.fabricateoverlay.OverlayAPI.Companion.getInstanceDirect

/**
 * The main API for registering and unregistering fabricated overlays.
 * There are also some extra overlay management features here.
 *
 * To use this, you must either have Java root/privileged access or
 * Shizuku set up and granted.
 *
 * There are a few ways to get an instance of this class.
 * 1. Use [getInstance] after Shizuku is up and running. This will retrieve
 *   an [android.content.om.IOverlayManager] instance, instantiate this class,
 *   and then fire the passed callback.
 * 2. Use [getInstanceDirect]. If you have your own way to get an
 *   [android.content.om.IOverlayManager] instance, you can use this
 *   method to pass it directly and not deal with callbacks.
 *
 * @see [ShizukuUtils]
 *
 * @param iomService an IBinder instance of [android.content.om.IOverlayManager].
 */
@SuppressLint("PrivateApi")
class OverlayAPI private constructor(private val iomService: IBinder) {
    companion object {
        /**
         * The current API version. You probably don't need to worry about this.
         */
        @Suppress("MemberVisibilityCanBePrivate")
        const val API_VERSION = 1

        /**
         * The connection to the Shizuku service. Since IOverlayManager can only
         * be accessed by the shell user and users more privileged, the default
         * implementation needs to retrieve IOverlayManager as shell using Shizuku.
         */
        private val connection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
                synchronized(instanceLock) {
                    binding = false

                    val service = IShizukuService.Stub.asInterface(binder)

                    instance = OverlayAPI(ShizukuBinderWrapper(service.iom))
                    servicePackage = service.packageName

                    callbacks.forEach { callback ->
                        callback(instance!!)
                    }
                    callbacks.clear()
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                synchronized(instanceLock) {
                    binding = false

                    instance = null
                }
            }
        }

        /**
         * Callbacks for getting the [OverlayAPI] instance.
         */
        private val callbacks = arrayListOf<(OverlayAPI) -> Unit>()

        /**
         * A thread lock.
         */
        private val instanceLock = Any()

        /**
         * The current [OverlayAPI] instance.
         */
        @Volatile
        private var instance: OverlayAPI? = null

        /**
         * The package of the Shizuku service (either "com.android.shell"
         * or "android").
         */
        var servicePackage: String? = null
            private set

        /**
         * Whether there's been a Shizuku Service bind request.
         */
        @Volatile
        private var binding = false

        /**
         * Get an instance of [OverlayAPI] using Shizuku.
         *
         * @param context used to get the app's package name.
         * @param callback invoked once the [OverlayAPI] instance is ready.
         */
        fun getInstance(context: Context, callback: (OverlayAPI) -> Unit) {
            synchronized(instanceLock) {
                if (instance != null) {
                    //If we already have an instance, immediately invoke the callback.
                    callback(instance!!)
                } else {
                    //Otherwise, queue the callback.
                    callbacks.add(callback)

                    if (!binding) {
                        //If there's not already a bind request in progress, make one.
                        binding = true

                        val serviceArgs = Shizuku.UserServiceArgs(
                            ComponentName(
                                context.packageName,
                                ShizukuService::class.java.name
                            )
                        ).processNameSuffix("service")
                            .debuggable(BuildConfig.DEBUG)
                            .version(API_VERSION)
                            .daemon(false)

                        Shizuku.bindUserService(serviceArgs, connection)
                    }
                }
            }
        }

        /**
         * Directly get an instance of [OverlayAPI] using your own instance
         * of [android.content.om.IOverlayManager].
         */
        fun getInstanceDirect(iOverlayManager: IBinder): OverlayAPI {
            return instance ?: OverlayAPI(iOverlayManager).apply {
                instance = this
            }
        }

        /**
         * If you've already retrieved an instance before, you can use
         * this to get it without a callback.
         */
        fun peekInstance(): OverlayAPI? {
            return instance
        }
    }

    private val omtbClass = Class.forName($$"android.content.om.OverlayManagerTransaction$Builder")
    private val oiClass = Class.forName("android.content.om.OverlayIdentifier")

    private val iomInstance = IOverlayManager.Stub.asInterface(iomService)

    /**
     * Register a new [FabricatedOverlayWrapper]. The overlay should immediately be available
     * to enable, although it won't be enabled automatically.
     *
     * @param overlay overlay to register.
     */
    @SuppressLint("BlockedPrivateApi")
    fun registerFabricatedOverlay(overlay: FabricatedOverlayWrapper) {
        val frro = FabricatedOverlay(
            overlay.overlayName,
            overlay.targetPackage
        ).apply {
            setTargetOverlayable(overlay.targetOverlayableName)
        }

        val mOverlay = frro.javaClass.getDeclaredField("mOverlay").run {
            isAccessible = true
            get(frro) as FabricatedOverlayInternal
        }

        // Needed on Samsung devices due to additional UID/owning package name check
        // The new AOSP FRRO API would have set this later by itself,
        // however the deprecated FRRO.Builder API sets it here, which Samsung apparently relies on in their checks.
        mOverlay.packageName = overlay.sourcePackage

        overlay.entries.forEach { (_, entry) ->
            // Decide which setResourceValue overload to use.
            val isDimension = entry.resourceType == TypedValue.TYPE_DIMENSION ||
                    entry.resourceType == TypedValue.TYPE_FRACTION ||
                    entry.resourceType == TypedValue.TYPE_FLOAT
            if (isDimension) {
                // Workaround: Framework has an incorrect integer type assertion that does not include TYPE_DIMENSION or TYPE_FRACTION
                mOverlay.entries.add(
                    FabricatedOverlayInternalEntry().apply {
                        resourceName = entry.resourceName
                        dataType = entry.resourceType
                        data = entry.resourceValue
                    }
                )
            }
            else if (entry.resourceType == TypedValue.TYPE_STRING) {
                // Use string overload
                frro.setResourceValue(
                    entry.resourceName,
                    entry.resourceType,
                    entry.resourceValueString!!,
                    null
                )
            } else if (entry.resourceType >= TypedValue.TYPE_FIRST_INT && entry.resourceType <= TypedValue.TYPE_LAST_INT) {
                // Integer-based overload. Ensure type is within int range allowed by framework. .
                frro.setResourceValue(
                    entry.resourceName,
                    entry.resourceType,
                    entry.resourceValue,
                    null
                )
            }
            else {
                // TODO: figure out file descriptors

                Log.e("OverlayAPI", "Resource ${entry.resourceName} has unsupported type ${entry.resourceType}, skipping. Only integer and string types are currently supported.")
                // throw NotImplementedError("Non-int, non-string resources with binary payloads are not yet supported.")
            }
        }

        // We can't access the nested Builder class even with stubs, so use reflection
        val omtbClass = Class.forName($$"android.content.om.OverlayManagerTransaction$Builder")
        @Suppress("DEPRECATION")
        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod("registerFabricatedOverlay", frro.javaClass)
            .invoke(omtbInstance, frro)

        // Build and commit transaction
        commit(
            omtbClass.getMethod("build").invoke(omtbInstance)!! as OverlayManagerTransaction
        )
    }

    /**
     * Unregister a [FabricatedOverlayWrapper].
     *
     * @param identifier the overlay identifier, retrieved using
     *   [FabricatedOverlayWrapper.identifier] or [FabricatedOverlayWrapper.generateOverlayIdentifier].
     */
    fun unregisterFabricatedOverlay(identifier: OverlayIdentifier) {
        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "unregisterFabricatedOverlay",
            oiClass
        ).invoke(omtbInstance, identifier)

        val omtInstance = omtbClass.getMethod(
            "build"
        ).invoke(omtbInstance)!! as OverlayManagerTransaction

        commit(omtInstance)
    }

    /*
        IOverlayManager.aidl wrapper methods.
     */
    fun getAllOverlays(userId: Int): Map<String, List<OverlayInfo>> =
        iomInstance.getAllOverlays(userId)
            .map { entry -> entry.key.toString() to (entry.value as List<*>)
            .map { OverlayInfo(it!!) } }
            .toMap()

    fun getOverlayInfosForTarget(targetPackageName: String, userId: Int): List<OverlayInfo> =
        iomInstance.getOverlayInfosForTarget(targetPackageName, userId)
            .map { OverlayInfo(it!!) }

    fun getOverlayInfo(packageName: String, userId: Int): OverlayInfo? =
        iomInstance.getOverlayInfo(packageName, userId)?.let {
            OverlayInfo(it)
        }

    fun getOverlayInfoByIdentifier(identifier: OverlayIdentifier, userId: Int): OverlayInfo? =
        iomInstance.getOverlayInfoByIdentifier(identifier, userId)?.let {
            OverlayInfo(it)
        }

    /**
     * Use this for changing the state of fabricated overlays.
     */
    fun setEnabled(identifier: Any, enable: Boolean, userId: Int) {
        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "setEnabled",
            oiClass,
            Boolean::class.java,
            Int::class.java
        ).invoke(omtbInstance, identifier, enable, userId)

        val omtInstance = omtbClass.getMethod(
            "build"
        ).invoke(omtbInstance)!! as OverlayManagerTransaction

        commit(omtInstance)
    }

    fun setEnabled(packageName: String, enable: Boolean, userId: Int): Boolean =
        iomInstance.setEnabled(packageName, enable, userId)

    fun setEnabledExclusive(packageName: String, enable: Boolean, userId: Int): Boolean =
        iomInstance.setEnabledExclusive(packageName, enable, userId)

    fun setEnabledExclusiveInCategory(packageName: String, userId: Int): Boolean =
        iomInstance.setEnabledExclusiveInCategory(packageName, userId)

    fun setPriority(packageName: String, newParentPackageName: String, userId: Int): Boolean =
        iomInstance.setPriority(packageName, newParentPackageName, userId)

    fun setHighestPriority(packageName: String, userId: Int): Boolean =
        iomInstance.setHighestPriority(packageName, userId)

    fun setLowestPriority(packageName: String, userId: Int): Boolean =
        iomInstance.setLowestPriority(packageName, userId)

    fun getDefaultOverlayPackages(): Array<String> = iomInstance.defaultOverlayPackages

    fun invalidateCachesForOverlay(packageName: String, userId: Int) {
        iomInstance.invalidateCachesForOverlay(packageName, userId)
    }

    fun commit(transaction: OverlayManagerTransaction) {
        iomInstance.commit(transaction)
    }
}