package ch.bayo.jasper.cockpit;

import java.io.File;

import javax.swing.JFileChooser;

import ch.bayo.jasper.cockpit.ui.*;

public class Main {
	
	static WorkspaceFacade loadWsFacade() {
		WorkspaceFacade wsf = new WorkspaceFacade();
		wsf.load();
		if ( ! wsf.isOpen() ) {
			
			File ws_dir = null;
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File("."));
			fc.setDialogTitle("Chose Workspace");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				ws_dir = fc.getSelectedFile();
			} else {
				ws_dir = null;
			}

			wsf.createWorkspace(ws_dir);
		}
		return wsf;
	}
	
	public static void saveWsFacade(WorkspaceFacade wsf) {
		wsf.save();
	}

	public static void main(String[] args) {
		
		WorkspaceFacade wsf = loadWsFacade();
		
		if (wsf.isOpen()) {
			MainFrame frm = new MainFrame(wsf.getWorkspace());
			frm.showFrame();
		}
	}
	
}
