package ch.bayo.jasper.model.modification;

import java.util.Iterator;
import java.util.Vector;

public class ModificationParams {

	private Vector<ModificationParam> items;
		
	ModificationParams() {
		items = new Vector<ModificationParam>();
	}
		
	boolean add(ModificationParam item) {
		return items.add(item);
	}
		
	public boolean hasItems() {
		return (items.size() > 0);
	}
		
	public Iterator getIterator() {
		return items.iterator();
	}
	
}
