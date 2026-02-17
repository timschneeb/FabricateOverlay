/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: out/host/linux-x86/bin/aidl -dout/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/gen/aidl/frameworks/base/core/java/android/content/om/IOverlayManager.aidl.d -Iframeworks/base/core/java -Iframeworks/base/drm/java -Iframeworks/base/graphics/java -Iframeworks/base/identity/java -Iframeworks/base/keystore/java -Iframeworks/base/media/java -Iframeworks/base/media/mca/effect/java -Iframeworks/base/media/mca/filterfw/java -Iframeworks/base/media/mca/filterpacks/java -Iframeworks/base/mms/java -Iframeworks/base/opengl/java -Iframeworks/base/rs/java -Iframeworks/base/sax/java -Iframeworks/base/telecomm/java -Iframeworks/base/telephony/java -Iframeworks/base/media/aidl -Iframeworks/base/core/java -Iframeworks/base/drm/java -Iframeworks/base/graphics/java -Iframeworks/base/identity/java -Iframeworks/base/keystore/java -Iframeworks/base/media/java -Iframeworks/base/media/mca/effect/java -Iframeworks/base/media/mca/filterfw/java -Iframeworks/base/media/mca/filterpacks/java -Iframeworks/base/mms/java -Iframeworks/base/opengl/java -Iframeworks/base/rs/java -Iframeworks/base/sax/java -Iframeworks/base/telecomm/java -Iframeworks/base/telephony/java -Iframeworks/av/aidl -Iframeworks/native/libs/permission/aidl -Ipackages/modules/Bluetooth/framework/aidl-export -Ipackages/modules/Connectivity/framework/aidl-export -Ipackages/modules/Media/apex/aidl/stable -Ihardware/interfaces/biometrics/common/aidl -Ihardware/interfaces/biometrics/fingerprint/aidl -Ihardware/interfaces/common/aidl -Ihardware/interfaces/common/fmq/aidl -Ihardware/interfaces/graphics/common/aidl -Ihardware/interfaces/keymaster/aidl -Ihardware/interfaces/power/aidl -Isystem/hardware/interfaces/media/aidl -Iframeworks/base -Iframeworks/base/apex/blobstore/framework/java -Iframeworks/base/nfc-non-updatable/java -Iframeworks/base/apex/jobscheduler/framework/java -Iframeworks/base/wifi/java/src -Isystem/connectivity/wificond/aidl -Iframeworks/base/packages/services/PacProcessor/src -Iframeworks/base/packages/services/Proxy/src -Iframeworks/base/native/android/aidl -Isystem/security/identity/binder -Iframeworks/native/cmds/dumpstate/binder -Iframeworks/native/aidl/binder -Iframeworks/native/aidl/gui -Isystem/core/gatekeeperd/binder -Isystem/gsid/aidl -Iframeworks/native/libs/gui -Iframeworks/base/cmds/idmap2/idmap2d/aidl/services -Iframeworks/base/cmds/idmap2/idmap2d/aidl/core -Iframeworks/native/libs/incidentcompanion/binder -Iframeworks/native/libs/input -Iframeworks/native/cmds/installd/binder -Iframeworks/av/media/libaudioclient/aidl -Iframeworks/native/libs/binder/aidl -Iframeworks/av/camera/aidl -Isystem/update_engine/binder_bindings -Isystem/logging/logd/binder -Iframeworks/av/services/mediaresourcemanager/aidl -Isystem/core/storaged/binder -Isystem/vold/binder -Iframeworks/native/aidl -t --transaction_names --min_sdk_version=current frameworks/base/core/java/android/content/om/IOverlayManager.aidl out/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/gen/aidl/aidl6.tmp/frameworks/base/core/java/android/content/om/IOverlayManager.java
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package android.content.om;
/**
 * Api for getting information about overlay packages.
 * 
 * {@hide}
 */
public interface IOverlayManager extends android.os.IInterface
{
  /** Default implementation for IOverlayManager. */
  public static class Default implements android.content.om.IOverlayManager
  {
    /**
     * Returns information about all installed overlay packages for the
     * specified user. If there are no installed overlay packages for this user,
     * an empty map is returned (i.e. null is never returned). The returned map is a
     * mapping of target package names to lists of overlays. Each list for a
     * given target package is sorted in priority order, with the overlay with
     * the highest priority at the end of the list.
     * 
     * @param userId The user to get the OverlayInfos for.
     * @return A Map<String, List<OverlayInfo>> with target package names
     *         mapped to lists of overlays; if no overlays exist for the
     *         requested user, an empty map is returned.
     */
    @Override public java.util.Map<java.lang.String,java.util.List<android.content.om.OverlayInfo>> getAllOverlays(int userId) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns information about all overlays for the given target package for
     * the specified user. The returned list is ordered according to the
     * overlay priority with the highest priority at the end of the list.
     * 
     * @param targetPackageName The name of the target package.
     * @param userId The user to get the OverlayInfos for.
     * @return A list of OverlayInfo objects; if no overlays exist for the
     *         requested package, an empty list is returned.
     */
    @Override public java.util.List<android.content.om.OverlayInfo> getOverlayInfosForTarget(java.lang.String targetPackageName, int userId) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns information about the overlay with the given package name for the
     * specified user.
     * 
     * @param packageName The name of the overlay package.
     * @param userId The user to get the OverlayInfo for.
     * @return The OverlayInfo for the overlay package; or null if no such
     *         overlay package exists.
     */
    @Override public android.content.om.OverlayInfo getOverlayInfo(java.lang.String packageName, int userId) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns information about the overlay with the given package name for the
     * specified user.
     * 
     * @param packageName The name of the overlay package.
     * @param userId The user to get the OverlayInfo for.
     * @return The OverlayInfo for the overlay package; or null if no such
     *         overlay package exists.
     */
    @Override public android.content.om.OverlayInfo getOverlayInfoByIdentifier(android.content.om.OverlayIdentifier packageName, int userId) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Request that an overlay package be enabled or disabled when possible to
     * do so.
     * 
     * It is always possible to disable an overlay, but due to technical and
     * security reasons it may not always be possible to enable an overlay. An
     * example of the latter is when the related target package is not
     * installed. If the technical obstacle is later overcome, the overlay is
     * automatically enabled at that point in time.
     * 
     * An enabled overlay is a part of target package's resources, i.e. it will
     * be part of any lookups performed via {@link android.content.res.Resources}
     * and {@link android.content.res.AssetManager}. A disabled overlay will no
     * longer affect the resources of the target package. If the target is
     * currently running, its outdated resources will be replaced by new ones.
     * This happens the same way as when an application enters or exits split
     * window mode.
     * 
     * @param packageName The name of the overlay package.
     * @param enable true to enable the overlay, false to disable it.
     * @param userId The user for which to change the overlay.
     * @return true if the system successfully registered the request, false otherwise.
     */
    @Override public boolean setEnabled(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Request that an overlay package is enabled and any other overlay packages with the same
     * target package are disabled.
     * 
     * See {@link #setEnabled} for the details on overlay packages.
     * 
     * @param packageName the name of the overlay package to enable.
     * @param enable must be true, otherwise the operation fails.
     * @param userId The user for which to change the overlay.
     * @return true if the system successfully registered the request, false otherwise.
     */
    @Override public boolean setEnabledExclusive(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Request that an overlay package is enabled and any other overlay packages with the same
     * target package and category are disabled.
     * 
     * See {@link #setEnabled} for the details on overlay packages.
     * 
     * @param packageName the name of the overlay package to enable.
     * @param userId The user for which to change the overlay.
     * @return true if the system successfully registered the request, false otherwise.
     */
    @Override public boolean setEnabledExclusiveInCategory(java.lang.String packageName, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Change the priority of the given overlay to be just higher than the
     * overlay with package name newParentPackageName. Both overlay packages
     * must have the same target and user.
     * 
     * @param packageName The name of the overlay package whose priority should
     *        be adjusted.
     * @param newParentPackageName The name of the overlay package the newly
     *        adjusted overlay package should just outrank.
     * @param userId The user for which to change the overlay.
     */
    @Override public boolean setPriority(java.lang.String packageName, java.lang.String newParentPackageName, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Change the priority of the given overlay to the highest priority relative to
     * the other overlays with the same target and user.
     *
     * @param packageName The name of the overlay package whose priority should
     *        be adjusted.
     * @param userId The user for which to change the overlay.
     */
    @Override public boolean setHighestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Change the priority of the overlay to the lowest priority relative to
     * the other overlays for the same target and user.
     *
     * @param packageName The name of the overlay package whose priority should
     *        be adjusted.
     * @param userId The user for which to change the overlay.
     */
    @Override public boolean setLowestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException
    {
      return false;
    }
    /** Returns the list of default overlay packages, or an empty array if there are none. */
    @Override public java.lang.String[] getDefaultOverlayPackages() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Invalidates and removes the idmap for an overlay,
     * @param packageName The name of the overlay package whose idmap should be deleted.
     */
    @Override public void invalidateCachesForOverlay(java.lang.String packageName, int userId) throws android.os.RemoteException
    {
    }
    /**
     * Perform a series of requests related to overlay packages. This is an
     * atomic operation: either all requests were performed successfully and
     * the changes were propagated to the rest of the system, or at least one
     * request could not be performed successfully and nothing is changed and
     * nothing is propagated to the rest of the system.
     * 
     * @see OverlayManagerTransaction
     * 
     * @param transaction the series of overlay related requests to perform
     * @throws SecurityException if the transaction failed
     */
    @Override public void commit(android.content.om.OverlayManagerTransaction transaction) throws android.os.RemoteException
    {
    }
    /** Returns a String of a list of partitions from low priority to high. */
    @Override public java.lang.String getPartitionOrder() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns a boolean which represent whether the partition list is sorted by default.
     * If not then it should be sorted by /product/overlay/partition_order.xml.
     */
    @Override public boolean isDefaultPartitionOrder() throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements android.content.om.IOverlayManager
  {
    /** Construct the stub and attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an android.content.om.IOverlayManager interface,
     * generating a proxy if needed.
     */
    public static android.content.om.IOverlayManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof android.content.om.IOverlayManager))) {
        return ((android.content.om.IOverlayManager)iin);
      }
      return new android.content.om.IOverlayManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    /** @hide */
    public static java.lang.String getDefaultTransactionName(int transactionCode)
    {
      switch (transactionCode)
      {
        case TRANSACTION_getAllOverlays:
        {
          return "getAllOverlays";
        }
        case TRANSACTION_getOverlayInfosForTarget:
        {
          return "getOverlayInfosForTarget";
        }
        case TRANSACTION_getOverlayInfo:
        {
          return "getOverlayInfo";
        }
        case TRANSACTION_getOverlayInfoByIdentifier:
        {
          return "getOverlayInfoByIdentifier";
        }
        case TRANSACTION_setEnabled:
        {
          return "setEnabled";
        }
        case TRANSACTION_setEnabledExclusive:
        {
          return "setEnabledExclusive";
        }
        case TRANSACTION_setEnabledExclusiveInCategory:
        {
          return "setEnabledExclusiveInCategory";
        }
        case TRANSACTION_setPriority:
        {
          return "setPriority";
        }
        case TRANSACTION_setHighestPriority:
        {
          return "setHighestPriority";
        }
        case TRANSACTION_setLowestPriority:
        {
          return "setLowestPriority";
        }
        case TRANSACTION_getDefaultOverlayPackages:
        {
          return "getDefaultOverlayPackages";
        }
        case TRANSACTION_invalidateCachesForOverlay:
        {
          return "invalidateCachesForOverlay";
        }
        case TRANSACTION_commit:
        {
          return "commit";
        }
        case TRANSACTION_getPartitionOrder:
        {
          return "getPartitionOrder";
        }
        case TRANSACTION_isDefaultPartitionOrder:
        {
          return "isDefaultPartitionOrder";
        }
        default:
        {
          return null;
        }
      }
    }
    /** @hide */
    public java.lang.String getTransactionName(int transactionCode)
    {
      return this.getDefaultTransactionName(transactionCode);
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_getAllOverlays:
        {
          int _arg0;
          _arg0 = data.readInt();
          data.enforceNoDataAvail();
          java.util.Map<java.lang.String,java.util.List<android.content.om.OverlayInfo>> _result = this.getAllOverlays(_arg0);
          reply.writeNoException();
          if (_result == null) {
            reply.writeInt(-1);
          } else {
            reply.writeInt(_result.size());
            _result.forEach((k, v) -> {
              reply.writeString(k);
              reply.writeTypedList(v, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            });
          }
          break;
        }
        case TRANSACTION_getOverlayInfosForTarget:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          java.util.List<android.content.om.OverlayInfo> _result = this.getOverlayInfosForTarget(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getOverlayInfo:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          android.content.om.OverlayInfo _result = this.getOverlayInfo(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedObject(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getOverlayInfoByIdentifier:
        {
          android.content.om.OverlayIdentifier _arg0;
          _arg0 = data.readTypedObject(android.content.om.OverlayIdentifier.CREATOR);
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          android.content.om.OverlayInfo _result = this.getOverlayInfoByIdentifier(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedObject(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_setEnabled:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = data.readBoolean();
          int _arg2;
          _arg2 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setEnabled(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_setEnabledExclusive:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = data.readBoolean();
          int _arg2;
          _arg2 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setEnabledExclusive(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_setEnabledExclusiveInCategory:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setEnabledExclusiveInCategory(_arg0, _arg1);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_setPriority:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _arg2;
          _arg2 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setPriority(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_setHighestPriority:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setHighestPriority(_arg0, _arg1);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_setLowestPriority:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          boolean _result = this.setLowestPriority(_arg0, _arg1);
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        case TRANSACTION_getDefaultOverlayPackages:
        {
          java.lang.String[] _result = this.getDefaultOverlayPackages();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_invalidateCachesForOverlay:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          data.enforceNoDataAvail();
          this.invalidateCachesForOverlay(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_commit:
        {
          android.content.om.OverlayManagerTransaction _arg0;
          _arg0 = data.readTypedObject(android.content.om.OverlayManagerTransaction.CREATOR);
          data.enforceNoDataAvail();
          this.commit(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getPartitionOrder:
        {
          java.lang.String _result = this.getPartitionOrder();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isDefaultPartitionOrder:
        {
          boolean _result = this.isDefaultPartitionOrder();
          reply.writeNoException();
          reply.writeBoolean(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements android.content.om.IOverlayManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
       * Returns information about all installed overlay packages for the
       * specified user. If there are no installed overlay packages for this user,
       * an empty map is returned (i.e. null is never returned). The returned map is a
       * mapping of target package names to lists of overlays. Each list for a
       * given target package is sorted in priority order, with the overlay with
       * the highest priority at the end of the list.
       * 
       * @param userId The user to get the OverlayInfos for.
       * @return A Map<String, List<OverlayInfo>> with target package names
       *         mapped to lists of overlays; if no overlays exist for the
       *         requested user, an empty map is returned.
       */
      @Override public java.util.Map<java.lang.String,java.util.List<android.content.om.OverlayInfo>> getAllOverlays(int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.Map<java.lang.String,java.util.List<android.content.om.OverlayInfo>> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllOverlays, _data, _reply, 0);
          _reply.readException();
          {
            int N = _reply.readInt();
            _result = N < 0 ? null : new java.util.HashMap<>();
            java.util.stream.IntStream.range(0, N).forEach(i -> {
              String k = _reply.readString();
              java.util.List<android.content.om.OverlayInfo> v;
              v = _reply.createTypedArrayList(android.content.om.OverlayInfo.CREATOR);
              _result.put(k, v);
            });
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns information about all overlays for the given target package for
       * the specified user. The returned list is ordered according to the
       * overlay priority with the highest priority at the end of the list.
       * 
       * @param targetPackageName The name of the target package.
       * @param userId The user to get the OverlayInfos for.
       * @return A list of OverlayInfo objects; if no overlays exist for the
       *         requested package, an empty list is returned.
       */
      @Override public java.util.List<android.content.om.OverlayInfo> getOverlayInfosForTarget(java.lang.String targetPackageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<android.content.om.OverlayInfo> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(targetPackageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOverlayInfosForTarget, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(android.content.om.OverlayInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns information about the overlay with the given package name for the
       * specified user.
       * 
       * @param packageName The name of the overlay package.
       * @param userId The user to get the OverlayInfo for.
       * @return The OverlayInfo for the overlay package; or null if no such
       *         overlay package exists.
       */
      @Override public android.content.om.OverlayInfo getOverlayInfo(java.lang.String packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.content.om.OverlayInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOverlayInfo, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readTypedObject(android.content.om.OverlayInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns information about the overlay with the given package name for the
       * specified user.
       * 
       * @param packageName The name of the overlay package.
       * @param userId The user to get the OverlayInfo for.
       * @return The OverlayInfo for the overlay package; or null if no such
       *         overlay package exists.
       */
      @Override public android.content.om.OverlayInfo getOverlayInfoByIdentifier(android.content.om.OverlayIdentifier packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.content.om.OverlayInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeTypedObject(packageName, 0);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOverlayInfoByIdentifier, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readTypedObject(android.content.om.OverlayInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Request that an overlay package be enabled or disabled when possible to
       * do so.
       * 
       * It is always possible to disable an overlay, but due to technical and
       * security reasons it may not always be possible to enable an overlay. An
       * example of the latter is when the related target package is not
       * installed. If the technical obstacle is later overcome, the overlay is
       * automatically enabled at that point in time.
       * 
       * An enabled overlay is a part of target package's resources, i.e. it will
       * be part of any lookups performed via {@link android.content.res.Resources}
       * and {@link android.content.res.AssetManager}. A disabled overlay will no
       * longer affect the resources of the target package. If the target is
       * currently running, its outdated resources will be replaced by new ones.
       * This happens the same way as when an application enters or exits split
       * window mode.
       * 
       * @param packageName The name of the overlay package.
       * @param enable true to enable the overlay, false to disable it.
       * @param userId The user for which to change the overlay.
       * @return true if the system successfully registered the request, false otherwise.
       */
      @Override public boolean setEnabled(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeBoolean(enable);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setEnabled, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Request that an overlay package is enabled and any other overlay packages with the same
       * target package are disabled.
       * 
       * See {@link #setEnabled} for the details on overlay packages.
       * 
       * @param packageName the name of the overlay package to enable.
       * @param enable must be true, otherwise the operation fails.
       * @param userId The user for which to change the overlay.
       * @return true if the system successfully registered the request, false otherwise.
       */
      @Override public boolean setEnabledExclusive(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeBoolean(enable);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setEnabledExclusive, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Request that an overlay package is enabled and any other overlay packages with the same
       * target package and category are disabled.
       * 
       * See {@link #setEnabled} for the details on overlay packages.
       * 
       * @param packageName the name of the overlay package to enable.
       * @param userId The user for which to change the overlay.
       * @return true if the system successfully registered the request, false otherwise.
       */
      @Override public boolean setEnabledExclusiveInCategory(java.lang.String packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setEnabledExclusiveInCategory, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Change the priority of the given overlay to be just higher than the
       * overlay with package name newParentPackageName. Both overlay packages
       * must have the same target and user.
       * 
       * @param packageName The name of the overlay package whose priority should
       *        be adjusted.
       * @param newParentPackageName The name of the overlay package the newly
       *        adjusted overlay package should just outrank.
       * @param userId The user for which to change the overlay.
       */
      @Override public boolean setPriority(java.lang.String packageName, java.lang.String newParentPackageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(newParentPackageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPriority, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Change the priority of the given overlay to the highest priority relative to
       * the other overlays with the same target and user.
       *
       * @param packageName The name of the overlay package whose priority should
       *        be adjusted.
       * @param userId The user for which to change the overlay.
       */
      @Override public boolean setHighestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setHighestPriority, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Change the priority of the overlay to the lowest priority relative to
       * the other overlays for the same target and user.
       *
       * @param packageName The name of the overlay package whose priority should
       *        be adjusted.
       * @param userId The user for which to change the overlay.
       */
      @Override public boolean setLowestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLowestPriority, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Returns the list of default overlay packages, or an empty array if there are none. */
      @Override public java.lang.String[] getDefaultOverlayPackages() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDefaultOverlayPackages, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Invalidates and removes the idmap for an overlay,
       * @param packageName The name of the overlay package whose idmap should be deleted.
       */
      @Override public void invalidateCachesForOverlay(java.lang.String packageName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_invalidateCachesForOverlay, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * Perform a series of requests related to overlay packages. This is an
       * atomic operation: either all requests were performed successfully and
       * the changes were propagated to the rest of the system, or at least one
       * request could not be performed successfully and nothing is changed and
       * nothing is propagated to the rest of the system.
       * 
       * @see OverlayManagerTransaction
       * 
       * @param transaction the series of overlay related requests to perform
       * @throws SecurityException if the transaction failed
       */
      @Override public void commit(android.content.om.OverlayManagerTransaction transaction) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeTypedObject(transaction, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_commit, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /** Returns a String of a list of partitions from low priority to high. */
      @Override public java.lang.String getPartitionOrder() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPartitionOrder, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns a boolean which represent whether the partition list is sorted by default.
       * If not then it should be sorted by /product/overlay/partition_order.xml.
       */
      @Override public boolean isDefaultPartitionOrder() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain(asBinder());
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isDefaultPartitionOrder, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readBoolean();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    /** @hide */
    public static final java.lang.String DESCRIPTOR = "android.content.om.IOverlayManager";
    static final int TRANSACTION_getAllOverlays = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getOverlayInfosForTarget = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getOverlayInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getOverlayInfoByIdentifier = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_setEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_setEnabledExclusive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_setEnabledExclusiveInCategory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_setPriority = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_setHighestPriority = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_setLowestPriority = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_getDefaultOverlayPackages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_invalidateCachesForOverlay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_commit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_getPartitionOrder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_isDefaultPartitionOrder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    /** @hide */
    public int getMaxTransactionId()
    {
      return 14;
    }
  }
  /**
   * Returns information about all installed overlay packages for the
   * specified user. If there are no installed overlay packages for this user,
   * an empty map is returned (i.e. null is never returned). The returned map is a
   * mapping of target package names to lists of overlays. Each list for a
   * given target package is sorted in priority order, with the overlay with
   * the highest priority at the end of the list.
   * 
   * @param userId The user to get the OverlayInfos for.
   * @return A Map<String, List<OverlayInfo>> with target package names
   *         mapped to lists of overlays; if no overlays exist for the
   *         requested user, an empty map is returned.
   */
   public java.util.Map<java.lang.String,java.util.List<android.content.om.OverlayInfo>> getAllOverlays(int userId) throws android.os.RemoteException;
  /**
   * Returns information about all overlays for the given target package for
   * the specified user. The returned list is ordered according to the
   * overlay priority with the highest priority at the end of the list.
   * 
   * @param targetPackageName The name of the target package.
   * @param userId The user to get the OverlayInfos for.
   * @return A list of OverlayInfo objects; if no overlays exist for the
   *         requested package, an empty list is returned.
   */
  public java.util.List<android.content.om.OverlayInfo> getOverlayInfosForTarget(java.lang.String targetPackageName, int userId) throws android.os.RemoteException;
  /**
   * Returns information about the overlay with the given package name for the
   * specified user.
   * 
   * @param packageName The name of the overlay package.
   * @param userId The user to get the OverlayInfo for.
   * @return The OverlayInfo for the overlay package; or null if no such
   *         overlay package exists.
   */
  public android.content.om.OverlayInfo getOverlayInfo(java.lang.String packageName, int userId) throws android.os.RemoteException;
  /**
   * Returns information about the overlay with the given package name for the
   * specified user.
   * 
   * @param packageName The name of the overlay package.
   * @param userId The user to get the OverlayInfo for.
   * @return The OverlayInfo for the overlay package; or null if no such
   *         overlay package exists.
   */
  public android.content.om.OverlayInfo getOverlayInfoByIdentifier(android.content.om.OverlayIdentifier packageName, int userId) throws android.os.RemoteException;
  /**
   * Request that an overlay package be enabled or disabled when possible to
   * do so.
   * 
   * It is always possible to disable an overlay, but due to technical and
   * security reasons it may not always be possible to enable an overlay. An
   * example of the latter is when the related target package is not
   * installed. If the technical obstacle is later overcome, the overlay is
   * automatically enabled at that point in time.
   * 
   * An enabled overlay is a part of target package's resources, i.e. it will
   * be part of any lookups performed via {@link android.content.res.Resources}
   * and {@link android.content.res.AssetManager}. A disabled overlay will no
   * longer affect the resources of the target package. If the target is
   * currently running, its outdated resources will be replaced by new ones.
   * This happens the same way as when an application enters or exits split
   * window mode.
   * 
   * @param packageName The name of the overlay package.
   * @param enable true to enable the overlay, false to disable it.
   * @param userId The user for which to change the overlay.
   * @return true if the system successfully registered the request, false otherwise.
   */
  public boolean setEnabled(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException;
  /**
   * Request that an overlay package is enabled and any other overlay packages with the same
   * target package are disabled.
   * 
   * See {@link #setEnabled} for the details on overlay packages.
   * 
   * @param packageName the name of the overlay package to enable.
   * @param enable must be true, otherwise the operation fails.
   * @param userId The user for which to change the overlay.
   * @return true if the system successfully registered the request, false otherwise.
   */
  public boolean setEnabledExclusive(java.lang.String packageName, boolean enable, int userId) throws android.os.RemoteException;
  /**
   * Request that an overlay package is enabled and any other overlay packages with the same
   * target package and category are disabled.
   * 
   * See {@link #setEnabled} for the details on overlay packages.
   * 
   * @param packageName the name of the overlay package to enable.
   * @param userId The user for which to change the overlay.
   * @return true if the system successfully registered the request, false otherwise.
   */
  public boolean setEnabledExclusiveInCategory(java.lang.String packageName, int userId) throws android.os.RemoteException;
  /**
   * Change the priority of the given overlay to be just higher than the
   * overlay with package name newParentPackageName. Both overlay packages
   * must have the same target and user.
   *
   * @param packageName The name of the overlay package whose priority should
   *        be adjusted.
   * @param newParentPackageName The name of the overlay package the newly
   *        adjusted overlay package should just outrank.
   * @param userId The user for which to change the overlay.
   */
  public boolean setPriority(java.lang.String packageName, java.lang.String newParentPackageName, int userId) throws android.os.RemoteException;
  /**
   * Change the priority of the given overlay to the highest priority relative to
   * the other overlays with the same target and user.
   *
   * @param packageName The name of the overlay package whose priority should
   *        be adjusted.
   * @param userId The user for which to change the overlay.
   */
  public boolean setHighestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException;
  /**
   * Change the priority of the overlay to the lowest priority relative to
   * the other overlays for the same target and user.
   *
   * @param packageName The name of the overlay package whose priority should
   *        be adjusted.
   * @param userId The user for which to change the overlay.
   */
  public boolean setLowestPriority(java.lang.String packageName, int userId) throws android.os.RemoteException;
  /** Returns the list of default overlay packages, or an empty array if there are none. */
  public java.lang.String[] getDefaultOverlayPackages() throws android.os.RemoteException;
  /**
   * Invalidates and removes the idmap for an overlay,
   * @param packageName The name of the overlay package whose idmap should be deleted.
   */
  public void invalidateCachesForOverlay(java.lang.String packageName, int userId) throws android.os.RemoteException;
  /**
   * Perform a series of requests related to overlay packages. This is an
   * atomic operation: either all requests were performed successfully and
   * the changes were propagated to the rest of the system, or at least one
   * request could not be performed successfully and nothing is changed and
   * nothing is propagated to the rest of the system.
   * 
   * @see OverlayManagerTransaction
   * 
   * @param transaction the series of overlay related requests to perform
   * @throws SecurityException if the transaction failed
   */
  public void commit(android.content.om.OverlayManagerTransaction transaction) throws android.os.RemoteException;
  /** Returns a String of a list of partitions from low priority to high. */
  public java.lang.String getPartitionOrder() throws android.os.RemoteException;
  /**
   * Returns a boolean which represent whether the partition list is sorted by default.
   * If not then it should be sorted by /product/overlay/partition_order.xml.
   */
  public boolean isDefaultPartitionOrder() throws android.os.RemoteException;
}
