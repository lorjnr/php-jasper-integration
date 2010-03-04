package ch.bayo.jasper.model.modification;

import java.util.Iterator;
import java.util.Vector;

public class Modifications {

	private Vector<Modification> items;
	
	public Modifications() {
		items = new Vector<Modification>();
	}
	
	public boolean add(Modification item) {
		return items.add(item);
	}
	
	public boolean hasItems() {
		return (items.size() > 0);
	}
	
	public Iterator getIterator() {
		return items.iterator();
	}
	
}
