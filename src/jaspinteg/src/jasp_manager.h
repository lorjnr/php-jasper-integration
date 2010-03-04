
#ifndef HG_JASP_MANAGER
#define HG_JASP_MANAGER

#include <string>

#include "jasp_config.h"
#include "jasp_report.h"


class JasperManager {
private:
  JasperConfig m_config;
  JasperReports m_reports;
public:
  JasperManager(const std::string root, const std::string config);
  virtual ~JasperManager();

  REP_HANDLE openReport(const std::string id);
  void closeReport(const REP_HANDLE& handle);

  const JasperReport* getReport(const REP_HANDLE& handle) const;
  JasperReport* getReport(const REP_HANDLE& handle);

  const JasperConfig& getConfig() const;
};

typedef CountedSp<JasperManager> JasperManager_Sp;

class JasperManagerItem {
private:
  MNGR_HANDLE m_handle;
  JasperManager_Sp m_manager;
public:
  JasperManagerItem(MNGR_HANDLE& handle, JasperManager* manager);
  virtual ~JasperManagerItem();

  MNGR_HANDLE getHandle() const;

  JasperManager_Sp& getManager();
  const JasperManager_Sp& getManager() const;
};

typedef CountedSp<JasperManagerItem> JasperManagerItem_Sp;
typedef std::vector<JasperManagerItem_Sp> JasperManagerItems;

class JasperManagerTable {
private:
  JasperManagerItems m_items;

  bool sameHandle(const MNGR_HANDLE& handle, const JasperManagerItem_Sp& item) const;
public:
  JasperManagerTable();
  virtual ~JasperManagerTable();

  void add(JasperManagerItem* item);

  const JasperManagerItem_Sp& getByHandle(const MNGR_HANDLE& handle) const;
  JasperManagerItem_Sp& getByHandle(const MNGR_HANDLE& handle);

  void removeByHandle(const MNGR_HANDLE& handle);

  MNGR_HANDLE getFreeHandle() const;
};

#endif
