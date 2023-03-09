package com.example.myday.applock;

import android.app.Application;

import com.example.myday.applock.core.LockManager;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LockManager.getInstance().enableAppLock(this);
	}

}
