
#include "jasp_executionlog.h"

#include "conversion.h"
#include <stdexcept>


//-----------------------------------------------------------------------------------------------//
//-- JasperExeclogItem --------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperExeclogItem::JasperExeclogItem(const int level, const std::string& msg)
: m_level(level), m_msg(msg)
{
}

//-----------------------------------------------------------------------------------------------//
JasperExeclogItem::~JasperExeclogItem()
{
}

//-----------------------------------------------------------------------------------------------//
const int JasperExeclogItem::getLevel() const
{
  return m_level;
}

//-----------------------------------------------------------------------------------------------//
const std::string& JasperExeclogItem::getMsg() const
{
  return m_msg;
}


//-----------------------------------------------------------------------------------------------//
//-- JasperExeclogList --------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperExeclogList::JasperExeclogList()
: m_items()
{
}

//-----------------------------------------------------------------------------------------------//
JasperExeclogList::~JasperExeclogList()
{
}

//-----------------------------------------------------------------------------------------------//
void JasperExeclogList::add(JasperExeclogItem* item)
{
  m_items.push_back(JasperExeclogItem_Sp(item));
}

//-----------------------------------------------------------------------------------------------//
int JasperExeclogList::getCount() const
{
  return m_items.size();
}

//-----------------------------------------------------------------------------------------------//
const JasperExeclogItem_Sp& JasperExeclogList::getItem_sp(int index) const
{
  return m_items.at(index);
}

//-----------------------------------------------------------------------------------------------//
const JasperExeclogItem* JasperExeclogList::getItem_ptr(int index) const
{
  try {
    const JasperExeclogItem* res = m_items.at(index).getPointer();
    return res;
  } catch (std::out_of_range e) {
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
JasperExeclogItems::const_iterator JasperExeclogList::begin() const
{
  return m_items.begin();
}

//-----------------------------------------------------------------------------------------------//
JasperExeclogItems::const_iterator JasperExeclogList::end() const
{
  return m_items.end();
}
