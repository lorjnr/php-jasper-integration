package ch.bayo.jasper.cockpit;

import java.util.Iterator;
import java.util.Vector;

public class Projects {

	private Vector<ProjectFacade> items;
	
	Projects() {
		items = new Vector<ProjectFacade>();
	}
	
	public boolean add(ProjectFacade item) {
		return items.add(item);
	}
	
	public boolean remove(ProjectFacade item) {
		return items.remove(item);
	}
	
	public boolean hasItems() {
		return (items.size() > 0);
	}
	
	public Iterator getIterator() {
		return items.iterator();
	}
	
}
