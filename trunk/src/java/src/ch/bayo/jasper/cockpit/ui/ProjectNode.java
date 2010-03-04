package ch.bayo.jasper.cockpit.ui;

import ch.bayo.jasper.cockpit.ProjectFacade;

public class ProjectNode {
	
	private ProjectFacade proj;
	
	public ProjectNode(ProjectFacade proj) {
		this.proj = proj;
	}
	
	public String toString() {
		return proj.getName();
	}
	
	public ProjectFacade getProj() {
		return proj;
	}

}
