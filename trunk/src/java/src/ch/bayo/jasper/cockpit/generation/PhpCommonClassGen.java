package ch.bayo.jasper.cockpit.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.bayo.jasper.model.generation.CommonGeneratorBase;

public class PhpCommonClassGen {
	
	private File out_dir;
	private BufferedWriter out;
	
	public PhpCommonClassGen(File out_dir) {
		this.out_dir = out_dir;
		this.out = null;
	}
	
	public boolean beginFile() {
		if (out == null) {
			try {
				FileWriter fstream = new FileWriter(out_dir.getAbsoluteFile()+"/common_classes.php");
				out = new BufferedWriter(fstream);
				out.write("<?php\n");
				out.write("\n");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean endFile() {
		if (out != null) {
			try {
				out.write("?>\n");
				out.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean genCommonClass(CommonGeneratorBase gen) {
		try {
			gen.generateClass(out);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}

}
