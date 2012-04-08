package de.zeroxabc.pomopro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.zeroxabc.pomopro.R;
import de.zeroxabc.pomopro.models.History;
import de.zeroxabc.pomopro.models.PomodoroEvent;
import de.zeroxabc.pomopro.models.PomodoroEvent.PomodoroEventState;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.SavedState;
import android.widget.Toast;

public class TimerActivity extends Activity {

	private static final String DEBUG_TAG = "PomoProTimer";
	protected TextView txtRemaining;
	protected PomodoroEvent event;
	
	CountDownTimer myTimer;
	PowerManager.WakeLock wakelock;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.layout.timer_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.cancelTimer:
	        	finishedTimer(false);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstances) {
		super.onCreate(savedInstances);
		debug("onCreate");
		
		if(History.getInstance().getCurrentEvent().getState() == PomodoroEvent.PomodoroEventState.PLANNED) //onStart is called when device is rotated -> ignore method call, if there is a counting timer
			History.getInstance().getCurrentEvent().start();
		
		setContentView(R.layout.timer);
	
		txtRemaining = (TextView) findViewById(R.id.txtTimeRemaining);
		
		debug("aquire wake lock");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PomoPro");
        wakelock.acquire();
		
        debug("init gui");
        
        event = History.getInstance().getCurrentEvent();

		refreshTime();
		
		debug("starting timer");
		myTimer = new CountDownTimer(event.getRemaining(), 1000) {
			@Override
			public void onFinish() {
				finishedTimer(true);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				event.setRemaining(millisUntilFinished);
				refreshTime();
			}
		}.start();
		
	}
	
	private void finishedTimer(boolean success) {
		debug("finished Timer");
		
		myTimer.cancel();
		if(success) {
			if(event.getVibrate()) {
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(new long[] {0, 200, 100, 200}, -1);
			}
			event.finish();
			this.setResult(RESULT_OK);
		} else {
			event.cancel();
			this.setResult(RESULT_CANCELED);
		}
		if(wakelock != null && wakelock.isHeld()) {
			wakelock.release();
		}
		this.finish();
	}
	
	@Override
	public void onDestroy() {
		if(myTimer != null)
			myTimer.cancel();
		super.onDestroy();
	}
	
	
	private void refreshTime() {
		debug("refreshTime");
		Date myDate = new Date(event.getRemaining());
		SimpleDateFormat myDF = new SimpleDateFormat("mm:ss");
		txtRemaining.setText(myDF.format(myDate));
	}
	
	
	private void debug(String d) {
		android.util.Log.d(DEBUG_TAG, d);
	}
}
