
    1.ProviderCall.call根据auth调用定制的Provider返回bundle
    2.使用ProviderCall.getBinder获取IBinder
    3.Provider例子：
        <provider
            android:name=".data.HostProvider"
            android:authorities="com.meituan.android.retail.agreement.agreementbase.IHostInterface"
            android:exported="true" />

    public class HostProvider extends ContentProvider {
        @Override
        public boolean onCreate() {
            return true;
        }

        @Nullable
        @Override
        public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
            return null;
        }

        @Nullable
        @Override
        public String getType(@NonNull Uri uri) {
            return null;
        }

        @Nullable
        @Override
        public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
            return null;
        }

        @Override
        public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
            return 0;
        }

        @Override
        public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
            return 0;
        }

        @Override
        public Bundle call(String method, String arg, Bundle extras) {
            if ("@".equals(method)) {
                Bundle bundle = new Bundle();
                BundleCompat.putBinder(bundle, ProviderCall.BINDER_KEY, DataServer.getInstance());
                return bundle;
            }

            return null;
        }
    }