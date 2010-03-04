package ch.bayo.jasper.model.elements;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.CommonGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.generation.GeneratorBase;
import ch.bayo.jasper.model.modification.Modification;

import ch.bayo.jasper.generation.php.StaticText_GenPhp;
import ch.bayo.jasper.generation.php.StaticText_Common_GenPhp;
import ch.bayo.lib.log.LogList;

import net.sf.jasperreports.engine.JRStaticText;

public class StaticText extends ElementBase implements Generatable {
	
	private JRStaticText element;
	
	public StaticText(JRStaticText element) {
		this.element = element;
	}
	
	public JRStaticText getElement() {
		return this.element;
	}
	
	public static CommonGeneratorBase getCommonGenerator(GeneratorBase.GenLanguages language) {
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new StaticText_Common_GenPhp();
		}
		return null;
	}

	public ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language) {
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new StaticText_GenPhp(report, this);
		}
		return null;
	}

	public boolean applyModification(Modification modification, LogList modificationLog) {
		if (modification.getId().compareTo("StaticText") == 0) {
			String value = modification.getParam(element.getKey());
			if (value != null) {
				element.setText(value);
				return true;
			}
			return false;		
		} else {
			return false;
		}
	}

}

