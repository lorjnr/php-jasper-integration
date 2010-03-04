package ch.bayo.jasper.model;

import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRParameter;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.generation.GeneratorBase;
import ch.bayo.jasper.model.modification.Modifiable;
import ch.bayo.jasper.model.modification.Modification;

import ch.bayo.jasper.generation.php.Parameters_GenPhp;
import ch.bayo.lib.log.LogList;

public class Parameters implements Modifiable, Generatable {

	private Vector<Parameter> items;
	private Map paramMap = null;

	Parameters(JRParameter jrParams[]) {
		items = new Vector<Parameter>();
		parseParameters(jrParams);
	}

	private void parseParameters(JRParameter jrParams[]) {

		if (jrParams.length > 0) {
			
			for (int i=0; i<jrParams.length; i++) {
				if ( ! jrParams[i].isSystemDefined() ) {
					Parameter p = new Parameter(jrParams[i]);
					add(p);
				}
			}
		}

	}

	private boolean add(Parameter item) {
		boolean b = items.add(item);
		if ( b ) item.setParent(this);
		return b;
	}
		
	public boolean hasItems() {
		return (items.size() > 0);
	}
		
	public Iterator getIterator() {
		return items.iterator();
	}
	
	public boolean applyModification(Modification modification, LogList modificationLog)
	{
		Iterator it = getIterator();
		while (it.hasNext()) {
			Parameter p = (Parameter)it.next();
			if ( p.applyModification(modification, modificationLog) ) return true; 
		}
		return false;		
	}
	
	
	/*
	private Vector<Class> getGenCandidates() {
		Vector<Class> res = new Vector<Class>();
		res.add(ParametersPhpGen.class);
		return res;
	}

	public ClassGeneratorBase getGenerator(GeneratorBase.GenLanguages language)
	{
		ClassGeneratorBase res = null;
		Class c = ClassGeneratorBase.getGeneratorClass(getGenCandidates(), language);
		if (c != null) {
			
			Class[] paras = new Class[1];
			paras[0] = Parameters.class;
			try {
				Constructor con = c.getConstructor(paras);
				Object actargs = new Object[] { this };
				res = (ClassGeneratorBase)con.newInstance(actargs);
				
			} catch (Exception e) {
				res = null;
			}
			
		}
		return res;
	}
	*/

	public ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language)
	{
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new Parameters_GenPhp(report, this);
		}
		return null;
	}
		
	void addMapParam(String name, Object value) {
		if (paramMap == null) paramMap = new HashMap();
		paramMap.put(name, value);
	}
	
	public Map getParamMap() {
		return paramMap;
	}
	
}
