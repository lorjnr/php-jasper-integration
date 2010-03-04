package ch.bayo.jasper.server;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Iterator;

import ch.bayo.lib.net.ClientContext;
import ch.bayo.jasper.model.modification.*;

import net.sf.jasperreports.engine.JRException;

import ch.bayo.lib.log.*;


class Client extends ClientContext {
	
	private enum StepResult { srSUCCEED, srERROR, srCLOSED, srFINISH, srTERMINATE }
	
	private char pSep = ';';
	private char vSep = '=';
	
	private ByteArrayOutputStream jrxmlStream = null;
	private DbConnection db_connection = null;
	private Modifications modifications = null;
	private JasperExecuter jExecuter;
	
	private void clean() {
		jrxmlStream = null;
		db_connection = null;
		modifications = null;
		jExecuter = null;
	}
		
	private String extractRecordId(String rec) {
		int p = rec.indexOf(pSep);
		if (p < 0) return null;
		return rec.substring(0, p);
	}
	
	private int findNextOccurrence(String str, char separator) {
		int p;
		int offset;

		offset = 0;
		p = str.indexOf(separator);
		while (true) {
			if ( p < 0 ) return p;
			if ( ( p < (str.length()-1) ) && ( str.charAt(p+1) == separator ) ) {
				offset = (p + 2);
				p = str.indexOf(separator, offset);
			} else {
				return p;
			}
		}	
	}

	private int getBsCount(String str, int p) {
		int ret = 0;
		p = p-1;
		while (p >= 0) {
			if ( str.charAt(p) == '\\' ) {
				ret = ret + 1;
			} else {
				break;
			}
			p = (p-1);
		}
		return ret;
	}

	private String decodeParam(String param) {
		int p;
		int offset;
		int bsCount;
		
		// pSep
		offset = 0;
		while (true) {
			p = param.indexOf(pSep, offset);
			if ( p < 0 ) break;
			if ( ( p < (param.length()-1) ) && ( param.charAt(p+1) == pSep ) ) {
				param = param.substring( 0, (p+1) ) + param.substring( (p+2), param.length() );
			}
			offset = (p+1);
		}

		// vSep
		offset = 0;
		while (true) {
			p = param.indexOf(vSep, offset);
			if ( p < 0 ) break;
			if ( ( p < (param.length()-1) ) && ( param.charAt(p+1) == vSep ) ) {
				param = param.substring( 0, (p+1) ) + param.substring( (p+2), param.length() );
			}
			offset = (p+1);
		}
		
		// \n
		offset = 0;
		while (true) {
			p = param.indexOf("\\n", offset);
			if ( p < 0 ) break;
			bsCount =getBsCount(param, p);
			if ( (bsCount % 2) == 0 ) {
				param = param.substring( 0, p ) + "\n" + param.substring( (p+2), param.length() );
				offset = (p+1);
			} else {
				offset = (p+2);
			}
		}
		
		// \
		offset = 0;
		while (true) {
			p = param.indexOf("\\", offset);
			if ( p < 0 ) break;
			if ( ( p < (param.length()-1) ) && ( param.charAt(p+1) == '\\' ) ) {
				param = param.substring( 0, (p+1) ) + param.substring( (p+2), param.length() );
			}
			offset = (p+1);
		}
		
		return param;
	}
	
	private String encodeParam(String param) {
		int offset;
		int p;
		
		// pSep
		offset = 0;
		while (true) {
			p = param.indexOf(pSep, offset);
			if (p >= 0) {
				param = param.substring( 0, (p+1) ) + pSep + param.substring( (p+1), param.length() );
				offset = (p + 2);
			} else {
				break;
			}
		}

		// vSep
		offset = 0;
		while (true) {
			p = param.indexOf(vSep, offset);
			if (p >= 0) {
				param = param.substring( 0, (p+1) ) + vSep + param.substring( (p+1), param.length() );
				offset = (p + 2);
			} else {
				break;
			}
		}

		// \
		offset = 0;
		while (true) {
			p = param.indexOf('\\', offset);
			if (p >= 0) {
				param = param.substring( 0, (p+1) ) + '\\' + param.substring( (p+1), param.length() );
				offset = (p + 2);
			} else {
				break;
			}
		}

		// \n
		offset = 0;
		while (true) {
			p = param.indexOf('\n', offset);
			if (p >= 0) {
				param = param.substring( 0, p ) + "\\n" + param.substring( (p+1), param.length() );
				offset = (p + 2);
			} else {
				break;
			}
		}
		
		return param;
	}
	
	private String extractParam(String rec, String name) {
		int p = rec.indexOf(pSep);
		if (p < 0) return null;
		String str = rec.substring(p+1);

		if (str.charAt(str.length()-1) != pSep) str = str + pSep;
		p = findNextOccurrence(str, pSep);
		while (p >= 0) {
			String param = str.substring(0, p);
			str = str.substring(p+1);
			p = param.indexOf(vSep);
			if (p < 0) return null;
			String n = param.substring(0, p);
			if (n.compareTo(name) == 0) {
				String v = param.substring(p+1);
				return decodeParam(v);
			}
			p = findNextOccurrence(str, pSep);
		}
		return null;
	}
	
	private boolean checkRecordId(String rec, String expected) {
		String rec_id = extractRecordId(rec);
		if (rec_id == null) return false;
		return (rec_id.compareTo(expected) == 0);
	}
	
	private boolean sendLine(String rec) {
		String line = rec + "\n";
		try {
			getSocket().getOutputStream().write(line.getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	private boolean sendProtError(int code, String msg) {
		String code_str = Integer.toString(code);
		String rec = "ProtError"+pSep+"code"+vSep+encodeParam(code_str)+pSep+"msg"+vSep+encodeParam(msg);
		return sendLine(rec);
	}
	
	private StepResult sendWelcome(BufferedReader reader) {
		String sendRec = "Hi, Jasper Report Server";
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		return StepResult.srSUCCEED;
	}
	
	private StepResult receiveJrxml(BufferedReader reader) {
		String recvRec = "";
		String sendRec = "";

		String file_size_str = "";
		int file_size = 0;
		
		// Receive jrxml-size
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "JrxmlSize") ) {
				if ( ! sendProtError(ErrorCodes.RECJRXML_JRXMLSIZE_EXPECTED, "JrxmlSize expected") ) return StepResult.srERROR;
				continue;
			}
			
			file_size_str = extractParam(recvRec, "size");
			if (file_size_str == null) {
				if ( ! sendProtError(ErrorCodes.RECJRXML_PARAMSIZE_EXPECTED, "JrxmlSize and param size expected") ) return StepResult.srERROR;
				continue;
			}

			file_size = Integer.valueOf(file_size_str);
			break;
		}
		
		//Confirm jrxml-size
		sendRec = "AwaitJrxml" + pSep + "size" + vSep + encodeParam(file_size_str);
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;

		//Receive File
		jrxmlStream = new ByteArrayOutputStream();
		boolean file_recv_restart = false;
		while ( true ) {
			int read_res = -1;
			byte[] buffer = new byte[512];

			if ( file_recv_restart ) jrxmlStream.reset();
			
			try {
				read_res = getSocket().getInputStream().read(buffer);
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				file_recv_restart = false;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			
			if (read_res == -1) return StepResult.srCLOSED;
			
			if (read_res > 0) {
				try {
					jrxmlStream.write(buffer, 0, read_res);
				} catch (Exception e) {
					if ( ! sendProtError(ErrorCodes.RECJRXML_UNABLETOWRITEFILE, "Unable to write JrxmlStream") ) return StepResult.srERROR;
					file_recv_restart = true;
					continue;
				}
			}
			
			if (jrxmlStream.size() >= file_size) break;
		}

		//Confirm file
		file_size_str = Integer.toString(jrxmlStream.size());
		sendRec = "JrxmlReceived" + pSep + "size" + vSep + encodeParam(file_size_str);
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		
		return StepResult.srSUCCEED;
	}
	
	private StepResult receiveDbInfo(BufferedReader reader) {
		String recvRec = "";
		String sendRec = "";
		
		// Receive information
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "DbInfo") ) {
				if ( ! sendProtError(ErrorCodes.RECDB_DBINFO_EXPECTED, "DbInfo expected") ) return StepResult.srERROR;
				continue;
			}
			
			String db_driver = "";
			String db_conn = "";
			String db_usr = "";
			String db_pwd = "";

			db_driver = extractParam(recvRec, "driver");
			if (db_driver == null) {
				if ( ! sendProtError(ErrorCodes.RECDB_PARAMDRIVER_EXPECTED, "DbInfo and param driver expected") ) return StepResult.srERROR;
				continue;
			}
			db_conn = extractParam(recvRec, "conn");
			if (db_conn == null) {
				if ( ! sendProtError(ErrorCodes.RECDB_PARAMCONN_EXPECTED, "DbInfo and param conn expected") ) return StepResult.srERROR;
				continue;
			}
			db_usr = extractParam(recvRec, "usr");
			if (db_usr == null) {
				if ( ! sendProtError(ErrorCodes.RECDB_PARAMUSR_EXPECTED, "DbInfo and param usr expected") ) return StepResult.srERROR;
				continue;
			}
			db_pwd = extractParam(recvRec, "pwd");
			if (db_pwd == null) {
				if ( ! sendProtError(ErrorCodes.RECDB_PARAMPWD_EXPECTED, "DbInfo and param pwd expected") ) return StepResult.srERROR;
				continue;
			}
			
			db_connection = new DbConnection(db_driver, db_conn, db_usr, db_pwd);

			
			break;
		}

		//Confirm information
		sendRec = "DbInfoReceived" + pSep;
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		
		return StepResult.srSUCCEED;
	}
	
	private StepResult receiveModifications(BufferedReader reader) {
		String recvRec = "";
		String sendRec = "";

		String count_str = "";
		int count = 0;

		// Receive count
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "ModificationCount") ) {
				if ( ! sendProtError(ErrorCodes.RECMOD_MODCOUNT_EXPECTED, "ModificationCount expected") ) return StepResult.srERROR;
				continue;
			}
			
			count_str = extractParam(recvRec, "count");
			if (count_str == null) {
				if ( ! sendProtError(ErrorCodes.RECMOD_PARAMCOUNT_EXPECTED, "ModificationCount and param count expected") ) return StepResult.srERROR;
				continue;
			}

			count = Integer.valueOf(count_str);
			break;
		}
		
		
		// Receive modifications
		if (count > 0) {

			// Confirm count
			sendRec = "AwaitModification" + pSep + "count" + vSep + encodeParam(count_str);
			if ( ! sendLine(sendRec) ) return StepResult.srERROR;

			String mod_str;
			
			modifications = new Modifications();
			for (int i=0; i<count; i++) {
				// Receive jrxml-size
				while (true) {
					try {
						recvRec = reader.readLine();
					} catch (SocketTimeoutException e) {
						if ( getWantTerminate() ) return StepResult.srTERMINATE;
						continue;
					} catch (IOException e) {
						return StepResult.srERROR;
					}
					if (recvRec == null) return StepResult.srCLOSED;
					
					if ( ! checkRecordId(recvRec, "Modification") ) {
						if ( ! sendProtError(ErrorCodes.RECMOD_MODIFICATION_EXPECTED, "Modification expected") ) return StepResult.srERROR;
						continue;
					}
					
					mod_str = extractParam(recvRec, "modstr");
					if (mod_str == null) {
						if ( ! sendProtError(ErrorCodes.RECMOD_PARAMMODSTR_EXPECTED, "Modification and param modstr expected") ) return StepResult.srERROR;
						continue;
					}
					Modification m = new Modification(mod_str);
					modifications.add(m);

					break;
				}
			}

			//Confirm
			sendRec = "ModificationReceived" + pSep;
			if ( ! sendLine(sendRec) ) return StepResult.srERROR;
			
		} else {
			//Confirm
			sendRec = "AwaitNoModification" + pSep;
			if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		}
		
		return StepResult.srSUCCEED;
	}
	
	private StepResult executeReport(BufferedReader reader) {
		ByteArrayInputStream jrxmlIStream = new ByteArrayInputStream(jrxmlStream.toByteArray());
		try {
			jrxmlStream.close();
		} catch(IOException e) {
			//Ignore
		}
		jrxmlStream = null;
		
		jExecuter = new JasperExecuter(jrxmlIStream, db_connection, modifications);
		try {
			jExecuter.execute();
		} catch (JRException e) {
			//Ignore
		}
		db_connection = null;
		modifications = null;

		return StepResult.srSUCCEED;
	}

	private StepResult sendExecutionLog(BufferedReader reader) {
		String recvRec = "";
		String sendRec = "";
		
		String entry_count_str = "";
		int entry_count = 0;

		LogList executionLog = jExecuter.getExecutionLog();

		// Send entry count
		entry_count_str = Integer.toString(executionLog.getCount());
		sendRec = "ExecLogCount" + pSep + "count" + vSep + encodeParam(entry_count_str);
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		
		// Read expected count
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "AwaitExecLog") ) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_AWAITEXECLOG_EXPECTED, "AwaitExecLog expected") ) return StepResult.srERROR;
				continue;
			}
			
			entry_count_str = extractParam(recvRec, "count");
			if (entry_count_str == null) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_PARAMCOUNT_A_EXPECTED, "AwaitExecLog and param count expected") ) return StepResult.srERROR;
				continue;
			}

			entry_count = Integer.valueOf(entry_count_str);
			if (entry_count != executionLog.getCount()) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_WRONG_A_COUNT, "Wrong count") ) return StepResult.srERROR;
				continue;
			}
			break;
		}
		
		// Send ExecutionLog
		{
			Iterator it = executionLog.getIterator();
			String level_str = "";
			String msg = "";
			while (it.hasNext()) {
				LogItem li = (LogItem)it.next();
				msg = li.getMsg();
				level_str = Integer.toString(li.getLevel());
				sendRec = "ExecLogItem" + pSep + "level" + vSep + encodeParam(level_str) + pSep + "msg" + vSep + encodeParam(msg);
				if ( ! sendLine(sendRec) ) return StepResult.srERROR;
			}
		}

		// Send entry count
		sendRec = "ExecLogEnd" + pSep;
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;

		// Receive confirmation
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "ExecLogReceived") ) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_EXECLOGREC_EXPECTED, "ExecLogReceived expected") ) return StepResult.srERROR;
				continue;
			}
			
			entry_count_str = extractParam(recvRec, "count");
			if (entry_count_str == null) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_PARAMCOUNT_R_EXPECTED, "ExecLogReceived and param count expected") ) return StepResult.srERROR;
				continue;
			}

			entry_count = Integer.valueOf(entry_count_str);
			if (entry_count != executionLog.getCount()) {
				if ( ! sendProtError(ErrorCodes.SNDEXLG_WRONG_R_COUNT, "Wrong count") ) return StepResult.srERROR;
				executionLog = null;
				return StepResult.srFINISH;
			}
			break;
		}
		
		return StepResult.srSUCCEED;
	}

	private StepResult sendExecutionResult(BufferedReader reader) {
		if ( jExecuter.getPdf() != null ) {
			String sendRec = "";
			sendRec = "PdfGenerated" + pSep;
			if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		} else {
			if ( ! sendProtError(ErrorCodes.EXECRES_CREATION_FAILED, "Pdf creation failed") ) return StepResult.srERROR;
			jExecuter = null;
			return StepResult.srFINISH;
		}
		return StepResult.srSUCCEED;
	}
	
	private StepResult sendPdf(BufferedReader reader) {
		String recvRec = "";
		String sendRec = "";
		
		String file_size_str = "";
		int file_size = 0;

		// Send size
		file_size_str = Integer.toString(jExecuter.getPdfSize());
		sendRec = "PdfSize" + pSep + "size" + vSep + encodeParam(file_size_str);
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		
		// Read expection size
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "AwaitPdf") ) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_AWAITPDF_EXPECTED, "AwaitPdf expected") ) return StepResult.srERROR;
				continue;
			}
			
			file_size_str = extractParam(recvRec, "size");
			if (file_size_str == null) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_PARAMSIZE_A_EXPECTED, "AwaitPdf and param size expected") ) return StepResult.srERROR;
				continue;
			}

			file_size = Integer.valueOf(file_size_str);
			if (file_size != jExecuter.getPdfSize()) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_WRONGSIZE_A_EXPECTED, "Wrong size") ) return StepResult.srERROR;
				continue;
			}
			break;
		}
		
		// Send pdf
		{
			ByteArrayInputStream is_pdf = jExecuter.getPdf();
			int read_result = -1;
			byte[] buffer = new byte[512];
			while (true) {
				try {
					read_result = is_pdf.read(buffer);
				} catch (IOException e) {
					return StepResult.srERROR;
				}
				if (read_result > 0) {
					try {
						getSocket().getOutputStream().write(buffer, 0, read_result);
					} catch (IOException e) {
						return StepResult.srERROR;
					}
				} else {
					break;
				}
			}
		}
		
		// Receive confirmation
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srTERMINATE;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
			
			if ( ! checkRecordId(recvRec, "PdfReceived") ) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_PDFRECEIVED_EXPECTED, "PdfReceived expected") ) return StepResult.srERROR;
				continue;
			}
			
			file_size_str = extractParam(recvRec, "size");
			if (file_size_str == null) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_PARAMSIZE_R_EXPECTED, "PdfReceived and param size expected") ) return StepResult.srERROR;
				continue;
			}

			file_size = Integer.valueOf(file_size_str);
			if (file_size != jExecuter.getPdfSize()) {
				if ( ! sendProtError(ErrorCodes.SNDPDF_WRONGSIZE_R_EXPECTED, "Wrong size") ) return StepResult.srERROR;
				jExecuter = null;
				return StepResult.srFINISH;
			}
			break;
		}
		
		jExecuter = null;
		
		return StepResult.srSUCCEED;
	}

	private StepResult sendByeBye(BufferedReader reader) {
		String sendRec = "ByeBye" + pSep;
		if ( ! sendLine(sendRec) ) return StepResult.srERROR;
		return StepResult.srSUCCEED;
	}
	
	private StepResult receiveDisconnect(BufferedReader reader) {
		String recvRec = "";
		while (true) {
			try {
				recvRec = reader.readLine();
			} catch (SocketTimeoutException e) {
				if ( getWantTerminate() ) return StepResult.srERROR;
				continue;
			} catch (IOException e) {
				return StepResult.srERROR;
			}
			if (recvRec == null) return StepResult.srCLOSED;
		}
	}
		
	public void doThings() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));		
		clean();
		
		StepResult res;
		
		res = sendWelcome(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = receiveJrxml(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = receiveDbInfo(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = receiveModifications(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = executeReport(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = sendExecutionLog(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = sendExecutionResult(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}
		
		res = sendPdf(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = sendByeBye(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}

		res = receiveDisconnect(reader);
		switch (res) {
			case srERROR: return;
			case srCLOSED: return;
			case srTERMINATE: return;
			case srFINISH: receiveDisconnect(reader); return;
		}
		
	}
	
}
