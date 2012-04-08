package de.zeroxabc.pomopro;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.zeroxabc.pomopro.R;
import de.zeroxabc.pomopro.models.PomodoroEvent;
import de.zeroxabc.pomopro.models.PomodoroEvent.PomodoroEventState;
import de.zeroxabc.pomopro.models.PomodoroEvent.PomodoroEventType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<PomodoroEvent> {
	private static final String DEBUG_TAG = "PomoProHistoryAdapter";
	private final Context context;
	private final PomodoroEvent[] values;
	
	public HistoryAdapter(Context context, PomodoroEvent[] values) {
		super(context, R.layout.history_item, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//inflate layout
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View entry = inflater.inflate(R.layout.history_item, parent, false);
		
		//fill values
		TextView textView = (TextView) entry.findViewById(R.id.label);
		TextView txtDateInfo = (TextView) entry.findViewById(R.id.txtDateInfo);
		ImageView imageView = (ImageView) entry.findViewById(R.id.icon);
		
		if(values[position].getState() == PomodoroEventState.FINISHED)
			imageView.setImageResource(R.drawable.check);
		else if(values[position].getState() == PomodoroEventState.ABORTED)
			imageView.setImageResource(R.drawable.fail);
		
		textView.setText(values[position].toString());
		txtDateInfo.setText(getFriendlyDate(values[position].getEndDate()));
		return entry;
	}
	
	
	
	private String getFriendlyDate(Date end) {
		if(end == null)
			return "";
		Date now = new java.util.GregorianCalendar().getTime();
		Date diff = new Date(now.getTime() - end.getTime());
		long secs = diff.getTime() / 1000;
		long mins = secs / 60;
		long hours = mins / 60;
		long days = hours / 24;
		
		if(secs < 50) 
			return context.getString(R.string.friendlyTime_seconds);
		
		if (mins < 2)
			return context.getString(R.string.friendlyTime_oneMin);
		
		if (hours == 0)
			return context.getString(R.string.friendlyTime_minutes);
		
		if (days == 0)
			return context.getString(R.string.friendlyTime_hours);
		
		if (days == 1)
			return context.getString(R.string.friendlyTime_yesterday);
		
		if (days == 2)
			return context.getString(R.string.friendlyTime_beforeYesterday);
		
		SimpleDateFormat f = new SimpleDateFormat("MMM d");
		return "on " + f.format(end);
	}
	
	private void debug(String d) {
		android.util.Log.d(DEBUG_TAG, d);
	}
}
