/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: out/host/linux-x86/bin/aidl -dout/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/gen/aidl/frameworks/base/cmds/idmap2/idmap2d/aidl/core/android/os/FabricatedOverlayInternal.aidl.d -Iframeworks/base/core/java -Iframeworks/base/drm/java -Iframeworks/base/graphics/java -Iframeworks/base/identity/java -Iframeworks/base/keystore/java -Iframeworks/base/media/java -Iframeworks/base/media/mca/effect/java -Iframeworks/base/media/mca/filterfw/java -Iframeworks/base/media/mca/filterpacks/java -Iframeworks/base/mms/java -Iframeworks/base/opengl/java -Iframeworks/base/rs/java -Iframeworks/base/sax/java -Iframeworks/base/telecomm/java -Iframeworks/base/telephony/java -Iframeworks/base/media/aidl -Iframeworks/base/core/java -Iframeworks/base/drm/java -Iframeworks/base/graphics/java -Iframeworks/base/identity/java -Iframeworks/base/keystore/java -Iframeworks/base/media/java -Iframeworks/base/media/mca/effect/java -Iframeworks/base/media/mca/filterfw/java -Iframeworks/base/media/mca/filterpacks/java -Iframeworks/base/mms/java -Iframeworks/base/opengl/java -Iframeworks/base/rs/java -Iframeworks/base/sax/java -Iframeworks/base/telecomm/java -Iframeworks/base/telephony/java -Iframeworks/av/aidl -Iframeworks/native/libs/permission/aidl -Ipackages/modules/Bluetooth/framework/aidl-export -Ipackages/modules/Connectivity/framework/aidl-export -Ipackages/modules/Media/apex/aidl/stable -Ihardware/interfaces/biometrics/common/aidl -Ihardware/interfaces/biometrics/fingerprint/aidl -Ihardware/interfaces/common/aidl -Ihardware/interfaces/common/fmq/aidl -Ihardware/interfaces/graphics/common/aidl -Ihardware/interfaces/keymaster/aidl -Ihardware/interfaces/power/aidl -Isystem/hardware/interfaces/media/aidl -Iframeworks/base -Iframeworks/base/apex/blobstore/framework/java -Iframeworks/base/nfc-non-updatable/java -Iframeworks/base/apex/jobscheduler/framework/java -Iframeworks/base/wifi/java/src -Isystem/connectivity/wificond/aidl -Iframeworks/base/packages/services/PacProcessor/src -Iframeworks/base/packages/services/Proxy/src -Iframeworks/base/native/android/aidl -Isystem/security/identity/binder -Iframeworks/native/cmds/dumpstate/binder -Iframeworks/native/aidl/binder -Iframeworks/native/aidl/gui -Isystem/core/gatekeeperd/binder -Isystem/gsid/aidl -Iframeworks/native/libs/gui -Iframeworks/base/cmds/idmap2/idmap2d/aidl/services -Iframeworks/base/cmds/idmap2/idmap2d/aidl/core -Iframeworks/native/libs/incidentcompanion/binder -Iframeworks/native/libs/input -Iframeworks/native/cmds/installd/binder -Iframeworks/av/media/libaudioclient/aidl -Iframeworks/native/libs/binder/aidl -Iframeworks/av/camera/aidl -Isystem/update_engine/binder_bindings -Isystem/logging/logd/binder -Iframeworks/av/services/mediaresourcemanager/aidl -Isystem/core/storaged/binder -Isystem/vold/binder -Iframeworks/native/aidl -t --transaction_names --min_sdk_version=current frameworks/base/cmds/idmap2/idmap2d/aidl/core/android/os/FabricatedOverlayInternal.aidl out/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/gen/aidl/aidl43.tmp/frameworks/base/cmds/idmap2/idmap2d/aidl/core/android/os/FabricatedOverlayInternal.java
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package android.os;


public class FabricatedOverlayInternal implements android.os.Parcelable
{
  public java.lang.String packageName;
  public java.lang.String overlayName;
  public java.lang.String targetPackageName;
  public java.lang.String targetOverlayable;
  public java.util.List<android.os.FabricatedOverlayInternalEntry> entries;
  public static final android.os.Parcelable.Creator<FabricatedOverlayInternal> CREATOR = new android.os.Parcelable.Creator<FabricatedOverlayInternal>() {
    @Override
    public FabricatedOverlayInternal createFromParcel(android.os.Parcel _aidl_source) {
      FabricatedOverlayInternal _aidl_out = new FabricatedOverlayInternal();
      _aidl_out.readFromParcel(_aidl_source);
      return _aidl_out;
    }
    @Override
    public FabricatedOverlayInternal[] newArray(int _aidl_size) {
      return new FabricatedOverlayInternal[_aidl_size];
    }
  };
  @Override public final void writeToParcel(android.os.Parcel _aidl_parcel, int _aidl_flag)
  {
    int _aidl_start_pos = _aidl_parcel.dataPosition();
    _aidl_parcel.writeInt(0);
    _aidl_parcel.writeString(packageName);
    _aidl_parcel.writeString(overlayName);
    _aidl_parcel.writeString(targetPackageName);
    _aidl_parcel.writeString(targetOverlayable);
    _aidl_parcel.writeTypedList(entries, _aidl_flag);
    int _aidl_end_pos = _aidl_parcel.dataPosition();
    _aidl_parcel.setDataPosition(_aidl_start_pos);
    _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
    _aidl_parcel.setDataPosition(_aidl_end_pos);
  }
  public final void readFromParcel(android.os.Parcel _aidl_parcel)
  {
    int _aidl_start_pos = _aidl_parcel.dataPosition();
    int _aidl_parcelable_size = _aidl_parcel.readInt();
    try {
      if (_aidl_parcelable_size < 4) throw new android.os.BadParcelableException("Parcelable too small");;
      if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) return;
      packageName = _aidl_parcel.readString();
      if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) return;
      overlayName = _aidl_parcel.readString();
      if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) return;
      targetPackageName = _aidl_parcel.readString();
      if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) return;
      targetOverlayable = _aidl_parcel.readString();
      if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) return;
      entries = _aidl_parcel.createTypedArrayList(android.os.FabricatedOverlayInternalEntry.CREATOR);
    } finally {
      if (_aidl_start_pos > (Integer.MAX_VALUE - _aidl_parcelable_size)) {
        throw new android.os.BadParcelableException("Overflow in the size of parcelable");
      }
      _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
    }
  }
  @Override
  public int describeContents() {
    int _mask = 0;
    _mask |= describeContents(entries);
    return _mask;
  }
  private int describeContents(Object _v) {
    if (_v == null) return 0;
    if (_v instanceof java.util.Collection) {
      int _mask = 0;
      for (Object o : (java.util.Collection) _v) {
        _mask |= describeContents(o);
      }
      return _mask;
    }
    if (_v instanceof android.os.Parcelable) {
      return ((android.os.Parcelable) _v).describeContents();
    }
    return 0;
  }
}
