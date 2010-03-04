
#include "jasp_executer.h"

#include "jasp_manager.h"

#include "conversion.h"
#include "binbuffer.h"

#include <netdb.h>
#include <fcntl.h>
#include <malloc.h>
#include <string.h>



//-----------------------------------------------------------------------------------------------//
//-- JasperExecuter -----------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperExecuter::JasperExecuter(JasperReport& rep)
: m_rep(rep), m_recvBuffer(), m_pdf(0), m_execLog(0), rSep("\n"), pSep(";"), vSep("=")
{
  clean();
}

//-----------------------------------------------------------------------------------------------//
JasperExecuter::~JasperExecuter()
{
  clean();
}

//-----------------------------------------------------------------------------------------------//
void JasperExecuter::clean()
{
  if (m_pdf != 0) {
    delete(m_pdf);
    m_pdf = 0;
  }
  if (m_execLog != 0) {
    delete(m_execLog);
    m_execLog = 0;
  }
}

//-----------------------------------------------------------------------------------------------//
int JasperExecuter::recvLine(const int sd, std::string& rec)
{
  rec = "";

  const int buffer_length = 512;
  char buffer[buffer_length];
  int op_result;

  std::string line = "";
  std::string recvTemp = "";

  for (;;) {
    std::getline(m_recvBuffer, line);
    if ( ! m_recvBuffer.fail() ) {
      rec = line;
      break;
    }
    m_recvBuffer.clear();

    recvTemp = "";
    for (;;) {
      op_result = recv(sd, buffer, buffer_length-1, 0);
      if ( op_result <= 0 ) return op_result;
      if (buffer[op_result-1] == rSep[0] ) {
        buffer[op_result-1] = 0;
        recvTemp = recvTemp + std::string(buffer);
        break;
      } else {
        buffer[op_result] = 0;
        recvTemp = recvTemp + std::string(buffer);
      }
    }
    m_recvBuffer << recvTemp;
  }

  return rec.length();
}

//-----------------------------------------------------------------------------------------------//
void JasperExecuter::sendLine(const int sd, std::string& rec) const
{
  rec.append(rSep);
  send(sd, rec.c_str(), rec.length(), 0);
}

//-----------------------------------------------------------------------------------------------//
bool JasperExecuter::extractRecordId(const std::string& req, std::string& recordId) const
{
  recordId = "";
  size_t p = req.find_first_of(pSep[0]);
  if ( p == std::string::npos ) return false;
  recordId = req.substr(0, p);
  return true;
}

//-----------------------------------------------------------------------------------------------//
int JasperExecuter::findNextOccurrence(const std::string& str, const char c) const
{
  size_t p;
  int offset;

  offset = 0;
  p = str.find_first_of(c);
  while (true) {
    if ( p == std::string::npos ) return p;
    if ( ( p < (str.length()-1) ) && ( str[p+1] == c ) ) {
      offset = (p + 2);
      p = str.find_first_of(c, offset);
    } else {
      return p;
    }
  }

  return 0;
}

//-----------------------------------------------------------------------------------------------//
bool JasperExecuter::extractParam(const std::string& req, const std::string& name, std::string& value) const
{
  value = "";
  size_t p = req.find_first_of(pSep[0]);
  if ( p == std::string::npos ) return false;

  std::string str = req;
  str.erase(0, (p+1));
  if ( str[str.length()-1] != pSep[0] ) str.append(pSep);

  std::string param;
  std::string n;
  std::string v;
  p = findNextOccurrence(str, pSep[0]);
  while ( p != std::string::npos ) {
    param = str.substr(0, p);
    str.erase(0, (p+1));

    p = param.find_first_of(vSep[0]);
    if ( p == std::string::npos ) return false;
    n = param.substr(0, p);
    v = param.substr((p+1), (param.length()-(p+1)));

    if ( n.compare(name) == 0) {
      value = decodeParam(v);
      return true;
    }

    p = findNextOccurrence(str, pSep[0]);
  }
}

//-----------------------------------------------------------------------------------------------//
bool JasperExecuter::checkRecordId(const std::string& req, const std::string& excected) const
{
  std::string rec_id;
  if ( ! extractRecordId(req, rec_id) ) return false;
  if ( rec_id.compare(excected) != 0 ) return false;
  return true;
}

//-----------------------------------------------------------------------------------------------//
bool JasperExecuter::checkParam(const std::string& req, const std::string& name, const std::string& excected) const
{
  std::string v;
  if ( ! extractParam(req, name, v) ) return false;
  if ( v.compare(excected) != 0 ) return false;
  return true;
}

//-----------------------------------------------------------------------------------------------//
int JasperExecuter::getBsCount(const std::string& str, int p) const
{
  int ret = 0;
  p = p-1;
  while (p >= 0) {
    if (str[p] == '\\') {
      ret = (ret + 1);
    } else {
      break;
    }
    p = (p-1);
  }
  return ret;
}

//-----------------------------------------------------------------------------------------------//
std::string& JasperExecuter::decodeParam(std::string& param) const
{
  size_t p;
  int offset;
  int bsCount;

  // pSep
  offset = 0;
  while (true) {
    p = param.find_first_of(pSep[0], offset);
    if (p == std::string::npos) break;
    if ( ( p < (param.length()-1) ) && ( param[p+1] == pSep[0] ) ) {
      param.erase(p, 1);
    }
    offset = (p+1);
  }

  // vSep
  offset = 0;
  while (true) {
    p = param.find_first_of(vSep[0], offset);
    if (p == std::string::npos) break;
    if ( ( p < (param.length()-1) ) && ( param[p+1] == vSep[0] ) ) {
      param.erase(p, 1);
    }
    offset = (p+1);
  }

  // \n
  offset = 0;
  while (true) {
    p = param.find("\\n", offset);
    if (p == std::string::npos) break;
    bsCount = getBsCount(param, p);
    if ( (bsCount % 2) == 0 ) {
      param.replace(p, 2, "\n");
      offset = (p+1);
    } else {
      offset = (p+2);
    }
  }

  // bs
  offset = 0;
  while (true) {
    p = param.find_first_of('\\', offset);
    if (p == std::string::npos) break;
    if ( ( p < (param.length()-1) ) && ( param[p+1] == '\\' ) ) {
      param.erase(p, 1);
    }
    offset = (p+1);
  }

  return param;
}

//-----------------------------------------------------------------------------------------------//
std::string& JasperExecuter::encodeParam(std::string& param) const
{
  int offset;
  size_t p;

  // pSep
  offset = 0;
  while (true) {
    p = param.find_first_of(pSep[0], offset);
    if (p != std::string::npos) {
      param.insert(p, pSep);
      offset = (p + 2);
    } else {
      break;
    }
  }

  // vSep
  offset = 0;
  while (true) {
    p = param.find_first_of(vSep[0], offset);
    if (p != std::string::npos) {
      param.insert(p, vSep);
      offset = (p + 2);
    } else {
      break;
    }
  }

  // \
  offset = 0;
  while (true) {
    p = param.find_first_of('\\', offset);
    if (p != std::string::npos) {
      param.insert(p, "\\");
      offset = (p + 2);
    } else {
      break;
    }
  }

  // \n
  offset = 0;
  while (true) {
    p = param.find_first_of(rSep[0], offset);
    if (p != std::string::npos) {
      param.replace(p, 1, "\\n");
      offset = (p + 2);
    } else {
      break;
    }
  }

  return param;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::receiveWelcome(const int sd)
{
  int op_result;
  std::string recvRec = "";

  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::sendJrxml(const int sd)
{
  std::string file_size_str = "";
  std::string sendRec = "";
  std::string recvRec = "";
  int op_result;
  void* buf;

  int fd = open(m_rep.getFilename().c_str(), O_RDONLY);
  if (fd == -1) return JPI_EXREP_OPENJRXML_E;

  // Send file-size
  struct stat f_stat;
  if ( fstat(fd, &f_stat) == -1) {
    close(fd);
    return JPI_EXREP_OPENJRXML_E;
  }
  IntToStr(f_stat.st_size, file_size_str);
  sendRec = "JrxmlSize"+pSep+"size" + vSep + encodeParam(file_size_str);
  sendLine(sd, sendRec);

  // Receive Expected size
  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) {
    close(fd);
    return JPI_EXREP_CONNCLOSE_E;
  }
  if ( op_result < 0 ) {
    close(fd);
    return JPI_EXREP_RECV_E;
  }
  if ( ! checkRecordId(recvRec, "AwaitJrxml") ) {
    close(fd);
    return JPI_EXREP_AWAITJRXML_E;
  }
  if ( ! checkParam(recvRec, "size", file_size_str) ) {
    close(fd);
    return JPI_EXREP_INVALIDPARAM_E;
  }

  // Send file
  buf = malloc(512);
  op_result = read(fd, buf, 512);
  while (op_result > 0) {
    send(sd, buf, op_result, 0);
    op_result = read(fd, buf, 512);
  }
  free(buf);

  close(fd);

  //Receive confirmation
  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "JrxmlReceived") ) return JPI_EXREP_JRXMLRECEIVED_E;
  if ( ! checkParam(recvRec, "size", file_size_str) ) return JPI_EXREP_INVALIDPARAM_E;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::sendDbInfo(const int sd)
{
  int op_result;
  std::string recvRec = "";
  std::string sendRec = "";

  std::string db_driver;
  std::string db_conn;
  std::string db_usr;
  std::string db_pwd;
  try {
    db_driver = m_rep.getManager().getConfig().getSecDatabase().getByKey("driver")->getValue();
    db_conn = m_rep.getManager().getConfig().getSecDatabase().getByKey("conn_str")->getValue();
    db_usr = m_rep.getManager().getConfig().getSecDatabase().getByKey("user")->getValue();
    db_pwd = m_rep.getManager().getConfig().getSecDatabase().getByKey("passwd")->getValue();
  } catch ( JaspItemNotFoundExc ) {
    return JPI_EXREP_INVALIDDBINFO_E;
  }

  sendRec = "DbInfo"+pSep+"driver"+vSep+encodeParam(db_driver)+pSep+"conn"+vSep+encodeParam(db_conn)+pSep+"usr"+vSep+encodeParam(db_usr)+pSep+"pwd"+vSep+encodeParam(db_pwd);
  sendLine(sd, sendRec);

  //Receive confirmation
  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "DbInfoReceived") ) return JPI_EXREP_DBINFORECEIVED_E;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::sendModifications(const int sd)
{
  int op_result;
  std::string recvRec = "";
  std::string sendRec = "";

  int count = 0;

  // Send count
  count = m_rep.getModifications().count();
  std::string count_str = "";
  IntToStr(count, count_str);
  sendRec = "ModificationCount"+pSep+"count" + vSep + encodeParam(count_str);
  sendLine(sd, sendRec);

  if (count > 0) {

    // Receive Expected count
    op_result = recvLine(sd, recvRec);
    if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
    if ( op_result < 0 ) return JPI_EXREP_RECV_E;
    if ( ! checkRecordId(recvRec, "AwaitModification") ) return JPI_EXREP_AWAITMODIF_E;
    if ( ! checkParam(recvRec, "count", count_str) ) return JPI_EXREP_INVALIDPARAM_E;

    // Send Modifications
    JasperModificationList::const_iterator itb = m_rep.getModifications().begin();
    JasperModificationList::const_iterator ite = m_rep.getModifications().end();
    for (JasperModificationList::const_iterator it=itb; it<ite; it++) {
      const JasperModification_Sp& item = *it;

      std::string mod_str = item->getModStr();

      sendRec = "Modification"+pSep+"modstr"+vSep+encodeParam(mod_str);
      sendLine(sd, sendRec);
    }

    // Receive confirmation
    op_result = recvLine(sd, recvRec);
    if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
    if ( op_result < 0 ) return JPI_EXREP_RECV_E;
    if ( ! checkRecordId(recvRec, "ModificationReceived") ) return JPI_EXREP_MODIFRECEIVED_E;

  } else {

    // Receive confirmation
    op_result = recvLine(sd, recvRec);
    if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
    if ( op_result < 0 ) return JPI_EXREP_RECV_E;
    if ( ! checkRecordId(recvRec, "AwaitNoModification") ) return JPI_EXREP_AWAITNOMODIF_E;

  }

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::receiveExecutionLog(const int sd)
{
  int op_result;
  std::string recvRec = "";
  std::string sendRec = "";

  std::string entry_count_str = "";
  int entry_count;

  void* buf;

  // Receive count
  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "ExecLogCount") ) return JPI_EXREP_EXLOG_E;
  if ( ! extractParam(recvRec, "count", entry_count_str) ) return JPI_EXREP_INVALIDPARAM_E;
  try {
    entry_count = StrToInt(entry_count_str);
  } catch ( JaspConversionExc ) {
    return 33;
  }

  // Confirm count
  sendRec = "AwaitExecLog"+pSep+"count"+vSep+encodeParam(entry_count_str);
  sendLine(sd, sendRec);

  // Receive entries
  JasperExeclogList* ll = new JasperExeclogList();
  std::string rec_id;
  std::string level_str;
  std::string msg;
  int ec = 0;
  while (true) {
    op_result = recvLine(sd, recvRec);
    if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
    if ( op_result < 0 ) return JPI_EXREP_RECV_E;
    if ( ! extractRecordId(recvRec, rec_id) ) return 33;
    if ( rec_id.compare("ExecLogEnd") == 0 ) break;
    if ( ! checkRecordId(recvRec, "ExecLogItem") ) return JPI_EXREP_EXLOGITEM_E;
    if ( ! extractParam(recvRec, "level", level_str) ) return JPI_EXREP_INVALIDPARAM_E;
    if ( ! extractParam(recvRec, "msg", msg) ) return JPI_EXREP_INVALIDPARAM_E;
    ec++;
    ll->add(new JasperExeclogItem(StrToInt(level_str), msg));
  }

  m_execLog = ll;

  // Send confirmation
  IntToStr(ec, entry_count_str);
  sendRec = "ExecLogReceived"+pSep+"count"+vSep+encodeParam(entry_count_str);
  sendLine(sd, sendRec);

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::reciveGenResult(const int sd)
{
  int op_result;
  std::string recvRec = "";

  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "PdfGenerated") ) return JPI_EXREP_NOPDF_E;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::receivePdf(const int sd)
{
  int op_result;
  std::string recvRec = "";
  std::string sendRec = "";

  std::string file_size_str = "";
  int file_size;

  void* buf;

  // Receive size
  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "PdfSize") ) return JPI_EXREP_PDFSIZE_E;
  if ( ! extractParam(recvRec, "size", file_size_str) ) return JPI_EXREP_INVALIDPARAM_E;
  try {
    file_size = StrToInt(file_size_str);
  } catch ( JaspConversionExc ) {
    return 33;
  }

  // Confirm size
  sendRec = "AwaitPdf"+pSep+"size"+vSep+encodeParam(file_size_str);
  sendLine(sd, sendRec);

  //Receive pdf
  BinaryBuffer* bb = new BinaryBuffer(file_size);
  buf = malloc(512);
  while (file_size > 0) {
    op_result = recv(sd, buf, 512, 0);
    if ( op_result == 0 ) {
      free(buf);
      delete(bb);
      return JPI_EXREP_CONNCLOSE_E;
    }
    if ( op_result < 0 ) {
      free(buf);
      delete(bb);
      return JPI_EXREP_RECV_E;
    }
    bb->write(buf, op_result);
    file_size = file_size - op_result;
  }
  free(buf);

  m_pdf = bb;

  //Send confirmation
  sendRec = "PdfReceived"+pSep+"size"+vSep+encodeParam(file_size_str);
  sendLine(sd, sendRec);

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::receiveByeBye(const int sd)
{
  int op_result;
  std::string recvRec = "";

  op_result = recvLine(sd, recvRec);
  if ( op_result == 0 ) return JPI_EXREP_CONNCLOSE_E;
  if ( op_result < 0 ) return JPI_EXREP_RECV_E;
  if ( ! checkRecordId(recvRec, "ByeBye") ) return JPI_EXREP_BYEBYE_E;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::doProtocol(const int sd)
{
  RETURN_CODE res;

  res = receiveWelcome(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = sendJrxml(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = sendDbInfo(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = sendModifications(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = receiveExecutionLog(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = reciveGenResult(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = receivePdf(sd);
  if ( res != JPI_NO_ERROR ) return res;

  res = receiveByeBye(sd);
  if ( res != JPI_NO_ERROR ) return res;

  m_rep.setExecLog(m_execLog);
  m_execLog = 0;

  m_rep.setPdfBuffer(m_pdf);
  m_pdf = 0;

  return JPI_NO_ERROR;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperExecuter::execute()
{
  std::string host = "";
  std::string port_str = "";
  try {
    host = m_rep.getManager().getConfig().getSecGeneral().getByKey("server")->getValue();
    port_str = m_rep.getManager().getConfig().getSecGeneral().getByKey("port")->getValue();
  } catch ( JaspItemNotFoundExc ) {
    return JPI_EXREP_INVALIDSERVERDATA_E;
  }
  std::string filename = m_rep.getFilename();
  int port = 0;
  try {
    port = StrToInt(port_str);
  } catch ( JaspConversionExc ) {
    return JPI_EXREP_INVALIDSERVERDATA_E;
  }
  const hostent* host_info = 0;
  for(int attempt=0; (host_info==0) && (attempt<3); ++attempt) host_info = gethostbyname(host.c_str());
  if ( host_info == 0 ) return JPI_EXREP_DNSRESOLVE_E;

  sockaddr_in dest_addr;
  memset(&dest_addr, 0, sizeof(dest_addr));
  dest_addr.sin_family = AF_INET;
  dest_addr.sin_port = htons(port);
  bcopy(host_info->h_addr, &(dest_addr.sin_addr.s_addr), host_info->h_length);

  int s;
  if ( (s = socket(PF_INET, SOCK_STREAM, 0)) == -1 ) return JPI_EXREP_CREATESOCK_E;

  struct timeval tv;
  tv.tv_sec = 30;
  tv.tv_usec = 0;
  if ( setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) < 0 ) return JPI_EXREP_SETSOCKOPT_E;

  if (connect(s, (sockaddr*)&dest_addr, sizeof(sockaddr)) == -1) return JPI_EXREP_CONNECT_E;

  RETURN_CODE res = JPI_NO_ERROR;

  try {

    clean();
    res = doProtocol(s);

  } catch ( ... ) {
    res = JPI_UNDEFINED_ERROR;
  }

  close(s);
  return res;
}

