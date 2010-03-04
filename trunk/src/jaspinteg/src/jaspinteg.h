
#ifndef HG_JSP_JASPINTEG
#define HG_JSP_JASPINTEG

#ifdef __cplusplus
extern "C" {
#endif


// types

typedef int MNGR_HANDLE;
typedef int REP_HANDLE;
typedef int RETURN_CODE;


// handle-values

const MNGR_HANDLE INVALID_MNGR_HANDLE                  = 0;
const REP_HANDLE INVALID_REP_HANDLE                    = 0;


// general error-codes

const RETURN_CODE JPI_NO_ERROR                         = 0;
const RETURN_CODE JPI_NOMNGR_ERROR                     = 1;
const RETURN_CODE JPI_NOREP_ERROR                      = 2;
const RETURN_CODE JPI_EXC_ERROR                        = 3;
const RETURN_CODE JPI_UNDEFINED_ERROR                  = 9;


// error-codes

  // jpi_open_mngr
  const RETURN_CODE JPI_OMNGR_INVALIDCONFIG_E          = 10;
  const RETURN_CODE JPI_OMNGR_GETHANDLE_E              = 11;
  const RETURN_CODE JPI_OMNGR_ADDTOTABLE_E             = 12;

  // jpi_open_rep
  const RETURN_CODE JPI_OPREP_INVALIDID_E              = 10;
  const RETURN_CODE JPI_OPREP_INITFAILED_E             = 11;

  // jpi_execute
  const RETURN_CODE JPI_EXREP_INVALIDSERVERDATA_E      = 10;
  const RETURN_CODE JPI_EXREP_DNSRESOLVE_E             = 11;
  const RETURN_CODE JPI_EXREP_CREATESOCK_E             = 12;
  const RETURN_CODE JPI_EXREP_SETSOCKOPT_E             = 13;
  const RETURN_CODE JPI_EXREP_CONNECT_E                = 14;

  const RETURN_CODE JPI_EXREP_CONNCLOSE_E              = 90;
  const RETURN_CODE JPI_EXREP_RECV_E                   = 91;
  const RETURN_CODE JPI_EXREP_INVALIDPARAM_E           = 92;

    // Send Jrxml
    const RETURN_CODE JPI_EXREP_OPENJRXML_E            = 20;
    const RETURN_CODE JPI_EXREP_AWAITJRXML_E           = 21;
    const RETURN_CODE JPI_EXREP_JRXMLRECEIVED_E        = 22;

    // Send Db Info
    const RETURN_CODE JPI_EXREP_INVALIDDBINFO_E        = 30;
    const RETURN_CODE JPI_EXREP_DBINFORECEIVED_E       = 31;

    // Send Modifications
    const RETURN_CODE JPI_EXREP_AWAITMODIF_E           = 40;
    const RETURN_CODE JPI_EXREP_MODIFRECEIVED_E        = 41;
    const RETURN_CODE JPI_EXREP_AWAITNOMODIF_E         = 42;

    //Receive Executioin Log
    const RETURN_CODE JPI_EXREP_EXLOG_E                = 80;
    const RETURN_CODE JPI_EXREP_EXLOGITEM_E            = 81;

    // Receive Pdfgen Results
    const RETURN_CODE JPI_EXREP_NOPDF_E                = 50;

    // Receive Pdf
    const RETURN_CODE JPI_EXREP_PDFSIZE_E              = 60;

    // Receive ByeBye
    const RETURN_CODE JPI_EXREP_BYEBYE_E               = 70;

  // jpi_save, jpi_save_random
  const RETURN_CODE JPI_SAVE_NOPDF_E                   = 10;
  const RETURN_CODE JPI_SAVE_OPENFILE_E                = 11;

  const RETURN_CODE JPI_SRD_TOOSMALLBUFFEE_E           = 30;
  const RETURN_CODE JPI_SRD_CONFIG_E                   = 31;
  const RETURN_CODE JPI_SRD_NOFILEFOUND_E              = 32;

  // jpi_buffer_length, jpi_buffer_resetread, jpi_buffer_read
  const RETURN_CODE JPI_BUFF_NOPDF_E                   = 10;

  // jpi_execlog_count, jpi_execlog_entry
  const RETURN_CODE JPI_EXLOG_NOLOG_E                  = 10;
  const RETURN_CODE JPI_EXLOG_WRONGINDEX_E             = 11;
  const RETURN_CODE JPI_EXLOG_TOOSMALLBUFFEE_E         = 12;

  // jpi_encode_param
  const RETURN_CODE JPI_ENP_TOOSMALLBUFFEE_E           = 10;


// Functions

void jpi_init();
void jpi_cleanup();

MNGR_HANDLE jpi_open_mngr(const char* root);
void jpi_close_mngr(const MNGR_HANDLE mHandle);

REP_HANDLE jpi_open_rep(const MNGR_HANDLE mHandle, const char* id);
void jpi_close_rep(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle);

RETURN_CODE jpi_execute(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle);
RETURN_CODE jpi_save(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const char* file);
int jpi_save_random(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, char* file, const int size);

int jpi_buffer_length(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle);
RETURN_CODE jpi_buffer_resetread(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle);
int jpi_buffer_read(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, void* buffer, const int size);

int jpi_execlog_count(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle);
int jpi_execlog_entry(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const int index, int* level, char* msg, const int size);

RETURN_CODE jpi_register_modification(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const char* mod);
int jpi_encode_param(const char* param, char* newParam, const int size);

RETURN_CODE getLastError();
char* jpi_version();


#ifdef __cplusplus
}
#endif


#endif
