package ch.bayo.jasper.cockpit.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.ReportModel;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.GeneratorBase;

class PhpRepGen {
	
	private Report report;
	private ReportModel model;
	
	PhpRepGen(Report report) {
		this.report = report;
		this.model = report.getRepModel();
	}
	
	private boolean genPhp(BufferedWriter out) throws Exception {
		
		ClassGeneratorBase gen = model.getGenerator(report, GeneratorBase.GenLanguages.glPHP);
		if (gen != null) {
			out.write("<?php\n");
			out.write("\n");
			out.write("include 'common_classes.php';\n");
			out.write("\n");
			gen.generateClass(out);
			out.write("?>\n");
		}
		
		return true;
	}
	
	public boolean execute(File out_dir) {
		
		//report.getRepParameters();
		
		try{
			FileWriter fstream = new FileWriter(out_dir.getAbsoluteFile()+"/"+report.getId()+".php");
			BufferedWriter out = new BufferedWriter(fstream);
			
			genPhp(out);
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
