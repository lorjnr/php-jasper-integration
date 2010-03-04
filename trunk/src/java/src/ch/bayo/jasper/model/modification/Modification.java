package ch.bayo.jasper.model.modification;

import java.util.Iterator;

public class Modification {

	private String id;
	private ModificationParams params;
	
	public static char C_PARAM_SEP = ':';
	public static char C_VALUE_SEP = '=';

	public Modification(String mod_str) {
		id = "Invalid";
		params = new ModificationParams();
		
		int p = mod_str.indexOf(C_PARAM_SEP);
		if ( p > 0 ) {
			id = mod_str.substring(0, p);
			String value = mod_str.substring(p+1);
			
			if (value.charAt(value.length()-1) != C_PARAM_SEP) value = value + C_PARAM_SEP;
			p = findNextOccurrence(value, C_PARAM_SEP);
			while (p >= 0) {
				String param = value.substring(0, p);
				value = value.substring(p+1);
				p = param.indexOf(C_VALUE_SEP);
				if (p < 0) {
					id = "Invalid";
					break;
				}
				String n = param.substring(0, p);
				String v = param.substring(p+1);
				v = decodeParam(v);
				
				params.add(new ModificationParam(n, v));
				
				p = findNextOccurrence(value, C_PARAM_SEP);
			}
			
		}
	}

	private int findNextOccurrence(String str, char separator) {
		int p;
		int offset;

		offset = 0;
		p = str.indexOf(separator);
		while (true) {
			if ( p < 0 ) return p;
			if ( ( p < (str.length()-1) ) && ( str.charAt(p+1) == separator ) ) {
				offset = (p + 2);
				p = str.indexOf(separator, offset);
			} else {
				return p;
			}
		}	
	}
	
	private String decodeParam(String param) {
		int p;
		int offset;
		
		// C_PARAM_SEP
		offset = 0;
		while (true) {
			p = param.indexOf(C_PARAM_SEP, offset);
			if ( p < 0 ) break;
			if ( ( p < (param.length()-1) ) && ( param.charAt(p+1) == C_PARAM_SEP ) ) {
				param = param.substring( 0, (p+1) ) + param.substring( (p+2), param.length() );
			}
			offset = (p+1);
		}

		return param;
	}
	
	public String getId() {
		return id;
	}
	
	public String getParam(String name) {
		Iterator it = params.getIterator();
		while (it.hasNext()) {
			ModificationParam p = (ModificationParam)it.next();
			if (p.getName().compareTo(name) == 0) return p.getValue();
		}
		return null;
	}

}
