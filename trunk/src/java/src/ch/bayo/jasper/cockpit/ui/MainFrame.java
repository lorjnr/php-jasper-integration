package ch.bayo.jasper.cockpit.ui;

import java.util.Iterator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

import ch.bayo.jasper.cockpit.*;
import ch.bayo.lib.log.LogItem;

import javax.swing.filechooser.*;
import java.io.File;

public class MainFrame extends BaseFrame {
	
	Workspace ws;
	
	MainFrameMouseListener mouseListener;
	MainFrameActionListener actionListener;
	DirSelMouseListener dirSelMouseListener;
	
	JTree trNav;
	JPopupMenu pmNav;
	JMenuItem miNewProj;
	JMenuItem miDelProj;
	JMenuItem miRenProj;
	JMenuItem miNewRep;
	JMenuItem miRenRep;
	JMenuItem miDelRep;
	JMenuItem miGenerate;
	
	JPopupMenu pmDirSel;
	JMenuItem miDirSel;
	
	CardLayout cards;
	JPanel pnlCards;
	EmptyCard crdEmpty;
	ProjectCard crdProject;
	ReportCard crdReport;
		
	public MainFrame(Workspace ws) {
		super("Jaspit");
		this.ws = ws;
		
		buildUi();
		
		//Pack
		pack();
		
	}
	
	private void buildUi() {
		
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc;
		
		//Create events
		
		mouseListener = new MainFrameMouseListener(this);
		actionListener = new MainFrameActionListener(this);
		
		dirSelMouseListener = new DirSelMouseListener(this);
		
		//Tree
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy= 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.ipadx = 100;
		gbc.insets = new Insets(2, 2, 2, 2);
				
		DefaultMutableTreeNode root = buildNavigationTree();
		trNav = new JTree(root);
		trNav.setRootVisible(false);
		trNav.setVisible(true);
				
		JScrollPane spTree = new JScrollPane(trNav);

		gbl.setConstraints(spTree, gbc);
		add(spTree);
		
		//ValuePanel

		cards = new CardLayout();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy= 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		
		pnlCards = new JPanel(cards);
		gbl.setConstraints(pnlCards, gbc);
		
		crdEmpty = new EmptyCard();
		pnlCards.add(EmptyCard.CARD_ID, crdEmpty);
		
		crdProject = new ProjectCard(this);
		pnlCards.add(ProjectCard.CARD_ID, crdProject);
		
		crdReport = new ReportCard(this);
		pnlCards.add(ReportCard.CARD_ID, crdReport);

		add(pnlCards);
		
		cards.show(pnlCards, EmptyCard.CARD_ID);
		
		//Popup
		pmNav = new JPopupMenu();
		miNewProj = new JMenuItem("New Project");
		pmNav.add(miNewProj);
		pmNav.addSeparator();
		miRenProj = new JMenuItem("Rename Project");
		pmNav.add(miRenProj);
		miDelProj = new JMenuItem("Delete Project");
		pmNav.add(miDelProj);
		miNewRep = new JMenuItem("New Report");
		pmNav.add(miNewRep);
		pmNav.addSeparator();
		miRenRep = new JMenuItem("Rename Report");
		pmNav.add(miRenRep);
		miDelRep = new JMenuItem("Delete Report");
		pmNav.add(miDelRep);
		pmNav.addSeparator();
		miGenerate = new JMenuItem("Generate");
		pmNav.add(miGenerate);
		
		//DirSel Popup
		pmDirSel = new JPopupMenu();
		miDirSel = new JMenuItem("Select directory");
		pmDirSel.add(miDirSel);
		
		//Set Events
		
		trNav.addMouseListener(mouseListener);
		
		miNewProj.addActionListener(actionListener);
		miRenProj.addActionListener(actionListener);
		miDelProj.addActionListener(actionListener);
		miNewRep.addActionListener(actionListener);
		miRenRep.addActionListener(actionListener);
		miDelRep.addActionListener(actionListener);
		miGenerate.addActionListener(actionListener);
		
		miDirSel.addActionListener(actionListener);
	}
	
	private DefaultMutableTreeNode buildNavigationTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		Iterator it_p = ws.getProjects().getIterator();
		while (it_p.hasNext()) {
			ProjectFacade p = (ProjectFacade)it_p.next();
			DefaultMutableTreeNode p_child = new DefaultMutableTreeNode(new ProjectNode(p));
			root.add(p_child);
			
			if ( p.getProject() != null ) {
				Iterator it_r = p.getProject().getReports().getIterator();
				while (it_r.hasNext()) {
					ReportFacade r = (ReportFacade)it_r.next();
					DefaultMutableTreeNode r_child = new DefaultMutableTreeNode(new ReportNode(r));
					p_child.add(r_child);
				}
			}
			
		}
		return root;
	}
	
	private void refreshNavigationTree() {
		DefaultMutableTreeNode root = buildNavigationTree();
		trNav.setModel(new DefaultTreeModel(root));
	}
	
	protected boolean exitOnClose() {
		return true;
	}
	
	void doAfterClose() {
		Main.saveWsFacade(ws.getFacade());
	}
	
	private ProjectFacade getSelectedProjFac() {
		ProjectFacade result = null;
		TreePath path = trNav.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode)path.getLastPathComponent();
			Object o = tn.getUserObject();
			if ( ProjectNode.class.isInstance(o) ) {
				ProjectNode pn = (ProjectNode)o;
				result = pn.getProj();
			}
		}
		return result;
	}

	private ReportFacade getSelectedRepFac() {
		ReportFacade result = null;
		TreePath path = trNav.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode)path.getLastPathComponent();
			Object o = tn.getUserObject();
			if ( ReportNode.class.isInstance(o) ) {
				ReportNode rn = (ReportNode)o;
				result = rn.getRep();
			}
		}
		return result;
	}
	
	void onMouseClick(MouseEvent event) {
		
		ProjectFacade projFac = getSelectedProjFac();
		Project proj = null;
		if (projFac != null) proj = projFac.getProject();
		
		ReportFacade repFac = getSelectedRepFac();
		Report rep = null;
		if (repFac != null) rep = repFac.getReport();
		
		if (event.isPopupTrigger()) {

			if (event.getComponent() == trNav) {
				miDelProj.setEnabled(projFac != null);
				miRenProj.setEnabled(projFac != null);
				miNewRep.setEnabled(proj != null);
				miRenRep.setEnabled(repFac != null);
				miDelRep.setEnabled(repFac != null);
				miGenerate.setEnabled(proj != null);
				pmNav.show(event.getComponent(), event.getX(), event.getY());		
			}

		} else {

			if (event.getComponent() == trNav) {	

				crdProject.setProject(projFac);
				crdReport.setReport(repFac);

				if (proj != null) {
					cards.show(pnlCards, ProjectCard.CARD_ID);
				} else if (rep != null) {
					cards.show(pnlCards, ReportCard.CARD_ID);
				} else {
					cards.show(pnlCards, EmptyCard.CARD_ID);
				}
								
			}

		}
	}

	void onDirSelMouseClick(MouseEvent event) {		
		if (event.isPopupTrigger()) {

			pmDirSel.show(event.getComponent(), event.getX(), event.getY());		

		}
	}
	
	void onActionPerformed(ActionEvent event) {

		ProjectFacade projFac = getSelectedProjFac();
		Project proj = null;
		if (projFac != null) proj = projFac.getProject();
		
		ReportFacade repFac = getSelectedRepFac();
		
		// New Project
		if ( event.getSource() == miNewProj ) {
			String name = JOptionPane.showInputDialog("Enter Project Name");
			if (name == null) return;
			if (name.compareTo("") == 0) {
				JOptionPane.showMessageDialog(null, "Name can't be empty", "New Project", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ws.createProj(name);
			refreshNavigationTree();
			return;
		}

		//Rename Project
		if ( event.getSource() == miRenProj ) {
			
			if (projFac != null) {
				String name = JOptionPane.showInputDialog("Enter new Project Name");
				if (name != null) {
					if (name.compareTo("") == 0) {
						JOptionPane.showMessageDialog(null, "Name can't be empty", "Rename Project", JOptionPane.ERROR_MESSAGE);
						return;
					}
					projFac.setName(name);
					refreshNavigationTree();
				}
			}
			return;
			
		}
		
		//Delete Project
		if ( event.getSource() == miDelProj ) {
			
			if (projFac != null) {
				ws.deleteProj(projFac);
				refreshNavigationTree();
			}
			return;

		}
		
		// New Report
		if ( event.getSource() == miNewRep ) {
			
			if (proj == null) {
				Logger.addItem(new LogItem(Logger.LEVEL_ERROR, "Project unavailable"));
				return;
			}

			File jrxml = null;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(
					new FileFilter() {
						public boolean accept(File f) {
							if (f.isDirectory()) return true;
							String file_name = f.getAbsolutePath();
							int p = file_name.lastIndexOf('.');
							String ext = "";
							if ( (p >= 0) && (p < file_name.length()-2) ) {
								ext = file_name.substring(p+1);
							}
							return (ext.compareTo("jrxml") == 0);
						}
						public String getDescription() {
							return "Jrxml-Files";
						}
					}
			);
			fc.setCurrentDirectory(new java.io.File("."));
			fc.setDialogTitle("Open Report");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				jrxml = fc.getSelectedFile();
				Logger.addItem(new LogItem(Logger.LEVEL_INFO, "AddReport " + jrxml));
			} else {
				Logger.addItem(new LogItem(Logger.LEVEL_INFO, "AddReport aborted by user"));
				return;
			}
			
			String name = JOptionPane.showInputDialog("Enter Report Name");
		    if (name == null) return;
		    if (name.compareTo("") == 0) {
			    JOptionPane.showMessageDialog(null, "Report-Name cant be empty", "New Report", JOptionPane.ERROR_MESSAGE);
				Logger.addItem(new LogItem(Logger.LEVEL_INFO, "AddReport aborted because of empty id"));
		    	return;
		    }
			
		    proj.createRep(name, jrxml);
			Logger.addItem(new LogItem(Logger.LEVEL_INFO, "New Report added"));
			refreshNavigationTree();
			Logger.addItem(new LogItem(Logger.LEVEL_INFO, "TreeView refreshed"));
		    
			return;
		}

		//Rename Report
		if ( event.getSource() == miRenRep ) {
			
			if (repFac != null) {
				String name = JOptionPane.showInputDialog("Enter new Report Name");
				if (name == null) return;
				if (name.compareTo("") == 0) {
					JOptionPane.showMessageDialog(null, "Name can't be empty", "Rename Report", JOptionPane.ERROR_MESSAGE);
					return;
				}
				repFac.setName(name);
				refreshNavigationTree();
			}
			return;
			
		}
		
		//Delete Report
		if ( event.getSource() == miDelRep ) {
			
			if (repFac != null) {
				repFac.getProject().deleteRep(repFac);
				refreshNavigationTree();
			}
			return;

		}

		//Generate
		if ( event.getSource() == miGenerate ) {
			
			if (proj != null) {
				boolean b = proj.generateProject();
				if ( b ) {
					JOptionPane.showMessageDialog(null, "Generated", "Generator", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Error occured", "Generator", JOptionPane.ERROR_MESSAGE);
				}
			}
			return;

		}

		//Select directory
		if ( event.getSource() == miDirSel ) {
			
			Component c = pmDirSel.getInvoker();
			if ( JTextField.class.isInstance(c) ) {
				JTextField f = (JTextField)c;
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("."));
				fc.setDialogTitle("Chose Directory");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f.setText(fc.getSelectedFile().getAbsolutePath());
				}
				
			}
			return;

		}
		
	}
	
	public DirSelMouseListener getDirSelMouseListener() {
		return dirSelMouseListener;
	}
	
}

class MainFrameMouseListener extends MouseAdapter {
	
	private MainFrame frame = null;
	
	public MainFrameMouseListener(MainFrame frame) {
		this.frame = frame;
	}
	
	public void mousePressed(MouseEvent event) {
		frame.onMouseClick(event);
	}
	
}

class DirSelMouseListener extends MouseAdapter {
	
	private MainFrame frame = null;
	
	public DirSelMouseListener(MainFrame frame) {
		this.frame = frame;
	}
	
	public void mousePressed(MouseEvent event) {
		frame.onDirSelMouseClick(event);
	}
	
}

class MainFrameActionListener implements ActionListener {

	private MainFrame frame = null;
	
	public MainFrameActionListener(MainFrame frame) {
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent event) {
		frame.onActionPerformed(event);
	}
	
}

class EmptyCard extends JPanel {
	
	public static String CARD_ID = "EmptyCard";
	
}

class ReportCard extends BaseCard {

	public static String CARD_ID = "ReportCard";
	
	private ReportFacade rep = null;
	
	private JTextField tfId;
	private JTextField tfLocation;

	private JButton btnSave;
	
	public ReportCard(MainFrame frm) {
		super(frm);
	}

	
	public void setReport(ReportFacade rep) {
		if (this.rep != rep) {
			this.rep = rep;
			updateUi();
		}
	}

	protected void updateUi() {
		Report report = null;
		
		if (rep != null) {
			report =rep.getReport();
		}
		
		if (report != null) {

			tfId.setText(report.getId());
			tfLocation.setText(report.getRepLocation());
			
		} else {

			tfId.setText("");
			tfLocation.setText("");
			
		}
	}

	private void saveUi() {
		Report report = null;
		
		if (rep != null) {
			report = rep.getReport();
		}
		
		if (report != null) {
			
			report.setId(tfId.getText());

		}
	}

	protected void buildUi() {
		addHSpacePanel(0);
		addTitle(1, "Report");
		
		tfId = addField(2, "Report Id");
		tfLocation = addField(3, "Location");
		tfLocation.setEnabled(false);
		
		btnSave = addSaveButton(4, "Save");
		
		btnSave.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveUi();
					}
				}	
		);
		
		addVSpacePanel(5);	
	}
	
}

class ProjectCard extends BaseCard {

	public static String CARD_ID = "ProjectCard";

	private ProjectFacade proj = null;
	
	private JTextField tfRepLocation;
	private JTextField tfOutLocation;
	private JTextField tfRepServer;
	private JTextField tfRepServerPort;
	private JTextField tfMaxPdfStorage;
	private JTextField tfMinPdfLifetime;
	
	private JTextField tfDriver;
	private JTextField tfConnStr;
	private JTextField tfUser;
	private JTextField tfPwd;

	private JTextField tfGenJaspRoot;
	private JTextField tfGenClassDir;
	
	private JButton btnSave;

	
	public ProjectCard(MainFrame frm) {
		super(frm);
	}
	
	public void setProject(ProjectFacade proj) {
		if (this.proj != proj) {
			this.proj = proj;
			updateUi();
		}
	}
	
	protected void updateUi() {
		Project project = null;
		
		if (proj != null) {
			project = proj.getProject();
		}
		
		if (project != null) {

			tfRepLocation.setText(project.getRepLocation());
			tfOutLocation.setText(project.getOutLocation());
			tfRepServer.setText(project.getRepServer());
			tfRepServerPort.setText(Integer.toString(project.getRepServerPort()));
			tfMaxPdfStorage.setText(Integer.toString(project.getMaxPdfStorage()));
			tfMinPdfLifetime.setText(Integer.toString(project.getMinPdfLifetime()));
			
			tfDriver.setText(project.getDbDriver());
			tfConnStr.setText(project.getDbConnStr());
			tfUser.setText(project.getDbUser());
			tfPwd.setText(project.getDbPwd());
			
			tfGenJaspRoot.setText(project.getGenJaspRoot());
			tfGenClassDir.setText(project.getGenClassDir());
			
		} else {

			tfRepLocation.setText("");
			tfOutLocation.setText("");
			tfRepServer.setText("");
			tfRepServerPort.setText("");
			tfMaxPdfStorage.setText("");
			tfMinPdfLifetime.setText("");

			tfDriver.setText("");
			tfConnStr.setText("");
			tfUser.setText("");
			tfPwd.setText("");
			
			tfGenJaspRoot.setText("");
			tfGenClassDir.setText("");
			
		}
	}
	
	private void saveUi() {
		Project project = null;
		
		if (proj != null) {
			project = proj.getProject();
		}
		
		if (project != null) {
			int port;
			int maxPdfStorage;
			int minPdfLifetime;
			
			try {
				port = Integer.valueOf(tfRepServerPort.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Port must be an integer", "Save Project", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				maxPdfStorage = Integer.valueOf(tfMaxPdfStorage.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Max Pdf Storage", "Save Project", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				minPdfLifetime = Integer.valueOf(tfMinPdfLifetime.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Man Pdf Lifetime", "Save Project", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			project.setRepLocation(tfRepLocation.getText());
			project.setOutLocation(tfOutLocation.getText());
			project.setRepServer(tfRepServer.getText());
			project.setRepServerPort(port);
			project.setMaxPdfStorage(maxPdfStorage);
			project.setMinPdfLifetime(minPdfLifetime);
			
			project.setDbConnStr(tfConnStr.getText());
			project.setDbDriver(tfDriver.getText());
			project.setDbUser(tfUser.getText());
			project.setDbPwd(tfPwd.getText());
			
			project.setGenJaspRoot(tfGenJaspRoot.getText());
			project.setGenClassDir(tfGenClassDir.getText());
		}
	}
	
	protected void buildUi() {
		addHSpacePanel(0);
		addTitle(1, "General settings");
		
		tfRepLocation = addField(2, "Report Location");
		tfOutLocation = addField(3, "Output Location");
		tfRepServer = addField(4, "Report server");
		tfRepServerPort = addField(5, "Report server port");
		tfMaxPdfStorage = addField(6, "Max Pdf storage");
		tfMinPdfLifetime = addField(8, "Min Pdf Lifetime");

		addHSpacePanel(9);
		addTitle(10, "Database information");

		tfDriver = addField(11, "Driver");
		tfConnStr = addField(12, "Connection");
		tfUser = addField(13, "Username");
		tfPwd = addField(14, "Password");

		addHSpacePanel(15);
		addTitle(16, "Generation");

		tfGenJaspRoot = addField(17, "Jasp Root");
		tfGenClassDir = addField(19, "Class Output");
		
		tfGenJaspRoot.addMouseListener(getFrm().getDirSelMouseListener());
		tfGenClassDir.addMouseListener(getFrm().getDirSelMouseListener());

		
		btnSave = addSaveButton(20, "Save");
		
		btnSave.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveUi();
					}
				}	
		);
		
		addVSpacePanel(21);	
	}
	
}

abstract class BaseCard extends JPanel {

	private GridBagLayout gbLayout;
	private MainFrame frm;
			
	public BaseCard(MainFrame frm) {
		super(new GridBagLayout());
		this.frm = frm;
		gbLayout = (GridBagLayout)getLayout();
		buildUi();
		updateUi();
	}
	
	protected MainFrame getFrm() {
		return frm;
	}
		
	protected abstract void updateUi();
	
	protected abstract void buildUi();

	protected void addTitle(int y, String caption) {
		GridBagConstraints gbc = createTitleGbc(y);
		JLabel lbl = new JLabel("--- "+caption+" ---");
		gbLayout.setConstraints(lbl, gbc);
		add(lbl);
	}
	
	protected JTextField addField(int y, String caption) {
		GridBagConstraints gbc;
		gbc = createLabelGbc(y);
		JLabel lbl = new JLabel(caption);
		gbLayout.setConstraints(lbl, gbc);
		add(lbl);

		gbc = createFieldGbc(y);
		JTextField fld = new JTextField("");
		gbLayout.setConstraints(fld, gbc);
		add(fld);
		
		return fld;
	}
	
	protected JButton addSaveButton(int y, String caption)
	{
		GridBagConstraints gbc = createButtonGbc(y);
		JButton btn = new JButton(caption);
		gbLayout.setConstraints(btn, gbc);
		add(btn);
		return btn;
	}

	protected void addHSpacePanel(int y) {
		GridBagConstraints gbc = createHSpacePnlGbc(y);
		JPanel pnl = new JPanel();
		gbLayout.setConstraints(pnl, gbc);
		add(pnl);
	}
	
	protected void addVSpacePanel(int height) {
		GridBagConstraints gbc = createVSpacePnlGbc(height);
		JPanel pnl = new JPanel();
		gbLayout.setConstraints(pnl, gbc);
		add(pnl);
	}

	private GridBagConstraints createTitleGbc(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 100;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		return gbc;
	}
	
	private GridBagConstraints createLabelGbc(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		return gbc;
	}

	private GridBagConstraints createFieldGbc(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 100;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		return gbc;
	}

	private GridBagConstraints createButtonGbc(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.gridwidth = 0;
		gbc.gridheight = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbc.insets = new Insets(2, 2, 2, 2);
		return gbc;
	}
	
	private GridBagConstraints createVSpacePnlGbc(int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = height;
		gbc.weightx = 0;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		return gbc;
	}

	private GridBagConstraints createHSpacePnlGbc(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 10;
		gbc.weighty = 0;
		gbc.ipady = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 0, 0);
		return gbc;
	}
	
}

