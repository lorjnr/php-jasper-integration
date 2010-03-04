package ch.bayo.jasper.model;

import net.sf.jasperreports.engine.*;

import java.util.Iterator;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.CommonGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.generation.GeneratorBase;
import ch.bayo.jasper.model.modification.Modifiable;
import ch.bayo.jasper.model.modification.Modification;
import ch.bayo.jasper.model.modification.Modifications;

import ch.bayo.jasper.generation.php.ReportModel_GenPhp;
import ch.bayo.jasper.generation.php.ReportModel_Common_GenPhp;

import ch.bayo.lib.log.*;


public class ReportModel implements Modifiable, Generatable {
	
	private JasperReport jReport;
	
	private Parameters parameters;
	
	private Band titleBand;
	private Band detailBand;
	
	public ReportModel(JasperReport jReport) {
		this.jReport = jReport;
		
		parameters = null;
		titleBand = null;
		detailBand = null;
		
		parseReport();
	}
	
	private void parseReport() {

		parameters = new Parameters(jReport.getParameters());
		titleBand = new Band(jReport.getTitle(), "TitleBand");
		detailBand = new Band(jReport.getDetail(), "DetailBand");

	}

	public Parameters getParameters() {
		return parameters;
	}
	
	public Band getTitleBand() {
		return titleBand;
	}
	
	public Band getDetailBand() {
		return detailBand;
	}
	
	public boolean applyModification(Modification modification, LogList modificationLog) {
		boolean b;
		b = parameters.applyModification(modification, modificationLog);
		if (b) return true;
		b = titleBand.applyModification(modification, modificationLog);
		if (b) return true;
		b = detailBand.applyModification(modification, modificationLog);
		if (b) return true;
		modificationLog.addWarning("No consumer found for modification "+modification.getId());
		return false;
	}
	
	public void applyModifications(Modifications modifications, LogList modificationLog) {
		if (modifications != null) {
			Iterator it = modifications.getIterator();
			while (it.hasNext()) {
				Modification m = (Modification)it.next();
				if (m != null) this.applyModification(m, modificationLog);
			}
		}
	}

	public static CommonGeneratorBase getCommonGenerator(GeneratorBase.GenLanguages language) {
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new ReportModel_Common_GenPhp();
		}
		return null;
	}

	public ClassGeneratorBase getGenerator(Report report, GeneratorBase.GenLanguages language)
	{
		if (language == GeneratorBase.GenLanguages.glPHP) {
			return new ReportModel_GenPhp(report, this);
		}
		return null;
	}

}
