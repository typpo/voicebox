package com.typpo.voicebox;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Audio {
	private MediaRecorder mRecorder;

	public Audio() {

	}

	public void Start() {
		File fd = Environment.getExternalStorageDirectory();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		java.util.Date date = new java.util.Date();
		// TODO human readable date
		mRecorder.setOutputFile(fd.getAbsolutePath() + "/voicebox"
				+ date.getTime() + ".3gp");
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("AudioRecorder", "prepare() failed");
		}

		mRecorder.start();
	}

	public void Stop() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
}
