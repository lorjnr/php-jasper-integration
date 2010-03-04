package ch.bayo.jasper.cockpit;

import ch.bayo.lib.log.LogItem;
import ch.bayo.lib.log.LogList;

public class Logger {

	private static LogList list = new LogList();
	
	public static int LEVEL_INFO = 3;
	public static int LEVEL_WARNING = 2;
	public static int LEVEL_ERROR = 1;
	public static int LEVEL_EXCEPTION = 0;
	
	public static void addItem(LogItem entry) {
		list.addItem(entry);
	}

}
