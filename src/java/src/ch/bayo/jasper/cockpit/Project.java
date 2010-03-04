package ch.bayo.jasper.cockpit;

import java.util.Iterator;

import org.jdom.Element;

import ch.bayo.jasper.cockpit.generation.Generator;
import ch.bayo.lib.log.LogItem;

import java.io.File;

public class Project {

	private ProjectFacade facade;
	private Reports reports;

	//General settings
	private String rep_location;
	private String out_location;
	private String rep_server;
	private int rep_server_port;
	private int max_pdf_storage;
	private int min_pdf_lifetime;
	
	//Database informations
	private String db_driver;
	private String db_conn_str;
	private String db_user;
	private String db_pwd;
	
	//Generation information
	private String gen_jasp_root;
	private String gen_class_dir;
	
	Project(ProjectFacade facade) {
		this.facade = facade;
		reports = new Reports();
		
		//General settings
		rep_location = "reports";
		out_location = "output";
		rep_server = "localhost";
		rep_server_port = 1234;
		max_pdf_storage = 10;
		min_pdf_lifetime = 10;
		
		//Database informations
		db_driver = "com.mysql.jdbc.Driver";
		db_conn_str = "jdbc:mysql://localhost:3306/db_name";
		db_user = "user_name";
		db_pwd = "password";
		
		//Generation information
		gen_jasp_root = "";
		gen_class_dir = "";
	}
	
	boolean ObjValid() {
		return true;
	}
	
	
	
	public ReportFacade createRep(String name, File jrxml) {
		ReportFacade rf = new ReportFacade(this);
		boolean b = rf.createReport(name, jrxml, facade.getDirectory());
		if ( ! b ) {
			Logger.addItem(new LogItem(Logger.LEVEL_ERROR, "Create Report failed"));
			return null;
		}
		reports.add(rf);
		Logger.addItem(new LogItem(Logger.LEVEL_INFO, "Report added"));
		return rf;
	}

	private ReportFacade createEmptyRep() {
		ReportFacade rf = new ReportFacade(this);
		rf.createEmptyReport();
		reports.add(rf);
		return rf;
	}

	public void deleteRep(ReportFacade rep) {
		boolean b = rep.deleteReport();
		if ( ! b ) return;
		reports.remove(rep);
	}
	
	public boolean generateProject() {
		Generator gen = new Generator(this);
		return gen.execute();
	}

	
	
	public ProjectFacade getFacade() {
		return facade;
	}
	
	public Reports getReports() {
		return reports;
	}
	

	public String getRepLocation() {
		return rep_location;
	}
	
	public String getOutLocation() {
		return out_location;
	}
	
	public String getRepServer() {
		return rep_server;
	}
	
	public int getRepServerPort() {
		return rep_server_port;
	}
	
	public int getMaxPdfStorage() {
		return max_pdf_storage;
	}
	
	public int getMinPdfLifetime() {
		return min_pdf_lifetime;
	}
	
	public String getDbDriver() {
		return db_driver;
	}
	
	public String getDbConnStr() {
		return db_conn_str;
	}
	
	public String getDbUser() {
		return db_user;
	}
	
	public String getDbPwd() {
		return db_pwd;
	}
	
	public String getGenJaspRoot() {
		return gen_jasp_root;
	}
	
	public String getGenClassDir() {
		return gen_class_dir;
	}
	
	public void setRepLocation(String rep_location) {
		this.rep_location = rep_location;
	}
	
	public void setOutLocation(String out_location) {
		this.out_location = out_location;
	}
	
	public void setRepServer(String rep_server) {
		this.rep_server = rep_server;
	}
	
	public void setRepServerPort(int rep_server_port) {
		this.rep_server_port = rep_server_port;
	}
	
	public void setMaxPdfStorage(int max_pdf_storage) {
		this.max_pdf_storage = max_pdf_storage;
	}
	
	public void setMinPdfLifetime(int min_pdf_lifetime) {
		this.min_pdf_lifetime = min_pdf_lifetime;
	}
	
	public void setDbDriver(String db_driver) {
		this.db_driver = db_driver;
	}
	
	public void setDbConnStr(String db_conn_str) {
		this.db_conn_str = db_conn_str;
	}
	
	public void setDbUser(String db_user) {
		this.db_user = db_user;
	}
	
	public void setDbPwd(String db_pwd) {
		this.db_pwd = db_pwd;
	}
	
	public void setGenJaspRoot(String gen_jasp_root) {
		this.gen_jasp_root = gen_jasp_root;
	}
	
	public void setGenClassDir(String gen_class_dir) {
		this.gen_class_dir = gen_class_dir;
	}

	
	
	
	void save(Element pj_el) {
		//General settings
		saveString("rep_location", rep_location, pj_el);
		saveString("out_location", out_location, pj_el);
		saveString("rep_server", rep_server, pj_el);
		saveInt("rep_server_port", rep_server_port, pj_el);
		saveInt("max_pdf_storage", max_pdf_storage, pj_el);
		saveInt("min_pdf_lifetime", min_pdf_lifetime, pj_el);
		
		//Db Information
		saveString("db_driver", db_driver, pj_el);
		saveString("db_conn_str", db_conn_str, pj_el);
		saveString("db_user", db_user, pj_el);
		saveString("db_pwd", db_pwd, pj_el);
		
		//Generation Information
		saveString("gen_jasp_root", gen_jasp_root, pj_el);
		saveString("gen_class_dir", gen_class_dir, pj_el);
		
		Iterator it = reports.getIterator();
		while (it.hasNext()) {
			ReportFacade r = (ReportFacade)it.next();
			Element repf_el = new Element("ReportFacade");
			r.save(repf_el);
			pj_el.addContent(repf_el);
		}
	}
	
	boolean load(Element pj_el) {
		//General settings
		this.rep_location = loadString("rep_location", pj_el);
		this.out_location = loadString("out_location", pj_el);
		this.rep_server = loadString("rep_server", pj_el);
		this.rep_server_port = loadInt("rep_server_port", pj_el);
		this.max_pdf_storage = loadInt("max_pdf_storage", pj_el);
		this.min_pdf_lifetime = loadInt("min_pdf_lifetime", pj_el);
		
		//Db information
		this.db_driver = loadString("db_driver", pj_el);
		this.db_conn_str = loadString("db_conn_str", pj_el);
		this.db_user = loadString("db_user", pj_el);
		this.db_pwd = loadString("db_pwd", pj_el);
		
		//Generation Information
		this.gen_jasp_root = loadString("gen_jasp_root", pj_el);
		this.gen_class_dir = loadString("gen_class_dir", pj_el);
		
		Iterator it = pj_el.getChildren("ReportFacade").iterator();
		while (it.hasNext()) {
			Element repf_el = (Element)it.next();
			ReportFacade repf = createEmptyRep();
			repf.load(repf_el);
		}
		
		return ObjValid();
	}

	private void saveString(String name, String value, Element p_el) {
		Element el = new Element(name);
		el.addContent(value);
		p_el.addContent(el);
	}

	private void saveInt(String name, int value, Element p_el) {
		String value_str = Integer.toString(value);
		saveString(name, value_str, p_el);
	}
		
	private String loadString(String name, Element p_el) {
		return p_el.getChildText(name);
	}
	
	private int loadInt(String name, Element p_el) {
		String value_str = loadString(name, p_el);
		return Integer.valueOf(value_str);
	}

		
}
