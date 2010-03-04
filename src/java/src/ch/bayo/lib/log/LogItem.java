package ch.bayo.lib.log;

public class LogItem {
	
	private int level;
	private String msg;
	 
	public LogItem(int level, String msg) {
		this.level = level;
		this.msg = msg;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public String getMsg() {
		return this.msg;
	}
	

}
