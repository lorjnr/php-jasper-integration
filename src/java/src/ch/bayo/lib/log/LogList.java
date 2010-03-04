package ch.bayo.lib.log;

import java.util.Iterator;
import java.util.Vector;

public class LogList {

	private Vector<LogItem> items;
	
	public static final int INFORMATION   = 1;
	public static final int WARNING       = 2;
	public static final int ERROR         = 3;
	
	public LogList() {
		items = new Vector<LogItem>();
	}

	public void addItem(LogItem entry) {
		items.add(entry);
	}

	public void addInfo(String msg) {
		items.add(new LogItem(INFORMATION, msg));
	}

	public void addWarning(String msg) {
		items.add(new LogItem(WARNING, msg));
	}
	
	public void addError(String msg) {
		items.add(new LogItem(ERROR, msg));
	}
	
	public int getCount() {
		return items.size();
	}

	public Iterator getIterator() {
		return items.iterator();
	}
	
}
