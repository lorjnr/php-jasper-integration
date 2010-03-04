package ch.bayo.jasper.model;

import java.util.Iterator;
import java.util.Vector;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRStaticText;
import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.elements.StaticText;
import ch.bayo.jasper.model.elements.ElementBase;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.generation.GeneratorBase;
import ch.bayo.jasper.model.modification.Modifiable;
import ch.bayo.jasper.model.modification.Modification;

import ch.bayo.jasper.generation.php.Band_GenPhp;
import ch.bayo.lib.log.LogList;

public class Band implements Generatable, Modifiable {

	private Vector<ElementBase> elements;
	private String bandName;
	private JRBand jrBand;

	public Band(JRBand band, String bandName) {
		elements = new Vector<ElementBase>();
		jrBand = band;
		this.bandName = bandName;
		parseBand(band);
	}
	
	private void parseBand(JRBand band) {
		JRElement[] elements = band.getElements();
		JRElement element = null;
		for (int i=0; i<elements.length; i++) {
			element = elements[i];

			if (JRStaticText.class.isInstance(element)) {
				add(new StaticText((JRStaticText)element));
			}

		}
	}

	private boolean add(ElementBase item) {
		boolean b = elements.add(item);
		return b;
	}

	public boolean hasItems() {
		return (elements.size() > 0);
	}

	public Iterator getIterator() {
		return elements.iterator();
	}
	
	public JRBand getJrBand() {
		return jrBand;
	}

	public ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language) {
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new Band_GenPhp(report, this, bandName);
		}
		return null;
	}

	public boolean applyModification(Modification modification, LogList modificationLog) {
		boolean b = false;
		if (elements != null) {
			Iterator it = getIterator();
			while (it.hasNext()) {
				ElementBase el = (ElementBase)it.next();
				if (el != null) {
					b = el.applyModification(modification, modificationLog);
					if(b) break;
				}
			}
		}
		return b;
	}
	
}
