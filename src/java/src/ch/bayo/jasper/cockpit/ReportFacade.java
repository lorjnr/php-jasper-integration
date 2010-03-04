package ch.bayo.jasper.cockpit;

import org.jdom.Element;

import ch.bayo.lib.log.LogItem;

import java.io.*;

public class ReportFacade {
	
	private Project proj;

	private String name = "";
	private Report report = null;
	
	private File jrxml = null;
	
	
	ReportFacade(Project proj) {
		this.proj = proj;
	}
	
		
	public String getName() {
		return name;
	}
	
	public File getJrxml() {
		return jrxml;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Report getReport() {
		return report;
	}
	
	public Project getProject() {
		return proj;
	}

	private String extractFileName(File f) {
		String file_name = f.getAbsolutePath();
		int p = file_name.lastIndexOf(File.separatorChar);
		String result = "";
		if ( (p >= 0) && (p < file_name.length()-2) ) {
			result = file_name.substring(p+1);
		}
		return result;
	}

	
	boolean createReport(String name, File jrxml, File proj_ws) {
		this.name = name;

		String jrxml_name = extractFileName(jrxml);
		File loc_jrxml = new File(proj_ws.getAbsolutePath() + "/" + jrxml_name);
		
		Logger.addItem(new LogItem(Logger.LEVEL_INFO, "Copy Report to "  + jrxml_name));
		
		if ( loc_jrxml.exists() ) {
			Logger.addItem(new LogItem(Logger.LEVEL_ERROR, "File exists "  + jrxml_name));
			return false;
		}
		
		if ( ! copyfile(jrxml, loc_jrxml) ) {
			Logger.addItem(new LogItem(Logger.LEVEL_ERROR, "Unable to copy file "  + jrxml_name));
			return false;
		}
		
		this.jrxml = loc_jrxml;
		report = new Report(this, deleteSpaces(name), loc_jrxml);
		boolean valid = report.ObjValid();
		if ( ! valid ) {
			Logger.addItem(new LogItem(Logger.LEVEL_ERROR, "Report invalid"  + loc_jrxml));
			loc_jrxml.delete();
			report = null;
		}
		return valid;
	}
	
	void createEmptyReport() {
		this.name = "";
		this.jrxml = null;
		report = new Report(this);
	}

	boolean deleteReport() {
		if ( jrxml.exists() ) jrxml.delete();
		report = null;
		return true;
	}
	
	private boolean copyfile(File source, File destinatioin){
		if ( destinatioin.exists() ) return false;
		try{
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destinatioin);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch(FileNotFoundException ex){
			return false;
		} catch(IOException e){
			if ( destinatioin.exists() ) destinatioin.delete();
			return false;
		}
	}
	
	private void saveString(String name, String value, Element r_el) {
		Element el = new Element(name);
		el.addContent(value);
		r_el.addContent(el);
	}

	private String deleteSpaces(String str) {
		int p = str.indexOf(' ');
		while (p >= 0) {
			String tmp = str.substring(0, p) + str.substring(p+1);
			str = tmp;
			p = str.indexOf(' ');
		}
		return str;
	}
	
	private String loadString(String name, Element r_el) {
		return r_el.getChildText(name);
	}
	
	void save(Element repf_el) {
		saveString("name", name, repf_el);
		saveString("jrxml", jrxml.getAbsolutePath(), repf_el);
		
		if ( report != null ) {
			Element rep_el = new Element("Report");
			report.save(rep_el);
			repf_el.addContent(rep_el);
		}
	}
	
	void load(Element repf_el) {
		this.name = loadString("name", repf_el);
		String jrxml_file = loadString("jrxml", repf_el);
		jrxml = new File(jrxml_file);
		
		Element rep_el = repf_el.getChild("Report");
		boolean v = report.load(rep_el);
		if ( ! v ) report = null;
	}
	
}
