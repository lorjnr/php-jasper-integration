package ch.bayo.jasper.server;

public class ErrorCodes {

	public static int RECJRXML_JRXMLSIZE_EXPECTED     = 1;
	public static int RECJRXML_PARAMSIZE_EXPECTED     = 2;
	public static int RECJRXML_UNABLETOWRITEFILE      = 3;

	public static int RECDB_DBINFO_EXPECTED           = 10;
	public static int RECDB_PARAMDRIVER_EXPECTED      = 11;
	public static int RECDB_PARAMCONN_EXPECTED        = 12;
	public static int RECDB_PARAMUSR_EXPECTED         = 13;
	public static int RECDB_PARAMPWD_EXPECTED         = 14;

	public static int RECMOD_MODCOUNT_EXPECTED        = 20;
	public static int RECMOD_PARAMCOUNT_EXPECTED      = 21;
	public static int RECMOD_MODIFICATION_EXPECTED    = 22;
	public static int RECMOD_PARAMMODSTR_EXPECTED     = 23;

	public static int SNDEXLG_AWAITEXECLOG_EXPECTED   = 30;
	public static int SNDEXLG_PARAMCOUNT_A_EXPECTED   = 31;
	public static int SNDEXLG_WRONG_A_COUNT           = 33;
	public static int SNDEXLG_EXECLOGREC_EXPECTED     = 34;
	public static int SNDEXLG_PARAMCOUNT_R_EXPECTED   = 35;
	public static int SNDEXLG_WRONG_R_COUNT           = 36;

	public static int EXECRES_CREATION_FAILED         = 40;

	public static int SNDPDF_AWAITPDF_EXPECTED        = 50;
	public static int SNDPDF_PARAMSIZE_A_EXPECTED     = 51;
	public static int SNDPDF_WRONGSIZE_A_EXPECTED     = 52;
	public static int SNDPDF_PDFRECEIVED_EXPECTED     = 53;
	public static int SNDPDF_PARAMSIZE_R_EXPECTED     = 54;
	public static int SNDPDF_WRONGSIZE_R_EXPECTED     = 55;
	
}
