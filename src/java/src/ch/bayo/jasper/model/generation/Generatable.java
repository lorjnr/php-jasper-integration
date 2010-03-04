package ch.bayo.jasper.model.generation;

import ch.bayo.jasper.cockpit.Report;

public interface Generatable {

	ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language);
	
}
