package ch.bayo.jasper.model.modification;

import ch.bayo.lib.log.LogList;

public interface Modifiable {
	
	boolean applyModification(Modification modification, LogList modificationLog);

}
