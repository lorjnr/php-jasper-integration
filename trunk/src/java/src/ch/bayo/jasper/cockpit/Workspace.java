package ch.bayo.jasper.cockpit;

import java.util.Iterator;
import org.jdom.Element;

public class Workspace {
	
	private WorkspaceFacade facade = null;
	private Projects projects = null;
	
	Workspace(WorkspaceFacade facade) {
		this.facade = facade;
		projects = new Projects();
	}
	
	public ProjectFacade createProj(String name) {
		ProjectFacade pf = new ProjectFacade();
		boolean b = pf.createProject(name, facade.getDirectory());
		if ( ! b ) return null;
		projects.add(pf);
		return pf;
	}

	private ProjectFacade createEmptyProj() {
		ProjectFacade pf = new ProjectFacade();
		pf.createEmptyProject();
		projects.add(pf);
		return pf;
	}
	
	public void deleteProj(ProjectFacade proj) {
		boolean b = proj.deleteProject();
		if ( ! b ) return;
		projects.remove(proj);
	}
	
	void save(Element ws_el) {
		Iterator it = projects.getIterator();
		while (it.hasNext()) {
			ProjectFacade p = (ProjectFacade)it.next();
			Element pjf_el = new Element("ProjectFacade");
			p.save(pjf_el);
			ws_el.addContent(pjf_el);
		}
	}
	
	void load(Element ws_el) {
		Iterator it = ws_el.getChildren("ProjectFacade").iterator();
		while (it.hasNext()) {
			Element pjf_el = (Element)it.next();
			ProjectFacade pjf = createEmptyProj();
			pjf.load(pjf_el);
		}
	}
	
	public Projects getProjects() {
		return projects;
	}
	
	public WorkspaceFacade getFacade() {
		return facade;
	}
	
}
