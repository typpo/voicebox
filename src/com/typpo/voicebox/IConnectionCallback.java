package com.typpo.voicebox;

public interface IConnectionCallback {

	public abstract void StateChanged(ConnectionState current,
			ConnectionState previous);
}
