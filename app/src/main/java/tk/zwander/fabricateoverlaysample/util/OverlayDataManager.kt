package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData

/**
 * Manages persistence of overlay data using SharedPreferences.
 * Allows saving and loading overlay entry backups by overlay name.
 */
object OverlayDataManager {
    private const val PREFS_NAME = "overlay_backups"
    private const val KEY_PREFIX = "overlay_backup_"
    private val gson = Gson()

    /**
     * Get SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save overlay entries to SharedPreferences
     * @param context Application context
     * @param overlayName Unique identifier for the overlay
     * @param entries List of AvailableResourceItemData to save
     */
    fun saveOverlayEntries(
        context: Context,
        overlayName: String,
        entries: List<AvailableResourceItemData>
    ) {
        val prefs = getPrefs(context)
        val json = gson.toJson(entries)
        prefs.edit { putString(getKey(overlayName), json) }
    }

    /**
     * Load overlay entries from SharedPreferences
     * @param context Application context
     * @param overlayName Unique identifier for the overlay
     * @return List of AvailableResourceItemData, or null if not found
     */
    fun loadOverlayEntries(
        context: Context,
        overlayName: String
    ): List<AvailableResourceItemData>? {
        val prefs = getPrefs(context)
        val json = prefs.getString(getKey(overlayName), null) ?: return null
        return try {
            val type = object : TypeToken<List<AvailableResourceItemData>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse overlay entries for $overlayName")
            null
        }
    }

    /**
     * Check if a backup exists for the given overlay
     * @param context Application context
     * @param overlayName Unique identifier for the overlay
     * @return True if backup exists, false otherwise
     */
    fun hasBackup(context: Context, overlayName: String): Boolean {
        return getPrefs(context).contains(getKey(overlayName))
    }

    /**
     * Delete overlay backup from SharedPreferences
     * @param context Application context
     * @param overlayName Unique identifier for the overlay
     */
    fun deleteBackup(context: Context, overlayName: String) {
        getPrefs(context).edit { remove(getKey(overlayName)) }
    }

    /**
     * Generate SharedPreferences key for an overlay
     */
    private fun getKey(overlayName: String): String {
        return "$KEY_PREFIX$overlayName"
    }
}

