package zeroxabc.de.pomopro;

import java.io.Serializable;
import java.util.Date;


public class HistoryEntry implements Comparable<HistoryEntry>, Serializable {

	private static final long serialVersionUID = 1770433016570143156L;
	private HistoryEntryType _type;
	private HistoryEntryState _state;
	private Date _start;
	private Date _end;

	public HistoryEntry(HistoryEntryType t) {
		_state = HistoryEntryState.RUNNING;
		_type = t;
		_start = java.util.GregorianCalendar.getInstance().getTime();
	}

	public HistoryEntryType getType() {
		return _type;
	}

	public HistoryEntryState getState() {
		return _state;
	}
	
	public Date getStartDate() {
		return _start;
	}
	
	public Date getEndDate() {
		return _end;
	}
	
	public void setStartDate(Date val) {
		_start = val;
	}
	
	public void setEndDate(Date val) {
		_end = val;
	}

	public void finish() {
		_end = java.util.GregorianCalendar.getInstance().getTime();
		_state = HistoryEntryState.FINISHED;
	}

	public void cancel() {
		_end = java.util.GregorianCalendar.getInstance().getTime();
		_state = HistoryEntryState.ABORTED;
	}

	@Override
	public String toString() {
		String strType = "";
		switch (_type) {
		case POMODORO:
			strType = "pomodoro";
			break;
		case LONG_BREAK:
			strType = "long break";
			break;
		case SHORT_BREAK:
			strType = "short break";
			break;
		}
		if (_state == HistoryEntryState.ABORTED)
			return strType + " (aborted)";
		return strType;
	}

	public enum HistoryEntryType {
		POMODORO, SHORT_BREAK, LONG_BREAK
	}

	public enum HistoryEntryState {
		RUNNING, FINISHED, ABORTED
	}

	public int compareTo(HistoryEntry arg0) {
		return arg0.getStartDate().compareTo(getStartDate());
	}
}