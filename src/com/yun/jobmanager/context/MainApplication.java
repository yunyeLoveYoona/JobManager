package com.yun.jobmanager.context;

import android.app.Application;

public class MainApplication extends Application{
	private static MainApplication mainApplication;
	public static MainApplication getAppInstance(){
		if(mainApplication==null){
			mainApplication=new MainApplication();
		}
		return mainApplication;
	}

}
