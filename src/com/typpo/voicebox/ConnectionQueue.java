package com.typpo.voicebox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.typpo.voicebox.ConnectionState;

public class ConnectionQueue {

	private BroadcastReceiver mConnReceiver;
	private ConnectionState mCurrentState;
	private IConnectionCallback mCallback;

	public ConnectionQueue(Activity a, IConnectionCallback cb) {
		mCurrentState = ConnectionState.DISCONNECTED;

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

				/*
				 * String reason = intent
				 * .getStringExtra(ConnectivityManager.EXTRA_REASON);
				 * 
				 * NetworkInfo currentNetworkInfo = (NetworkInfo) intent
				 * .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				 * NetworkInfo otherNetworkInfo = (NetworkInfo) intent
				 * .getParcelableExtra
				 * (ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
				 */
			}
		};

		a.registerReceiver(mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public ConnectionState getState() {
		return mCurrentState;
	}

}
