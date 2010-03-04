package ch.bayo.jasper.cockpit.ui;

import ch.bayo.jasper.cockpit.ReportFacade;

public class ReportNode {
	
	private ReportFacade rep;
	
	public ReportNode(ReportFacade rep) {
		this.rep = rep;
	}
	
	public String toString() {
		return rep.getName();
	}
	
	public ReportFacade getRep() {
		return rep;
	}

}
