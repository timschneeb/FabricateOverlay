package tk.zwander.fabricateoverlaysample.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailableResourceItemData(
    val name: String,
    val resourceName: String,
    val type: Int,
    var values: Array<ResourceValueInfo>
) : Comparable<AvailableResourceItemData>, Parcelable {
    fun valuesToString(): String {
        return values.joinToString(" -> ", transform = ResourceValueInfo::toString)
    }

    fun setValue(value: Int) {
        values = arrayOf(ResourceValueInfo(type, value, null))
    }

    fun setValue(value: String) {
        values = arrayOf(ResourceValueInfo(type, null, value))
    }

    override fun compareTo(other: AvailableResourceItemData): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailableResourceItemData

        if (name != other.name) return false
        if (type != other.type) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type
        result = 31 * result + values.contentHashCode()
        return result
    }
}