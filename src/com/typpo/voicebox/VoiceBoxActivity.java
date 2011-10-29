package com.typpo.voicebox;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class VoiceBoxActivity extends Activity {

	private DropboxAPI<AndroidAuthSession> mDBApi;
	private Audio mAudio;
	private boolean mRecording;
	private String mLastFilePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRecording = false;
		Init();
		setContentView(R.layout.main);
	}

	public void Init() {
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
	}

	public void MainButtonClick(View v) {
		if (!mDBApi.getSession().isLinked()) {
			toast("Please link your Dropbox account first.");
			Authenticate(this);
			return;
		}

		if (!mRecording) {
			toast("Starting recording");
			mAudio = new Audio();
			mLastFilePath = mAudio.StartRecording();
			mRecording = true;

			((Button) v).setText("Stop Recording");
		} else {
			toast("Stopping recording");
			mAudio.StopRecording();
			mRecording = false;
			Upload("/", mLastFilePath);

			((Button) v).setText("Start Recording");
		}
	}

	public void AuthenticateButtonClick(View v) {
		Authenticate(this);
	}

	public void Authenticate(Activity a) {
		mDBApi.getSession().startAuthentication(a);

	}

	public void Upload(String filename, String path) {
		toast("Uploading " + filename);

		Uploader u = new Uploader(this, mDBApi, filename, new File(path));
		u.execute();
		/*
		 * File f = new File(path);
		 * 
		 * String fileContents = "Hello World!"; ByteArrayInputStream
		 * inputStream = new ByteArrayInputStream( fileContents.getBytes()); try
		 * { Entry newEntry = mDBApi.putFile("/testing.txt", inputStream,
		 * fileContents.length(), null, null); Log .i("DbExampleLog",
		 * "The uploaded file's rev is: " + newEntry.rev); } catch
		 * (DropboxUnlinkedException e) { // User has unlinked, ask them to link
		 * again here. Log.e("DbExampleLog", "User has unlinked."); } catch
		 * (DropboxException e) {
		 * toast("Something went wrong while uploading :("); }
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// MANDATORY call to complete auth.
				// Sets the access token on the session
				mDBApi.getSession().finishAuthentication();

				AccessTokenPair tokens = mDBApi.getSession()
						.getAccessTokenPair();

				// Provide your own storeKeys to persist the access token pair
				// A typical way to store tokens is using SharedPreferences
				storeKeys(tokens.key, tokens.secret);
			} catch (IllegalStateException e) {
				toast("There was a problem authenticating your account.");
			}
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(
				Constants.ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(Constants.ACCESS_KEY_NAME, key);
		edit.putString(Constants.ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(
				Constants.ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(Constants.ACCESS_KEY_NAME, null);
		String secret = prefs.getString(Constants.ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(Constants.APP_KEY,
				Constants.APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, Constants.ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, Constants.ACCESS_TYPE);
		}

		return session;
	}

	private void toast(String msg) {
		Toast e = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		e.show();
	}

}