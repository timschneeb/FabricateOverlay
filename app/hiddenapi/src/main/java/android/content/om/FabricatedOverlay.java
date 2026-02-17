/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.om;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.res.AssetFileDescriptor;
import android.os.FabricatedOverlayInternal;
import android.os.FabricatedOverlayInternalEntry;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.TypedValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Objects;

/**
 * FabricatedOverlay describes the content of Fabricated Runtime Resource Overlay (FRRO) that is
 * used to overlay the app's resources. The app should register the {@link FabricatedOverlay}
 * instance in an {@link OverlayManagerTransaction} by calling {@link
 * OverlayManagerTransaction#registerFabricatedOverlay(FabricatedOverlay)}. The FRRO is
 * created once the transaction is committed successfully.
 *
 * <p>The app creates a FabricatedOverlay to describe the how to overlay string, integer, and file
 * type resources. Before creating any frro, please define a target overlayable in {@code
 * res/values/overlayable.xml} that describes what kind of resources can be overlaid, what kind of
 * roles or applications can overlay the resources. Here is an example.
 *
 * <pre>{@code
 * <overlayable name="SignatureOverlayable" actor="overlay://theme">
 *     <!-- The app with the same signature can overlay the below resources -->
 *     <policy type="signature">
 *         <item type="color" name="mycolor" />
 *         <item type="string" name="mystring" />
 *     </policy>
 * </overlayable>
 * }</pre>
 *
 * <p>The overlay must assign the target overlayable name just like the above example by calling
 * {@link #setTargetOverlayable(String)}. Here is an example:
 *
 * <pre>{@code
 * FabricatedOverlay fabricatedOverlay = new FabricatedOverlay("overlay_name",
 *                                                             context.getPackageName());
 * fabricatedOverlay.setTargetOverlayable("SignatureOverlayable")
 * fabricatedOverlay.setResourceValue("mycolor", TypedValue.TYPE_INT_COLOR_ARGB8, Color.White)
 * fabricatedOverlay.setResourceValue("mystring", TypedValue.TYPE_STRING, "Hello")
 * }</pre>
 *
 * <p>The app can create any {@link FabricatedOverlay} instance by calling the following APIs.
 *
 * <ul>
 *   <li>{@link #setTargetOverlayable(String)}
 *   <li>{@link #setResourceValue(String, int, int, String)}
 *   <li>{@link #setResourceValue(String, int, String, String)}
 *   <li>{@link #setResourceValue(String, ParcelFileDescriptor, String)}
 * </ul>
 *
 * @see OverlayManager
 * @see OverlayManagerTransaction
 */
public class FabricatedOverlay {

    /**
     * Retrieves the identifier for this fabricated overlay.
     * @return the overlay identifier
     */
    @NonNull
    public OverlayIdentifier getIdentifier() {
        throw new RuntimeException("STUB");
    }

    /**
     * The builder of Fabricated Runtime Resource Overlays(FRROs).
     *
     * Fabricated overlays are enabled, disabled, and reordered just like normal overlays. The
     * overlayable policies a fabricated overlay fulfills are the same policies the creator of the
     * overlay fulfill. For example, a fabricated overlay created by a platform signed package on
     * the system partition would fulfil the {@code system} and {@code signature} policies.
     *
     * The owner of a fabricated overlay is the UID that created it. Overlays commit to the overlay
     * manager persist across reboots. When the UID is uninstalled, its fabricated overlays are
     * wiped.
     *
     * Processes with {@code android.Manifest.permission#CHANGE_OVERLAY_PACKAGES} can manage normal
     * overlays and fabricated overlays.
     *
     * @see FabricatedOverlay
     * @see OverlayManagerTransaction.Builder#registerFabricatedOverlay(FabricatedOverlay)
     * @hide
     */
    public static final class Builder {
        private final String mOwningPackage;
        private final String mName;
        private final String mTargetPackage;
        private String mTargetOverlayable = "";
        private final ArrayList<FabricatedOverlayInternalEntry> mEntries = new ArrayList<>();

        /**
         * Constructs a build for a fabricated overlay.
         *
         * @param owningPackage the name of the package that owns the fabricated overlay (must
         *                      be a package name of this UID).
         * @param name a name used to uniquely identify the fabricated overlay owned by
         *             {@param owningPackageName}
         * @param targetPackage the name of the package to overlay
         */
        public Builder(@NonNull String owningPackage, @NonNull String name,
                @NonNull String targetPackage) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the name of the target overlayable to be overlaid.
         *
         * <p>The target package defines may define several overlayables. The
         * {@link FabricatedOverlay} should specify which overlayable to be overlaid.
         *
         * <p>The target overlayable should be defined in {@code <overlayable>} and pass the value
         * of its {@code name} attribute as the parameter.
         *
         * @param targetOverlayable is a name of the overlayable resources set
         * @hide
         */
        @NonNull
        public Builder setTargetOverlayable(@Nullable String targetOverlayable) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the integer-like types.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param dataType the data type of the new value
         * @param value the unsigned 32 bit integer representing the new value
         * @return the builder itself
         * @see #setResourceValue(String, int, int, String)
         * @see android.util.TypedValue#TYPE_INT_COLOR_ARGB8 android.util.TypedValue#type
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String, int,
                       int, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @IntRange(from = TypedValue.TYPE_FIRST_INT, to = TypedValue.TYPE_LAST_INT)
                        int dataType,
                int value) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the integer-like types with the
         * configuration.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param dataType the data type of the new value
         * @param value the unsigned 32 bit integer representing the new value
         * @param configuration The string representation of the config this overlay is enabled for
         * @return the builder itself
         * @see android.util.TypedValue#TYPE_INT_COLOR_ARGB8 android.util.TypedValue#type
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String, int,
                       int, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @IntRange(from = TypedValue.TYPE_FIRST_INT, to = TypedValue.TYPE_LAST_INT)
                        int dataType,
                int value,
                @Nullable String configuration) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the string-like type.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param dataType the data type of the new value
         * @param value the string representing the new value
         * @return the builder itself
         * @see android.util.TypedValue#TYPE_STRING android.util.TypedValue#type
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String, int,
                       String, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @StringTypeOverlayResource int dataType,
                @NonNull String value) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the string-like type with the configuration.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param dataType the data type of the new value
         * @param value the string representing the new value
         * @param configuration The string representation of the config this overlay is enabled for
         * @return the builder itself
         * @see android.util.TypedValue#TYPE_STRING android.util.TypedValue#type
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String, int,
                       String, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @StringTypeOverlayResource int dataType,
                @NonNull String value,
                @Nullable String configuration) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the file descriptor type.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param value the file descriptor whose contents are the value of the frro
         * @param configuration The string representation of the config this overlay is enabled for
         * @return the builder itself
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String,
                ParcelFileDescriptor, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @NonNull ParcelFileDescriptor value,
                @Nullable String configuration) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the value of the fabricated overlay for the file descriptor type.
         *
         * @param resourceName name of the target resource to overlay (in the form
         *     [package]:type/entry)
         * @param value the file descriptor whose contents are the value of the frro
         * @param configuration The string representation of the config this overlay is enabled for
         * @return the builder itself
         * @deprecated Framework should use {@link FabricatedOverlay#setResourceValue(String,
                ParcelFileDescriptor, String)} instead.
         * @hide
         */
        @Deprecated(since = "Please use FabricatedOverlay#setResourceValue instead")
        @NonNull
        public Builder setResourceValue(
                @NonNull String resourceName,
                @NonNull AssetFileDescriptor value,
                @Nullable String configuration) {
            throw new RuntimeException("STUB");
        }

        /**
         * Builds an immutable fabricated overlay.
         *
         * @return the fabricated overlay
         * @hide
         */
        @NonNull
        public FabricatedOverlay build() {
            throw new RuntimeException("STUB");
        }
    }

    /**
     * Create a fabricated overlay to overlay on the specified package.
     *
     * @param overlayName a name used to uniquely identify the fabricated overlay owned by the
     *                   caller itself.
     * @param targetPackage the name of the package to be overlaid
     */
    public FabricatedOverlay(@NonNull String overlayName, @NonNull String targetPackage) {
        throw new RuntimeException("STUB");
    }

    /**
     * Set the package that owns the overlay
     *
     * @param owningPackage the package that should own the overlay.
     * @hide
     */
    public void setOwningPackage(@NonNull String owningPackage) {
        throw new RuntimeException("STUB");
    }

    /**
     * Set the target overlayable name of the overlay
     *
     * The target package defines may define several overlayables. The {@link FabricatedOverlay}
     * should specify which overlayable to be overlaid.
     *
     * @param targetOverlayable the overlayable name defined in target package.
     */
    public void setTargetOverlayable(@Nullable String targetOverlayable) {
        throw new RuntimeException("STUB");
    }

    /**
     * Return the target overlayable name of the overlay
     *
     * The target package defines may define several overlayables. The {@link FabricatedOverlay}
     * should specify which overlayable to be overlaid.
     *
     * @return the target overlayable name.
     * @hide
     */
    @Nullable
    public String getTargetOverlayable() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the resource value in the fabricated overlay for the integer-like types with the
     * configuration.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param dataType the data type of the new value
     * @param value the integer representing the new value
     * @param configuration The string representation of the config this overlay is enabled for
     * @see android.util.TypedValue#TYPE_INT_COLOR_ARGB8 android.util.TypedValue#type
     */
    @NonNull
    public void setResourceValue(
            @NonNull String resourceName,
            @IntRange(from = TypedValue.TYPE_FIRST_INT, to = TypedValue.TYPE_LAST_INT) int dataType,
            int value,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }

    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    public @interface StringTypeOverlayResource {}

    /**
     * Sets the resource value in the fabricated overlay for the string-like type with the
     * configuration.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param dataType the data type of the new value
     * @param value the string representing the new value
     * @param configuration The string representation of the config this overlay is enabled for
     * @see android.util.TypedValue#TYPE_STRING android.util.TypedValue#type
     */
    @NonNull
    public void setResourceValue(
            @NonNull String resourceName,
            @StringTypeOverlayResource int dataType,
            @NonNull String value,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the resource value in the fabricated overlay for the file descriptor type with the
     * configuration.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param value the file descriptor whose contents are the value of the frro
     * @param configuration The string representation of the config this overlay is enabled for
     */
    @NonNull
    public void setResourceValue(
            @NonNull String resourceName,
            @NonNull ParcelFileDescriptor value,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the resource value in the fabricated overlay from a nine patch.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param value the file descriptor whose contents are the value of the frro
     * @param configuration The string representation of the config this overlay is enabled for
     */
    @NonNull
    public void setNinePatchResourceValue(
            @NonNull String resourceName,
            @NonNull ParcelFileDescriptor value,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the resource value in the fabricated overlay for the file descriptor type with the
     * configuration.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param value the file descriptor whose contents are the value of the frro
     * @param configuration The string representation of the config this overlay is enabled for
     */
    @NonNull
    public void setResourceValue(
            @NonNull String resourceName,
            @NonNull AssetFileDescriptor value,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the resource value in the fabricated overlay for the dimension type with the
     * configuration.
     *
     * @param resourceName name of the target resource to overlay (in the form
     *     [package]:type/entry)
     * @param dimensionValue the float representing the dimension value
     * @param dimensionUnit the integer representing the dimension unit
     * @param configuration The string representation of the config this overlay is enabled for
     */
    public void setResourceValue(
            @NonNull String resourceName,
            float dimensionValue,
            int dimensionUnit,
            @Nullable String configuration) {
        throw new RuntimeException("STUB");
    }
}
