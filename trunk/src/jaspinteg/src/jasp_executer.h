
#ifndef HG_JASP_EXECUTER
#define HG_JASP_EXECUTER

#include "jasp_report.h"

#include <sstream>

class JasperExecuter {
private:
  JasperReport& m_rep;

  std::stringstream m_recvBuffer;
  BinaryBuffer* m_pdf;
  JasperExeclogList* m_execLog;

  const std::string rSep;           // Record Separator
  const std::string pSep;           // Param Separator
  const std::string vSep;           // Value Separator


  int recvLine(const int sd, std::string& rec);
  void sendLine(const int sd, std::string& rec) const;

  bool extractRecordId(const std::string& req, std::string& recordId) const;
  bool extractParam(const std::string& req, const std::string& name, std::string& value) const;

  bool checkRecordId(const std::string& req, const std::string& excected) const;
  bool checkParam(const std::string& req, const std::string& name, const std::string& excected) const;

  std::string& encodeParam(std::string& param) const;
  std::string& decodeParam(std::string& param) const;
  int getBsCount(const std::string& str, int p) const;
  int findNextOccurrence(const std::string& str, const char c) const;

  void clean();

  RETURN_CODE receiveWelcome(const int sd);
  RETURN_CODE sendJrxml(const int sd);
  RETURN_CODE sendDbInfo(const int sd);
  RETURN_CODE sendModifications(const int sd);
  RETURN_CODE reciveGenResult(const int sd);
  RETURN_CODE receiveExecutionLog(const int sd);
  RETURN_CODE receivePdf(const int sd);
  RETURN_CODE receiveByeBye(const int sd);

  RETURN_CODE doProtocol(const int sd);
public:
  JasperExecuter(JasperReport& rep);
  virtual ~JasperExecuter();

  RETURN_CODE execute();
};

#endif 
