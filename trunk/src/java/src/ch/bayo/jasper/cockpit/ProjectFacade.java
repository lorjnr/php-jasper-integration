package ch.bayo.jasper.cockpit;

import org.jdom.Element;
import java.io.*;

public class ProjectFacade {

	private String name = "";
	private File ws_dir = null;
	
	private Project project = null;
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Project getProject() {
		return project;
	}
	
	public File getDirectory() {
		return ws_dir;
	}
	
	boolean createProject(String name, File parent) {
		this.name = name;
		
		String proj_ws = parent.getAbsolutePath();
		proj_ws = proj_ws + "/" + deleteSpaces(name);
		File proj_ws_file = new File(proj_ws);
		if ( proj_ws_file.exists() ) return false;
		proj_ws_file.mkdir();
		if ( ! proj_ws_file.exists() ) return false;
		if ( ! proj_ws_file.isDirectory() ) return false;
		
		ws_dir = proj_ws_file;
		project = new Project(this);
		boolean valid = project.ObjValid();
		if ( ! valid ) project = null;

		return valid;
	}

	void createEmptyProject() {
		this.name = "";
		project = new Project(this);
	}
	
	boolean deleteProject() {
		boolean res = true;
		if ( ws_dir.exists() ) {
			res = deleteDirectory(ws_dir);
		}
		if ( res ) project = null;
		return res;
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
	
	private boolean deleteDirectory(File path) {
        if( path.exists() ) {
          File[] files = path.listFiles();
          for(int i=0; i<files.length; i++) {
             if(files[i].isDirectory()) {
               deleteDirectory(files[i]);
             }
             else {
               files[i].delete();
             }
          }
        }
        return( path.delete() );
	} 

	private void saveString(String name, String value, Element p_el) {
		Element el = new Element(name);
		el.addContent(value);
		p_el.addContent(el);
	}

	private String loadString(String name, Element p_el) {
		return p_el.getChildText(name);
	}
		
	void save(Element pjf_el) {
		saveString("name", name, pjf_el);
		saveString("directory", ws_dir.getAbsolutePath(), pjf_el);
		
		if ( project != null ) {
			Element pj_el = new Element("Project");
			project.save(pj_el);
			pjf_el.addContent(pj_el);
		}
	}
	
	void load(Element pjf_el) {
		this.name = loadString("name", pjf_el);
		String wd = loadString("directory", pjf_el);
		ws_dir = new File(wd);
		
		Element pj_el = pjf_el.getChild("Project");
		boolean v = project.load(pj_el);
		if ( ! v ) project = null;

	}
		
}
