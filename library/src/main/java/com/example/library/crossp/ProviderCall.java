package com.example.library.crossp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.BundleCompat;

import java.io.Serializable;

import static java.lang.System.exit;

/**
 * @author cd
 *
 */
public class ProviderCall {
    public static String BINDER_KEY = "_MT_|_binder_";

	public static Bundle call(Context context, String authority,  String method, String arg, Bundle bundle) {
		Uri uri = Uri.parse("content://" + authority);
		return ContentProviderCompat.call(context, uri, method, arg, bundle);
	}

	public static IBinder getBinder(Bundle bundle) {
		if (bundle == null) {
			return null;
		}

		IBinder binder = BundleCompat.getBinder(bundle, BINDER_KEY);
		if (binder != null) {
			linkBinderDied(binder);
		}

		return binder;
	}

	public static void linkBinderDied(final IBinder binder) {
		IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
			@Override
			public void binderDied() {
				binder.unlinkToDeath(this, 0);
				exit(0);
			}
		};
		try {
			binder.linkToDeath(deathRecipient, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static final class Builder {

		private Context context;

		private Bundle bundle = new Bundle();

		private String method;
		private String auth;
		private String arg;

		public Builder(Context context, String auth) {
			this.context = context;
			this.auth = auth;
		}

		public Builder methodName(String name) {
			this.method = name;
			return this;
		}

		public Builder arg(String arg) {
			this.arg = arg;
			return this;
		}

		public Builder addArg(String key, Object value) {
			if (value != null) {
				if (value instanceof Boolean) {
					bundle.putBoolean(key, (Boolean) value);
				} else if (value instanceof Integer) {
					bundle.putInt(key, (Integer) value);
				} else if (value instanceof String) {
					bundle.putString(key, (String) value);
				} else if (value instanceof Serializable) {
					bundle.putSerializable(key, (Serializable) value);
				} else if (value instanceof Bundle) {
					bundle.putBundle(key, (Bundle) value);
				} else if (value instanceof Parcelable) {
					bundle.putParcelable(key, (Parcelable) value);
				} else if (value instanceof IBinder) {
					bundle.putBinder(key, (IBinder) value);
				} else {
					throw new IllegalArgumentException("Unknown type " + value.getClass() + " in Bundle.");
				}
			}
			return this;
		}

		public Bundle call() {
			return ProviderCall.call(context, auth, method, arg, bundle);
		}

	}

}
