package com.typpo.voicebox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.typpo.voicebox.ConnectionState;

public class ConnectionWatcher {

	private BroadcastReceiver mConnReceiver;
	private ConnectionState mCurrentState;
	private IConnectionCallback mCallback;
	private Activity mActivity;

	public ConnectionWatcher(Activity a, IConnectionCallback cb) {
		mActivity = a;
		mCurrentState = ConnectionState.NULL;
		mCallback = cb;
	}

	public void Listen() {
		mConnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean noConnectivity = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

				boolean isFailover = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_IS_FAILOVER, false);

				ConnectionState prev = mCurrentState;
				if (noConnectivity) {
					mCurrentState = ConnectionState.DISCONNECTED;
				} else if (isFailover) {
					mCurrentState = ConnectionState.FAILOVER;
				} else {
					mCurrentState = ConnectionState.CONNECTED;
				}

				mCallback.StateChanged(mCurrentState, prev);
			}
		};

		mActivity.registerReceiver(mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public ConnectionState getState() {
		return mCurrentState;
	}

}
