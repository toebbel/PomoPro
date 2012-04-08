package de.zeroxabc.pomopro.models;

import java.io.Serializable;
import java.util.Date;

import de.zeroxabc.pomopro.R;
import de.zeroxabc.pomopro.R.string;



public class PomodoroEvent implements Comparable<PomodoroEvent>, Serializable {

	private static final long serialVersionUID = 1770433016570143156L;
	private PomodoroEventType _type;
	private PomodoroEventState _state;
	private Date _start;
	private Date _end;
	private long _plannedDuration; //in ms
	private long _remaining; //in ms
	private boolean _vibrate;

	public PomodoroEvent(PomodoroEventType t, long duration, boolean vibrate) {
		_state = PomodoroEventState.PLANNED;
		_type = t;
		_start = java.util.GregorianCalendar.getInstance().getTime();
		_vibrate = vibrate;
		setPlannedDuration(duration);
		setRemaining(duration);
	}
	
	public void start() {
		_start = java.util.GregorianCalendar.getInstance().getTime();
		_state = PomodoroEventState.RUNNING;
	}

	
	public PomodoroEventType getType() {
		return _type;
	}

	public PomodoroEventState getState() {
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
	
	public long getPlannedDuration() {
		return _plannedDuration;
	}

	public void setPlannedDuration(long _plannedDuration) {
		this._plannedDuration = _plannedDuration;
	}
	
	public void setVibrate(boolean v){
		this._vibrate = v;
	}
	
	public boolean getVibrate() {
		return _vibrate;
	}
	
	public long getRemaining() {
		return _remaining;
	}
	
	public void setRemaining(long v) {
		assert v >= 0;
		_remaining = v;
	}

	public void finish() {
		_end = java.util.GregorianCalendar.getInstance().getTime();
		_state = PomodoroEventState.FINISHED;
	}

	public void cancel() {
		_end = java.util.GregorianCalendar.getInstance().getTime();
		_state = PomodoroEventState.ABORTED;
	}

	public enum PomodoroEventType {
		POMODORO, SHORT_BREAK, LONG_BREAK
	}

	public enum PomodoroEventState {
		RUNNING, FINISHED, ABORTED, PLANNED
	}

	public int compareTo(PomodoroEvent arg0) {
		return arg0.getStartDate().compareTo(getStartDate());
	}
	
	/**
	 * Converts a given EntryType to the localized string
	 * @param t type to convert
	 * @return type as localized string or 'unknown type'
	 */
	public String toString() {
		String isAborted = _state == PomodoroEventState.ABORTED ? " (aborted)" : "";
		
		switch (_type) {
		case POMODORO:
			return ResourceExpose.getR().getString(R.string.pomodoro) + isAborted;
		case LONG_BREAK:
			return ResourceExpose.getR().getString(R.string.longBreak) + isAborted;
		case SHORT_BREAK:
			return ResourceExpose.getR().getString(R.string.shortBreak) + isAborted;
		}
		assert false;
		return "unknown event";
	}
}