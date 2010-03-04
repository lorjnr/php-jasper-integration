package ch.bayo.jasper.model.elements;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.generation.GeneratorBase;
import ch.bayo.jasper.model.modification.Modification;
import ch.bayo.jasper.model.modification.Modifiable;
import ch.bayo.lib.log.LogList;

public class ElementBase implements Generatable, Modifiable {

	public ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language)
	{
		return null;
	}

	public boolean applyModification(Modification modification, LogList modificationLog) {
		return false;
	}

}
