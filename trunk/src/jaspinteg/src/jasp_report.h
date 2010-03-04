
#ifndef HG_JASP_REPORT
#define HG_JASP_REPORT

#include "jaspinteg.h"
#include "jasp_config.h"
#include "jasp_modifications.h"

#include "smartpointer.h"
#include "binbuffer.h"
#include "jasp_executionlog.h"

#include <string>
#include <vector>


class JasperManager;

class JasperReport {
private:
  JasperManager& m_mngr;
  ConfigGroup_Sp m_config;

  JasperModifications m_modifications;

  BinaryBuffer* m_pdf;
  JasperExeclogList* m_execLog;

  bool fileExists(const std::string& filename) const;

  void check() const;
public:
  JasperReport(JasperManager& mngr, ConfigGroup* repConfig);
  virtual ~JasperReport();

  RETURN_CODE execute();
  RETURN_CODE save(const std::string& file);
  RETURN_CODE save_random(std::string& file);

  std::string getFilename() const;

  JasperModifications& getModifications();
  const JasperModifications& getModifications() const;

  bool hasPdfBuffer() const;
  void setPdfBuffer(BinaryBuffer* pdfBuf);
  BinaryBuffer& getPdfBuffer_ref() const;
  const BinaryBuffer* getPdfBuffer_ptr() const;

  bool hasExecLog() const;
  void setExecLog(JasperExeclogList* execLog);
  JasperExeclogList& getExeclog_ref() const;
  const JasperExeclogList* getExeclog_ptr() const;

  JasperManager& getManager() const;
};

typedef CountedSp<JasperReport> JasperReport_Sp;

class JasperReportItem {
private:
  REP_HANDLE m_handle;
  JasperReport_Sp m_report;
public:
  JasperReportItem(REP_HANDLE& handle, JasperReport* report);
  virtual ~JasperReportItem();

  REP_HANDLE getHandle() const;

  JasperReport_Sp& getReport();
  const JasperReport_Sp& getReport() const;
};

typedef CountedSp<JasperReportItem> JasperReportItem_Sp;
typedef std::vector<JasperReportItem_Sp> JasperReportItems;

class JasperReportTable {
private:
  JasperReportItems m_items;

  bool sameHandle(const REP_HANDLE& handle, const JasperReportItem_Sp& item) const;
public:
  JasperReportTable();
  virtual ~JasperReportTable();

  void add(JasperReportItem* item);

  const JasperReportItem_Sp& getByHandle(const REP_HANDLE& handle) const;
  JasperReportItem_Sp& getByHandle(const REP_HANDLE& handle);

  void removeByHandle(const REP_HANDLE& handle);

  REP_HANDLE getFreeHandle() const;
};

class ReportConfig;

class JasperReports {
private:
  JasperManager& m_mngr;
  JasperReportTable m_reports;
public:
  JasperReports(JasperManager& mngr);
  virtual ~JasperReports();

  REP_HANDLE openNew(ConfigGroup* repConfig);
  void close(const REP_HANDLE& handle);

  const JasperReport* getReport(const REP_HANDLE& handle) const;
  JasperReport* getReport(const REP_HANDLE& handle);
};

#endif
 
