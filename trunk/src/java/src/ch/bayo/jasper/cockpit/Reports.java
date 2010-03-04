package ch.bayo.jasper.cockpit;

import java.util.Iterator;
import java.util.Vector;

public class Reports {

	private Vector<ReportFacade> items;
	
	Reports() {
		items = new Vector<ReportFacade>();
	}
	
	public boolean add(ReportFacade item) {
		return items.add(item);
	}
	
	public boolean remove(ReportFacade item) {
		return items.remove(item);
	}
	
	public boolean hasItems() {
		return (items.size() > 0);
	}
	
	public Iterator getIterator() {
		return items.iterator();
	}

}
