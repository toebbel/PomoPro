package zeroxabc.de.pomopro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PomoTimer extends Activity {

	private static final String DEBUG_TAG = "PomoProTimer";
	protected TextView txtName;
	protected TextView txtRemaining;
	protected PomodoroEvent event;
	protected long remaining;
	
	CountDownTimer myTimer;
	PowerManager.WakeLock wakelock;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.layout.time_menu, menu);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("onCreate");
		setContentView(R.layout.pomotimer);
		
		txtName = (TextView) findViewById(R.id.txtActionName);
		txtRemaining = (TextView) findViewById(R.id.txtTimeRemaining);
		
		debug("aquire wake lock");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PomoPro");
        wakelock.acquire();
		
        debug("init gui");
        assert getIntent().getExtras().get("event").getClass().equals(PomodoroEvent.class);
        event = (PomodoroEvent) getIntent().getExtras().get("event");
        remaining = event.getPlannedDuration();

		txtName.setText(getString(R.string.actionLabel) + "\n" + event);
		refreshTime();
		
		debug("starting timer");
		myTimer = new CountDownTimer(event.getPlannedDuration(), 1000) {
			@Override
			public void onFinish() {
				finishedTimer(true);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				remaining = millisUntilFinished;
				refreshTime();
			}
		}.start();
		
	}
	
	private void finishedTimer(boolean success) {
		debug("finished Timer");
		myTimer.cancel();
		if(wakelock.isHeld())
			wakelock.release();
		if(success) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(new long[] {200, 40}, 2);
			
			this.setResult(RESULT_OK);
			this.finish();
		} else {
			this.setResult(RESULT_CANCELED);
			this.finish();
		}
	}
	
	private void refreshTime() {
		debug("redresh time");
		Date myDate = new Date(remaining);
		SimpleDateFormat myDF = new SimpleDateFormat("mm:ss");
		txtRemaining.setText(myDF.format(myDate));
	}
	
	
	private void debug(String d) {
		android.util.Log.d(DEBUG_TAG, d);
	}
}
