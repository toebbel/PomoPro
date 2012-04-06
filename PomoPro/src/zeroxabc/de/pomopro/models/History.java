package zeroxabc.de.pomopro.models;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class History {
	protected List<PomodoroEvent> history;
	protected PomodoroEvent currentEvent;
	private static final String HISTORY_FILE = "history";
	private static final String DEBUG_TAG = "History";
	private static History _instance;
	private static Context _c;
	
	private History() {
		history = new ArrayList<PomodoroEvent>();
	}
	
	public static void init(Context c) {
		if(_c == null)
			_c = c;
		_instance = new History();
	}
	
	public static History getInstance() {
		assert _instance != null;
		return _instance;
	}
	
	public void addEvent(PomodoroEvent e) {
		currentEvent = e;
		history.add(e);
		java.util.Collections.sort(history);
		while(history.size() > 10)
			history.remove(history.size() - 1);
	}
	
	public PomodoroEvent getCurrentEvent() {
		return currentEvent;
	}
	
	public boolean clear() {
		currentEvent = null;
		history = new ArrayList<PomodoroEvent>();
		try {
			saveHistoryToFile();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public PomodoroEvent[] getEvents() {
		PomodoroEvent[] result = new PomodoroEvent[history.size()];
		history.toArray(result);
		return result;
	}
	
	private void restoreHistoryFromfile() {
		debug("restore history from file");
		try {
			InputStream file = _c.openFileInput(HISTORY_FILE);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			history = (ArrayList<PomodoroEvent>) input.readObject();
			input.close();
		} catch (Exception e) {
			debug("failed to restore from file: " + e.getMessage() + " | "
					+ e.getCause() + " | " + e.getStackTrace());
			history = new ArrayList<PomodoroEvent>();
		}
	}

	private void saveHistoryToFile() throws IOException {
		debug("save History to file");
		try {
			FileOutputStream fos = _c.openFileOutput(HISTORY_FILE,
					Context.MODE_PRIVATE);
			ObjectOutput output = new ObjectOutputStream(fos);
			output.writeObject(history);
			fos.close();
			debug("saved");
		} catch (IOException e) { // Bad Practice <- gonna catch'em all! and do
									// nothing :-/
		}
	}
	
	private void debug(String d) {
		android.util.Log.d(DEBUG_TAG, d);
	}
}
