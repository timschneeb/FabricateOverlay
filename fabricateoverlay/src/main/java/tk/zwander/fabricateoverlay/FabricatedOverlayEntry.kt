package tk.zwander.fabricateoverlay

import android.os.Parcel
import android.os.Parcelable

/**
 * A resource entry for a [FabricatedOverlay].
 *
 * @param resourceName the name of the resource to overlay. Should
 *   be fully qualified (e.g., "com.android.systemui:integer/quick_settings_num_columns").
 * @param resourceType the type of resource, as determined by [android.util.TypedValue].
 * @param resourceValue the value of the resource. Android 12's framework limits this
 *   to resources that can be represented as integers.
 */
data class FabricatedOverlayEntry(
    var resourceName: String,
    var resourceType: Int,
    var resourceValue: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(resourceName)
        parcel.writeInt(resourceType)
        parcel.writeInt(resourceValue)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FabricatedOverlayEntry> {
        override fun createFromParcel(parcel: Parcel): FabricatedOverlayEntry {
            return FabricatedOverlayEntry(parcel)
        }

        override fun newArray(size: Int): Array<FabricatedOverlayEntry?> {
            return arrayOfNulls(size)
        }
    }
}
