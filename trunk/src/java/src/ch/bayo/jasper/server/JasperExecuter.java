package ch.bayo.jasper.server;

import java.io.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRProperties;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;

import ch.bayo.jasper.model.ReportModel;
import ch.bayo.jasper.model.modification.*;

import ch.bayo.lib.log.*;


public class JasperExecuter {
	
	private ByteArrayInputStream jrxml;
	
	private JasperReport jReport;
	private JasperPrint jPrint;
	
	private ByteArrayInputStream jPdf;
	private int jPdfSize;

	private DbConnection conn;
	private Modifications modifications;
	
	private LogList executionLog;
	
	public JasperExecuter(ByteArrayInputStream jrxml, DbConnection conn, Modifications modifications) {
		this.jrxml = jrxml;
		this.conn = conn;
		this.modifications = modifications;
		this.executionLog = new LogList();
		clean();
	}
	
	private void clean() {
		this.jReport = null;
		this.jPrint = null;
		this.jPdf = null;
		this.jPdfSize = 0;
	}
	
	public ByteArrayInputStream getPdf() {
		return jPdf;
	}
	
	public int getPdfSize() {
		return jPdfSize;
	}
	
	public LogList getExecutionLog() {
		return this.executionLog;
	}

	public void execute() throws JRException {
		clean();
	
		//Compile
		
		JRProperties.backupProperties();
		//JRProperties.setProperty(JRProperties.COMPILER_XML_VALIDATION, false);
		
		try {
			jReport = JasperCompileManager.compileReport(jrxml);
		} catch (JRException e) {
			executionLog.addError(e.getMessage());
			e.printStackTrace();
			return;
		}
	
		JRProperties.restoreProperties();
		
		//Apply modifications
		ReportModel model;
		try {
			model = new ReportModel(jReport);
		} catch (Exception e) {
			executionLog.addError(e.getMessage());
			e.printStackTrace();
			return;
		}

		try {
			model.applyModifications(modifications, executionLog);
		} catch (Exception e) {
			executionLog.addError(e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//FillFrom File
		Connection db_conn = null;
		try {
			db_conn = conn.getConnection();
		} catch (SQLException e) {
			db_conn = null;
		} catch (ClassNotFoundException e) {
			db_conn = null;
		}
		
		Map params = null;
		if (model.getParameters() != null) {
			params = model.getParameters().getParamMap();
		}

		try {
			jPrint = JasperFillManager.fillReport(jReport, params, db_conn);
		} catch (JRException e) {
			executionLog.addError(e.getMessage());
			e.printStackTrace();
			return;
		}

		//View
		//JasperViewer.viewReport(jPrint);
		
		//Generate Pdf
		byte[] pdf = null;

		try {
			pdf = JasperExportManager.exportReportToPdf(jPrint);
		} catch (JRException e) {
			executionLog.addError(e.getMessage());
			e.printStackTrace();
			return;
		}

		jPdfSize = pdf.length;
		jPdf = new ByteArrayInputStream(pdf);

		executionLog.addInfo("Report generated!");
	}

}
