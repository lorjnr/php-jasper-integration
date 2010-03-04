
#include "jasp_manager.h"
#include "jasp_exception.h"

#include <assert.h>


//-----------------------------------------------------------------------------------------------//
//-- JasperManager ------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperManager::JasperManager(const std::string root, const std::string config)
: m_config(root, config), m_reports(*this)
{
}

//-----------------------------------------------------------------------------------------------//
JasperManager::~JasperManager()
{
}

//-----------------------------------------------------------------------------------------------//
REP_HANDLE JasperManager::openReport(const std::string id)
{
  ConfigGroup* c = m_config.getSection(id);
  if ( c == 0 ) throw JaspGetConfigExc();
  return m_reports.openNew(c);
}

//-----------------------------------------------------------------------------------------------//
void JasperManager::closeReport(const REP_HANDLE& handle)
{
  m_reports.close(handle);
}

//-----------------------------------------------------------------------------------------------//
const JasperReport* JasperManager::getReport(const REP_HANDLE& handle) const
{
  return m_reports.getReport(handle);
}

//-----------------------------------------------------------------------------------------------//
JasperReport* JasperManager::getReport(const REP_HANDLE& handle)
{
  return m_reports.getReport(handle);
} 

//-----------------------------------------------------------------------------------------------//
const JasperConfig& JasperManager::getConfig() const
{
  return m_config;
}


//-----------------------------------------------------------------------------------------------//
//-- JasperManagerItem --------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//
 
//-----------------------------------------------------------------------------------------------//
JasperManagerItem::JasperManagerItem(MNGR_HANDLE& handle, JasperManager* manager)
: m_handle(handle), m_manager(manager)
{
}

//-----------------------------------------------------------------------------------------------//
JasperManagerItem::~JasperManagerItem()
{
}

//-----------------------------------------------------------------------------------------------//
MNGR_HANDLE JasperManagerItem::getHandle() const
{
  return m_handle;
}

//-----------------------------------------------------------------------------------------------//
JasperManager_Sp& JasperManagerItem::getManager()
{
  return m_manager;
}

//-----------------------------------------------------------------------------------------------//
const JasperManager_Sp& JasperManagerItem::getManager() const
{
  return m_manager;
}


//-----------------------------------------------------------------------------------------------//
//-- JasperManagerTable -------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperManagerTable::JasperManagerTable()
: m_items()
{
}

//-----------------------------------------------------------------------------------------------//
JasperManagerTable::~JasperManagerTable()
{
}

//-----------------------------------------------------------------------------------------------//
void JasperManagerTable::add(JasperManagerItem* item)
{
  m_items.push_back(JasperManagerItem_Sp(item));
}

//-----------------------------------------------------------------------------------------------//
bool JasperManagerTable::sameHandle(const MNGR_HANDLE& handle, const JasperManagerItem_Sp& item) const
{
  return ( handle == item->getHandle() );
}

//-----------------------------------------------------------------------------------------------//
const JasperManagerItem_Sp& JasperManagerTable::getByHandle(const MNGR_HANDLE& handle) const
{
  JasperManagerItems::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const JasperManagerItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
JasperManagerItem_Sp& JasperManagerTable::getByHandle(const MNGR_HANDLE& handle)
{
  JasperManagerItems::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    JasperManagerItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
void JasperManagerTable::removeByHandle(const MNGR_HANDLE& handle)
{
  JasperManagerItems::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    JasperManagerItem_Sp& item = *it;
    if ( sameHandle(handle, item) ) {
      m_items.erase(it);
      return;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
MNGR_HANDLE JasperManagerTable::getFreeHandle() const
{
  MNGR_HANDLE res = (INVALID_MNGR_HANDLE + 1);
  MNGR_HANDLE& curr = res;

  JasperManagerItems::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const JasperManagerItem_Sp& item = *it;
    curr = item->getHandle();
    if ( curr >= res ) {
      res = (curr + 1);
    }
  }

  return res;
}

