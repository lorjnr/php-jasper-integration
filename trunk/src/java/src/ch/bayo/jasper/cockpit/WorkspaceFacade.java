package ch.bayo.jasper.cockpit;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.*;


public class WorkspaceFacade {

	Workspace ws = null;
	
	File ws_dir;
	
	File conf_dir;
	File conf_file;
	
	WorkspaceFacade() {
		String cd = System.getProperty("user.home") + "/.jaspit";
		String cf = cd + "/workspace";
		
		conf_dir = new File(cd);
		if ( ! conf_dir.exists() ) {
			conf_dir.mkdir();
		}
		
		conf_file = new File(cf);
		
		ws_dir = null;
	}
	
	public boolean isOpen() {
		return (ws != null);
	}
	
	public Workspace getWorkspace() {
		return ws;
	}
	
	public File getDirectory() {
		return ws_dir;
	}
	
	void createWorkspace(File workspace) {
		if ( ! isOpen() ) {
			boolean b = workspace.exists();
			b = workspace.isDirectory();
			if (b) {
				ws_dir = workspace;
				ws = new Workspace(this);
			}
		}
	}

	private void createEmptyWorkspace() {
		if ( ! isOpen() ) {
			ws = new Workspace(this);
		}
	}

	private void saveString(String name, String value, Element p_el) {
		Element el = new Element(name);
		el.addContent(value);
		p_el.addContent(el);
	}

	private String loadString(String name, Element p_el) {
		return p_el.getChildText(name);
	}
	
	void load() {
		if ( ! isOpen() ) {
			reload();
		}
	}
	
	void reload() {
		ws = null;
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		if (conf_file.exists()) {
			try {
				doc = builder.build(conf_file.getAbsoluteFile());
			} catch ( Exception e) {
				doc = null;
			}
		}
		
		if ( doc != null ) {
			
			Element wsf_el = doc.getRootElement();

			String wd = loadString("directory", wsf_el);
			ws_dir = new File(wd);
			
			Element ws_el = wsf_el.getChild("Workspace");
			createEmptyWorkspace();
			ws.load(ws_el);
			
		}
	}	
	
	void save() {
		if ( isOpen() ) {

			Element wsf_el = new Element("WorkspaceFacade");
			
			saveString("directory", ws_dir.getAbsolutePath(), wsf_el);
			
			Element ws_el = new Element("Workspace");
			ws.save(ws_el);
			wsf_el.addContent(ws_el);
			Document doc = new Document(wsf_el);
			
            XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
            try {
            	outp.output(doc,new FileOutputStream(conf_file.getAbsoluteFile()));
            } catch ( Exception e ) {
            	//Ignore
            }

		}
	}
		
}
