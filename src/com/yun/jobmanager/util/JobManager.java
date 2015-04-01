package com.yun.jobmanager.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;

import com.yun.jobmanager.context.MainApplication;

/**
 * 任务管理 用来管理当前不执行但可能以后会执行 或者触发某些条件后执行的任务比如广播
 * 
 * @author Administrator
 * 
 */
public class JobManager {
	private HashMap<String, Job> jobMap;
	private List<String> actionList;
	private static JobManager jobManager;
	private HandlerThread thread;
	private JobBroadcastReceiver jobBroadcastReceiver;

	private JobManager() {
		jobMap = new HashMap<>();
		actionList = new ArrayList<String>();
		thread = new HandlerThread("job");
		MainApplication.getAppInstance().getApplicationContext()
				.registerReceiver(jobBroadcastReceiver, new IntentFilter());
	}

	public JobManager getInstance() {
		if (jobManager == null) {
			jobManager = new JobManager();
		}
		return jobManager;
	}

	public void addJob(String tag, Job job) {
		if (job != null) {
			jobMap.put(tag, job);
			if (job.action != null && job.action.length() > 0) {
				MainApplication.getAppInstance().getApplicationContext()
						.unregisterReceiver(jobBroadcastReceiver);
				IntentFilter filter = new IntentFilter();
				for (String action : actionList) {
					filter.addAction(action);
				}
				filter.addAction(job.action);
				MainApplication.getAppInstance().getApplicationContext()
						.registerReceiver(jobBroadcastReceiver, filter);
			}
		}
	}

	public void remeJob(String tag) {
		Job job = jobMap.get(tag);
		if (job.action != null && job.action.length() > 0) {
			MainApplication.getAppInstance().getApplicationContext()
					.unregisterReceiver(jobBroadcastReceiver);
			IntentFilter filter = new IntentFilter();
			for (String action : actionList) {
				if (action.equals(job.action)) {
					actionList.remove(action);
				} else {
					filter.addAction(action);
				}
			}
			MainApplication.getAppInstance().getApplicationContext()
					.registerReceiver(jobBroadcastReceiver, filter);
		}
	}

	public void startJob(String tag) {
		Job job = jobMap.get(tag);
		if (job.isRunInUiTherad && job.runnable != null) {
			((Activity) MainApplication.getAppInstance()
					.getApplicationContext()).runOnUiThread(job.runnable);
		} else if (job.runnable != null) {
			Handler handler = new Handler(thread.getLooper());
			handler.postDelayed(job.runnable, job.delayMillis);
		}
		if (job.task != null) {
			job.task.execute();
		}
	}

	public class Job {
		public String action = "";
		public Runnable runnable;
		public int delayMillis = 0;
		public boolean isRunInUiTherad;
		public AsyncTask task;
	}

	private class JobBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Set<Entry<String, Job>> set = jobMap.entrySet();
			Iterator<Entry<String, Job>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Job> entry = iterator.next();
				Job job = entry.getValue();
				if (job.action.equals(intent.getAction())) {
					if (job.isRunInUiTherad && job.runnable != null) {
						((Activity) MainApplication.getAppInstance()
								.getApplicationContext())
								.runOnUiThread(job.runnable);
					} else if (job.runnable != null) {
						Handler handler = new Handler(thread.getLooper());
						handler.postDelayed(job.runnable, job.delayMillis);
					}
					if (job.task != null) {
						job.task.execute();
					}
					return;
				}
			}
		}

	}

}
