package zeroxabc.de.pomopro;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import zeroxabc.de.pomopro.HistoryEntry.HistoryEntryType;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/*
 * http://openclipart.org/detail/26082/green-checkmark-and-red-minus-by-anselmus-26082
 * http://openclipart.org/detail/72037/tomato-line-art-by-horse50
 */
public class PomoProActivity extends Activity implements OnClickListener {

	protected Button btnPomoStart;
	protected Button btnLongBreakStart;
	protected Button btnShortBreakStart;
	protected ListView lstHistory;
	protected List<HistoryEntry> history;
	protected HistoryEntry currentActivity;
	public static final String PREFS_NAME = "PomoProSettings";
	private static final String HISTORY_FILE = "history";
	private static final String DEBUG_TAG = "PomoProMainActivity";
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("onCreate");
		setContentView(R.layout.main);

		settings = getSharedPreferences(PREFS_NAME, 0);

		btnPomoStart = (Button) findViewById(R.id.btnStartPomo);
		btnPomoStart.setOnClickListener(this);
		btnLongBreakStart = (Button) findViewById(R.id.btnLongBreak);
		btnLongBreakStart.setOnClickListener(this);
		btnShortBreakStart = (Button) findViewById(R.id.btnShortBreak);
		btnShortBreakStart.setOnClickListener(this);
		lstHistory = (ListView) findViewById(R.id.lstHistory);

		refreshHistory();
		
		//schedule history refresh every 30 secs
		new CountDownTimer(30000, 30000) {
			@Override
			public void onFinish() {
				refreshHistory();
				this.start();
			}

			@Override
			public void onTick(long millisUntilFinished) {}
		}.start();
	}

	public void onClick(View arg0) {
		debug("onclick: " + arg0);
		
		if (btnPomoStart.equals(arg0) || btnShortBreakStart.equals(arg0) || btnLongBreakStart.equals(arg0)) {
			Intent intent = new Intent(this, PomoTimer.class);
			//intent.setAction("pomodoro");
			if(btnShortBreakStart.equals(arg0)) {
				debug("start short break");
				intent.putExtra("duration", 5 * 60);
				intent.putExtra("name", getString(R.string.shortBreak));
				currentActivity = new HistoryEntry(HistoryEntryType.SHORT_BREAK);
			} else if (btnLongBreakStart.equals(arg0)) {
				debug("start long break");
				intent.putExtra("duration", 20 * 60);
				intent.putExtra("name", getString(R.string.longBreak));
				currentActivity = new HistoryEntry(HistoryEntryType.LONG_BREAK);
			} else {
				debug("start pomodoro");
				intent.putExtra("duration", 25 * 60);
				intent.putExtra("name", getString(R.string.pomodoro));
				currentActivity = new HistoryEntry(HistoryEntryType.POMODORO);
			}
			
			history.add(currentActivity);
			debug("start pomotimer");
			startActivityForResult(intent, 1);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.layout.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.quit:
	        	System.exit(0);
	            return true;
	        case R.id.help:
	        	Uri uriUrl = Uri.parse("http://bit.ly/pomopro");
	        	Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	        	startActivity(launchBrowser); 
	            return true;
	        case R.id.remove:
	        	history = new ArrayList<HistoryEntry>();
	        	refreshHistory();
	            return true;
//	        case R.id.settings:
//	            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show(); 
//	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debug("result...");
		if (requestCode == 1 && currentActivity != null) {
			debug("... from timer");
			if (resultCode == Activity.RESULT_OK) {
				currentActivity.finish();
				debug("user activity finished");
			} else {
				currentActivity.cancel();
				debug("user activity canceled");
			}
			currentActivity = null;
			refreshHistory();
		}
	}

	private void refreshHistory() {
		debug("refresh History");
		if (history == null) {
			restoreHistoryFromfile();
			/*history = new ArrayList<HistoryEntry>();
			Date now = new java.util.GregorianCalendar().getTime();
			history.add(new HistoryEntry(HistoryEntryType.POMODORO));
			history.get(0).finish();
			history.get(0).setStartDate(new Date(now.getTime() - 1000 * 30));
			history.get(0).setEndDate(new Date(now.getTime() - 1000 * 30));*/
		}

		java.util.Collections.sort(history);
		while (history.size() > 10) {
			history.remove(history.size() - 1);
		}

		try {
			saveHistoryToFile();
		} catch (IOException e) {
			debug("exception while saving file" + e.getMessage() + " | " + e.getCause() + " | " + e.getStackTrace().toString());
		}

		HistoryEntry[] contents = new HistoryEntry[history.size()];
		ArrayAdapter<HistoryEntry> adapter = new HistoryAdapter(this,
				history.toArray(contents));
		lstHistory.setAdapter(adapter);
		debug("finisehd refreshing");
	}

	private void restoreHistoryFromfile() {
		debug("restore history from file");
		try {
			InputStream file = openFileInput(HISTORY_FILE);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			history = (ArrayList<HistoryEntry>) input.readObject();
			input.close();
		} catch (Exception e) {
			debug("failed to restore from file: "+   e.getMessage() + " | " + e.getCause() + " | " + e.getStackTrace());
			history = new ArrayList<HistoryEntry>();
		}
	}

	private void saveHistoryToFile() throws IOException {
		debug("save History to file");
		// try {
		FileOutputStream fos = openFileOutput(HISTORY_FILE,
				Context.MODE_PRIVATE);
		ObjectOutput output = new ObjectOutputStream(fos);
		output.writeObject(history);
		fos.close();
		debug("saved");
		// } catch (IOException e) { //Bad Practice <- gonna catch'em all! and
		// do nothing :-/

		// }
	}
	
	private void debug(String d) {
		android.util.Log.d(DEBUG_TAG, d);
	}
}
