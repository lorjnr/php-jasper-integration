package ch.bayo.jasper.model;

import net.sf.jasperreports.engine.*;

import ch.bayo.jasper.model.modification.Modifiable;
import ch.bayo.jasper.model.modification.Modification;
import ch.bayo.lib.log.LogList;

public class Parameter implements Modifiable {
	
	JRParameter jParam = null;
	Parameters parent;
	
	Parameter(JRParameter jParam) {
		this.jParam = jParam;
	}
		
	public String getName() {
		return jParam.getName();
	}

	void setParent(Parameters parent) {
		this.parent = parent;
	}
	
	public String getModificationString(String newValue) {
		return "Parameter"+Modification.C_PARAM_SEP+jParam.getName()+Modification.C_VALUE_SEP+newValue;
	}
	
	public boolean applyModification(Modification modification, LogList modificationLog)
	{
		if (modification.getId().compareTo("Parameter") == 0) {
			String value = modification.getParam(jParam.getName());
			if (value != null) {
				Class c = jParam.getValueClass();
				
				boolean b = false;
				
				//Integer parameter
				if (c == Integer.class) {
					int int_value = 0;
					try {
						int_value = Integer.valueOf(value);
						b = true;
					} catch ( NumberFormatException e) {
						modificationLog.addWarning("Parameter "+jParam.getName()+": Unable to convert "+value+" to an integer");
						return true;
					}
					parent.addMapParam(jParam.getName(), int_value);
				}
				
				//String parameter
				if (c == String.class) {
					parent.addMapParam(jParam.getName(), value);
					b = true;
				}
				
				if (b == false) {
					modificationLog.addWarning("Parameter "+jParam.getName()+": Parameter-Type not supported");
				}
				
				return true;
			}
			return false;		
		} else {
			return false;
		}
	}
		
}
