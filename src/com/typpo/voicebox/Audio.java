package com.typpo.voicebox;

import java.io.IOException;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Audio {
	private MediaRecorder mRecorder;

	// private AudioRecord mAudio;

	public Audio() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	}

	public String StartRecording() {
		java.util.Date date = new java.util.Date();
		// TODO human readable date
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/voicebox" + date.getTime() + ".3gp";
		mRecorder.setOutputFile(path);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("AudioRecorder", "prepare() failed");
		}

		mRecorder.start();
		return path;

		/*
		 * int sampleRate = AudioTrack
		 * .getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM); int
		 * bufferSize = AudioRecord.getMinBufferSize(sampleRate,
		 * AudioFormat.CHANNEL_CONFIGURATION_MONO,
		 * AudioFormat.ENCODING_PCM_16BIT);
		 * 
		 * AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
		 * 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT,
		 * bufferSize);
		 * 
		 * recorder.startRecording();
		 */
	}

	public void StopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
}
