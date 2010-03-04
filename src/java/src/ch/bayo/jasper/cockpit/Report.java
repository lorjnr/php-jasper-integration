package ch.bayo.jasper.cockpit;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

import org.jdom.Element;
import java.io.*;

import ch.bayo.jasper.model.ReportModel;
import ch.bayo.lib.log.LogItem;

public class Report {

	private ReportFacade facade;
	
	private String id;
	private ReportModel repModel;

	Report(ReportFacade facade, String id, File jrxml) {
		this.facade = facade;
		this.id = id;
		
		JasperReport jReport = compileReport(facade.getJrxml());
		if (jReport != null) {
			repModel = new ReportModel(jReport);
		} else {
			repModel = null;
		}
	}

	Report(ReportFacade facade) {
		this.facade = facade;
		this.id = "";
		repModel = null;
	}
	
	boolean ObjValid() {
		boolean res = (repModel != null);
		if ( ! res ) return false;
		return true;
	}
	
	public ReportFacade getFacade() {
		return facade;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRepLocation() {
		return facade.getJrxml().getAbsolutePath();
	}
	
	public ReportModel getRepModel() {
		return repModel; 
	}
	
	public void setId(String id) {
		this.id = id;
	}
		
	private JasperReport compileReport(File jrxml) {
		JRProperties.backupProperties();
		
		JasperReport res = null;
		try {
			Logger.addItem(new LogItem(Logger.LEVEL_INFO, "Compile Jrxml "  + jrxml.getAbsolutePath()));
			res = JasperCompileManager.compileReport(jrxml.getAbsolutePath());
		} catch (JRException e) {
			Logger.addItem(new LogItem(Logger.LEVEL_EXCEPTION, e.getMessage()));
			res = null;
		}

		JRProperties.restoreProperties();
		
		return res;
	}
		
	void save(Element rep_el) {
		
		saveString("id", id, rep_el);
		
	}
	
	boolean load(Element rep_el) {

		this.id = loadString("id", rep_el);
		JasperReport jReport = compileReport(facade.getJrxml());
		if (jReport != null) {
			repModel = new ReportModel(jReport);
		} else {
			repModel = null;
		}
		
		return ObjValid();
		
	}

	private void saveString(String name, String value, Element p_el) {
		Element el = new Element(name);
		el.addContent(value);
		p_el.addContent(el);
	}

	private String loadString(String name, Element p_el) {
		return p_el.getChildText(name);
	}
	
}
