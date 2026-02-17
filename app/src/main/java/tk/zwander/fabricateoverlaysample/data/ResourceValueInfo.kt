package tk.zwander.fabricateoverlaysample.data

import android.os.Parcelable
import android.util.TypedValue
import kotlinx.parcelize.Parcelize

/**
 * Represents a resolved resource value step: type, raw data, and the coerced value string.
 */
@Parcelize
data class ResourceValueInfo(
    val type: Int,
    val data: Int?,
    val stringData: String?
) : Parcelable {
    fun displayString(): String {
        val value = TypedValue()
        value.type = type
        value.data = data ?: 0
        value.string = stringData
        return value.coerceToString().toString()
    }

    override fun toString() = displayString()
}
