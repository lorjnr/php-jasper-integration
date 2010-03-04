package ch.bayo.jasper.model.generation;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

public class GeneratorBase {

	public static enum GenLanguages { glUndefined, glPHP }
	private GenLanguages language;
	
	public GeneratorBase(GenLanguages language) {
		this.language = language;
	}

	protected final GenLanguages getObjLanguage() {
		return language;
	}
	
	public static GenLanguages getLanguage() {
		return GenLanguages.glUndefined;
	}

	public static Class getGeneratorClass(Vector<Class> candidates, GenLanguages language)
	{
		Method meth;
		GenLanguages lan;
		
		Iterator it = candidates.iterator();
		while (it.hasNext()) {
			Class c = (Class)it.next();

			meth = null;
			lan = GenLanguages.glUndefined;
			try {
				meth = c.getMethod("getLanguage");
			} catch (NoSuchMethodException e) {
				meth = null;
			}
			if (meth != null) {
				try { 
					lan = (GenLanguages)meth.invoke(null, (Object[])null);
				} catch (Exception e) {
					lan = GenLanguages.glUndefined;
				}
			}
			
			if (lan == language) {
				return c;
			}
			
		}
		return null;
	}

}
