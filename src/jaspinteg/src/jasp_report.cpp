
#include "jasp_report.h"

#include "jasp_manager.h"
#include "last_error.h"

#include "jasp_executer.h"
#include "jasp_saver.h"

#include <sys/stat.h> 
#include <fcntl.h>
#include <cstdlib>

#include "utils.h"
#include "conversion.h"
#include "mutex_wrapper.h"


//-----------------------------------------------------------------------------------------------//
//-- Globals ------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

CountedSp<MutexWrapper> JPI_OUTPUT_MUTEX(new MutexWrapper());


//-----------------------------------------------------------------------------------------------//
//-- JasperReports ------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperReports::JasperReports(JasperManager& mngr)
: m_mngr(mngr), m_reports()
{
}

//-----------------------------------------------------------------------------------------------//
JasperReports::~JasperReports()
{
}

//-----------------------------------------------------------------------------------------------//
 REP_HANDLE JasperReports::openNew(ConfigGroup* repConfig)
{
  JasperReport* rep = 0;
  try {
    rep = new JasperReport(m_mngr, repConfig);
  } catch ( JaspInitFailedExc ) {
    setLastError_I(JPI_OPREP_INITFAILED_E);
    rep = 0;
  }
  REP_HANDLE h = INVALID_REP_HANDLE;
  if ( rep != 0 ) {
    h = m_reports.getFreeHandle();
    JasperReportItem* item = new JasperReportItem(h, rep);
    m_reports.add(item);
  }
  return h;
}

//-----------------------------------------------------------------------------------------------//
void JasperReports::close(const REP_HANDLE& handle)
{
  m_reports.removeByHandle(handle);
}

//-----------------------------------------------------------------------------------------------//
const JasperReport* JasperReports::getReport(const REP_HANDLE& handle) const
{
  const JasperReportItem_Sp* pRepItem = 0;
  try {
    const JasperReportItem_Sp& repItem = m_reports.getByHandle(handle);
    pRepItem = &repItem;
  } catch ( JaspItemNotFoundExc ) {
    pRepItem = 0;
  }
  if ( pRepItem == 0 ) return 0;
  const JasperReportItem_Sp& rRepItem = *pRepItem;
  const JasperReport_Sp& rRep = rRepItem->getReport();
  return rRep.getPointer();
}

//-----------------------------------------------------------------------------------------------//
JasperReport* JasperReports::getReport(const REP_HANDLE& handle)
{
  JasperReportItem_Sp* pRepItem = 0;
  try {
    JasperReportItem_Sp& repItem = m_reports.getByHandle(handle);
    pRepItem = &repItem;
  } catch ( JaspItemNotFoundExc ) {
    pRepItem = 0;
  }
  if ( pRepItem == 0 ) return 0;
  JasperReportItem_Sp& rRepItem = *pRepItem;
  JasperReport_Sp& rRep = rRepItem->getReport();
  return rRep.getPointer();
}


//-----------------------------------------------------------------------------------------------//
//-- JasperReport -------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperReport::JasperReport(JasperManager& mngr, ConfigGroup* repConfig)
: m_mngr(mngr), m_config(repConfig), m_modifications(), m_pdf(0), m_execLog(0)
{
  check();
}

//-----------------------------------------------------------------------------------------------//
JasperReport::~JasperReport()
{
  if ( m_pdf != 0 ) delete(m_pdf);
  if ( m_execLog != 0 ) delete(m_execLog);
}

//-----------------------------------------------------------------------------------------------//
bool JasperReport::fileExists(const std::string& filename) const
{
  struct stat stFileInfo;
  int i = stat(filename.c_str(), &stFileInfo);
  return (i == 0);
}

//-----------------------------------------------------------------------------------------------//
void JasperReport::check() const
{
  std::string fn = getFilename();
  if ( ! fileExists(fn) ) throw JaspInitFailedExc();
}

//-----------------------------------------------------------------------------------------------//
std::string JasperReport::getFilename() const
{
  std::string res = m_mngr.getConfig().getRoot();
  NormalizePath(res);
  res = res + m_mngr.getConfig().getSecGeneral().getByKey("rep_location")->getValue();
  NormalizePath(res);
  res = res + m_config->getByKey("filename")->getValue();
  return res;
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperReport::execute()
{
  JasperExecuter executer(*this);
  return executer.execute();
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperReport::save(const std::string& file)
{
  JasperSaver saver(*this);
  return saver.save(file);
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperReport::save_random(std::string& file)
{
  std::string p = m_mngr.getConfig().getRoot();
  NormalizePath(p);
  p = p + m_mngr.getConfig().getSecGeneral().getByKey("out_location")->getValue();
  NormalizePath(p);

  int max_st = 0;
  int min_lt = 0;
  try {
    std::string max_st_str = m_mngr.getConfig().getSecGeneral().getByKey("max_pdf_storage")->getValue();
    max_st = StrToInt(max_st_str);
    std::string min_lt_str = m_mngr.getConfig().getSecGeneral().getByKey("min_pdf_lifetime")->getValue();
    min_lt = StrToInt(min_lt_str);
  } catch (JaspConversionExc e) {
    return JPI_SRD_CONFIG_E;
  }

  ScopedMutex sm(*JPI_OUTPUT_MUTEX.getPointer());

  srand(time(NULL));
  int r = 0;
  std::string n = "";
  int i = 0;
  time_t time_now = time(0);
  time_t time_delta = 0;
    while (true) {
    r = rand() % (max_st+1);
    if (r == 0) continue;
    IntToStr(r, n);
    n = p + "rep" + n + ".pdf";

    if ( ! fileExists(n) ) break;

    int fd = open(n.c_str(), O_RDONLY);
    if (fd == -1) {
      n = "";
      break;
    }

    struct stat f_stat;
    if ( fstat(fd, &f_stat) == -1) {
      n = "";
      break;
    }

    time_t mt = f_stat.st_mtime;
    close(fd);

    time_delta = (time_now - mt);
    int min = (int)(time_delta / 60);

    if (min > min_lt) {
      if ( unlink(n.c_str()) == 0) break;
    }

    i = i + 1;
    if (i > 20) {
      n = "";
      break;
    }
  }

  RETURN_CODE ret = JPI_UNDEFINED_ERROR;
  if ( n.compare("") != 0) {
    ret = save(n);
    if (ret == JPI_NO_ERROR) file = n;
    return ret;
  } else {
    return JPI_SRD_NOFILEFOUND_E;
  }
}

//-----------------------------------------------------------------------------------------------//
JasperModifications& JasperReport::getModifications()
{
  return m_modifications;
}

//-----------------------------------------------------------------------------------------------//
const JasperModifications& JasperReport::getModifications() const
{
  return m_modifications;
}

//-----------------------------------------------------------------------------------------------//
bool JasperReport::hasPdfBuffer() const
{
  return (m_pdf != 0);
}

//-----------------------------------------------------------------------------------------------//
void JasperReport::setPdfBuffer(BinaryBuffer* pdfBuf)
{
  if ( m_pdf != 0 ) delete(m_pdf);
  m_pdf = pdfBuf;
}

//-----------------------------------------------------------------------------------------------//
const BinaryBuffer* JasperReport::getPdfBuffer_ptr() const
{
  return m_pdf;
}

//-----------------------------------------------------------------------------------------------//
BinaryBuffer& JasperReport::getPdfBuffer_ref() const
{
  return *m_pdf;
}

//-----------------------------------------------------------------------------------------------//
bool JasperReport::hasExecLog() const
{
  return (m_execLog != 0);
}

//-----------------------------------------------------------------------------------------------//
void JasperReport::setExecLog(JasperExeclogList* execLog)
{
  if ( m_execLog != 0 ) delete(m_execLog);
  m_execLog = execLog;
}

//-----------------------------------------------------------------------------------------------//
JasperExeclogList& JasperReport::getExeclog_ref() const
{
  return *m_execLog;
}

//-----------------------------------------------------------------------------------------------//
const JasperExeclogList* JasperReport::getExeclog_ptr() const
{
  return m_execLog;
}

//-----------------------------------------------------------------------------------------------//
JasperManager& JasperReport::getManager() const
{
  return m_mngr;
}


//-----------------------------------------------------------------------------------------------//
//-- JasperReportItem ---------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//
 
//-----------------------------------------------------------------------------------------------//
JasperReportItem::JasperReportItem(REP_HANDLE& handle, JasperReport* report)
: m_handle(handle), m_report(report)
{
}

//-----------------------------------------------------------------------------------------------//
JasperReportItem::~JasperReportItem()
{
}

//-----------------------------------------------------------------------------------------------//
REP_HANDLE JasperReportItem::getHandle() const
{
  return m_handle;
}

//-----------------------------------------------------------------------------------------------//
JasperReport_Sp& JasperReportItem::getReport()
{
  return m_report;
}

//-----------------------------------------------------------------------------------------------//
const JasperReport_Sp& JasperReportItem::getReport() const
{
  return m_report;
}


//-----------------------------------------------------------------------------------------------//
//-- JasperReportTable --------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperReportTable::JasperReportTable()
: m_items()
{
}

//-----------------------------------------------------------------------------------------------//
JasperReportTable::~JasperReportTable()
{
}

//-----------------------------------------------------------------------------------------------//
void JasperReportTable::add(JasperReportItem* item)
{
  m_items.push_back(JasperReportItem_Sp(item));
}

//-----------------------------------------------------------------------------------------------//
bool JasperReportTable::sameHandle(const REP_HANDLE& handle, const JasperReportItem_Sp& item) const
{
  return ( handle == item->getHandle() );
}

//-----------------------------------------------------------------------------------------------//
const JasperReportItem_Sp& JasperReportTable::getByHandle(const REP_HANDLE& handle) const
{
  JasperReportItems::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const JasperReportItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
JasperReportItem_Sp& JasperReportTable::getByHandle(const REP_HANDLE& handle)
{
  JasperReportItems::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    JasperReportItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
void JasperReportTable::removeByHandle(const REP_HANDLE& handle)
{
  JasperReportItems::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    JasperReportItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      m_items.erase(it);
      return;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
REP_HANDLE JasperReportTable::getFreeHandle() const
{
  REP_HANDLE res = (INVALID_REP_HANDLE + 1);
  REP_HANDLE& curr = res;

  JasperReportItems::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const JasperReportItem_Sp& item = *it;
    curr = item->getHandle();
    if ( curr >= res ) {
      res = (curr + 1);
    }
  }

  return res;
}
