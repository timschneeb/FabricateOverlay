package tk.zwander.fabricateoverlaysample.util

import android.util.TypedValue
import androidx.annotation.FloatRange
import androidx.core.util.TypedValueCompat

object TypedValueExt {
    /**
     *
     * Creates a complex data integer that stores a dimension value and units.
     *
     *
     * The resulting value can be passed to e.g.
     * [TypedValue.complexToDimensionPixelOffset] to calculate the pixel
     * value for the dimension.
     *
     * @param value the value of the dimension
     * @param units the units of the dimension, e.g. [TypedValue.COMPLEX_UNIT_DIP]
     * @return A complex data integer representing the value and units of the dimension.
     */
    fun createComplexDimension(
        @FloatRange(from = (-0x800000).toDouble(), to = 0x7FFFFF.toDouble()) value: Float,
        @TypedValueCompat.ComplexDimensionUnit units: Int
    ): Int {
        return TypedValue::class.java.getMethod("createComplexDimension", Float::class.java, Int::class.java)
            .invoke(null, value, units) as Int
    }

    /**
     * Convert a base value to a complex data integer.  This sets the [ ][TypedValue.COMPLEX_MANTISSA_MASK] and [TypedValue.COMPLEX_RADIX_MASK] fields of the
     * data to create a floating point representation of the given value. The units are not set.
     *
     *
     * This is the inverse of [TypedValue.complexToFloat].
     *
     * @param value A floating point value.
     * @return A complex data integer representing the value.
     * @hide
     */
    fun floatToComplex(@FloatRange(from = (-0x800000).toDouble(), to = 0x7FFFFF.toDouble()) value: Float): Int {
        return TypedValue::class.java.getMethod("floatToComplex", Float::class.java)
            .invoke(null, value) as Int
    }
}