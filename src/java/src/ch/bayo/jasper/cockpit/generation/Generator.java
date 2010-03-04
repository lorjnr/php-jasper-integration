package ch.bayo.jasper.cockpit.generation;

import java.io.*;
import java.util.Iterator;

import javax.swing.JOptionPane;

import ch.bayo.jasper.cockpit.Project;
import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.cockpit.ReportFacade;
import ch.bayo.jasper.model.ReportModel;
import ch.bayo.jasper.model.elements.StaticText;
import ch.bayo.jasper.model.generation.GeneratorBase;

public class Generator {
	
	private Project project;
	
	public Generator(Project project) {
		this.project = project;
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
	
	private boolean clearDirectory(File path) {
        if( path.exists() ) {
          File[] files = path.listFiles();
          for(int i=0; i<files.length; i++) {
             if( ! files[i].isDirectory() ) {
               files[i].delete();
             //} else {
             //  deleteDirectory(files[i]);
             }
          }
        }
        return true;
        //return( path.delete() );
	}
	
	public boolean execute() {
		
		File jasp_root_dir = new File(project.getGenJaspRoot());
		File class_out_dir = new File(project.getGenClassDir());
		File rep_out_dir = new File(jasp_root_dir.getAbsoluteFile() + "/" + project.getRepLocation());
		File out_out_dir = new File(jasp_root_dir.getAbsoluteFile() + "/" + project.getOutLocation());
		
		File conf_file = new File(jasp_root_dir.getAbsoluteFile() + "/jasper.conf");
		
		if ( conf_file.exists() ) conf_file.delete();
		if ( rep_out_dir.exists() ) clearDirectory(rep_out_dir);
		if ( class_out_dir.exists() ) clearDirectory(class_out_dir);
		if ( out_out_dir.exists() ) clearDirectory(out_out_dir);
		
		Iterator it;
		
		try{
			FileWriter fstream = new FileWriter(conf_file.getAbsoluteFile());
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("\n");
			out.write("[general]\n");
			out.write("rep_location="+project.getRepLocation()+"\n");
			out.write("out_location="+project.getOutLocation()+"\n");
			out.write("server="+project.getRepServer()+"\n");
			out.write("port="+Integer.toString(project.getRepServerPort())+"\n");
			out.write("max_pdf_storage="+Integer.toString(project.getMaxPdfStorage())+"\n");
			out.write("min_pdf_lifetime="+Integer.toString(project.getMinPdfLifetime())+"\n");
			out.write("\n");
			out.write("[database]\n");
			out.write("driver="+project.getDbDriver()+"\n");
			out.write("conn_str="+project.getDbConnStr()+"\n");
			out.write("user="+project.getDbUser()+"\n");
			out.write("passwd="+project.getDbPwd()+"\n");
			out.write("\n");
			
			it = project.getReports().getIterator();
			while (it.hasNext()) {
				ReportFacade rf = (ReportFacade)it.next();
				Report r = rf.getReport();
				if (r != null) {
					out.write("["+r.getId()+"]\n");
					out.write("filename="+extractFileName(new File(r.getRepLocation()))+"\n");
					out.write("\n");
				}
			}

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if ( ! rep_out_dir.exists() ) rep_out_dir.mkdir();
		if ( ! out_out_dir.exists() ) out_out_dir.mkdir();
		if ( ! class_out_dir.exists() ) class_out_dir.mkdir();
		
		it = project.getReports().getIterator();
		while (it.hasNext()) {
			ReportFacade rf = (ReportFacade)it.next();
			Report r = rf.getReport();
			if (r != null) {
				String s = r.getRepLocation();
				String d = rep_out_dir.getAbsolutePath()+"/"+extractFileName(new File(r.getRepLocation()));
				
				File sf = new File(s);
				File df = new File(d);
				
				if ( df.exists() ) df.delete();
				if ( ! sf.exists() ) {
					JOptionPane.showMessageDialog(null, "Report " + s + "does not exist", "Missing file", JOptionPane.ERROR_MESSAGE);
					continue;
				}
				
				copyfile(sf, df);
			}
		}
		
		PhpCommonClassGen cg = new PhpCommonClassGen(class_out_dir);
		cg.beginFile();
		cg.genCommonClass(ReportModel.getCommonGenerator(GeneratorBase.GenLanguages.glPHP));
		cg.genCommonClass(StaticText.getCommonGenerator(GeneratorBase.GenLanguages.glPHP));
		cg.endFile();		

		it = project.getReports().getIterator();
		while (it.hasNext()) {
			ReportFacade rf = (ReportFacade)it.next();
			Report r = rf.getReport();
			if (r != null) {
				
				PhpRepGen phpGen = new PhpRepGen(r);
				boolean b = phpGen.execute(class_out_dir);
				if ( ! b ) return false;
				
			}
		}

		return true;
	}
	

}
