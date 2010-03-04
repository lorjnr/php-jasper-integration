
#include "last_error.h"



//-----------------------------------------------------------------------------------------------//
//-- Globals ------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

CountedSp<LastErrorManager> ERROR_MNGR(new LastErrorManager());


//-----------------------------------------------------------------------------------------------//
//-- Functions ----------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
void setLastError_I(const RETURN_CODE& error)
{
  ERROR_MNGR->setLastError(error, pthread_self());
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE getLastError_I()
{
  return ERROR_MNGR->getLastError(pthread_self());
}


//-----------------------------------------------------------------------------------------------//
//-- LastErrorItem ------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
LastErrorItem::LastErrorItem(const RETURN_CODE& code, const pthread_t& thread_id)
: m_code(code), m_thread_id(thread_id)
{
}

//-----------------------------------------------------------------------------------------------//
LastErrorItem::~LastErrorItem()
{
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE LastErrorItem::getCode() const
{
  return m_code;
}

//-----------------------------------------------------------------------------------------------//
pthread_t LastErrorItem::getThreadId() const
{
  return m_thread_id;
}

//-----------------------------------------------------------------------------------------------//
void LastErrorItem::setCode(const RETURN_CODE& code)
{
  m_code = code;
}


//-----------------------------------------------------------------------------------------------//
//-- LastErrorItems -----------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
LastErrorItems::LastErrorItems()
: m_items()
{
}

//-----------------------------------------------------------------------------------------------//
LastErrorItems::~LastErrorItems()
{
}

//-----------------------------------------------------------------------------------------------//
void LastErrorItems::add(LastErrorItem* item)
{
  m_items.push_back(LastErrorItem_Sp(item));
}

//-----------------------------------------------------------------------------------------------//
bool LastErrorItems::sameThreadId(const int& threadId, const LastErrorItem_Sp& item) const
{
  return ( threadId == item->getThreadId() );
}

//-----------------------------------------------------------------------------------------------//
const LastErrorItem_Sp& LastErrorItems::getByThreadId(const pthread_t& thread_id) const
{
  LastErrorItemList::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const LastErrorItem_Sp& item = *it;
    if ( sameThreadId(thread_id, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
LastErrorItem_Sp& LastErrorItems::getByThreadId(const pthread_t& thread_id)
{
  LastErrorItemList::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    LastErrorItem_Sp& item = *it;
    if ( sameThreadId(thread_id, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}


//-----------------------------------------------------------------------------------------------//
//-- LastErrorManager ---------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
LastErrorManager::LastErrorManager()
: m_errors()
{
}

//-----------------------------------------------------------------------------------------------//
LastErrorManager::~LastErrorManager()
{
}

//-----------------------------------------------------------------------------------------------//
void LastErrorManager::setLastError(const RETURN_CODE& error, const pthread_t& thread_id)
{
  LastErrorItem_Sp* pEi = 0;
  try {
    LastErrorItem_Sp& rEi = m_errors.getByThreadId(thread_id);
    pEi = &rEi;
  } catch ( JaspItemNotFoundExc ) {
    pEi = 0;
  }
  if (pEi == 0) {
    m_errors.add(new LastErrorItem(error, thread_id));
  } else {
    LastErrorItem_Sp& rEi = *pEi;
    rEi->setCode(error);
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE LastErrorManager::getLastError(const pthread_t& thread_id) const
{
  const LastErrorItem_Sp* pEi = 0;
  try {
    const LastErrorItem_Sp& rEi = m_errors.getByThreadId(thread_id);
    pEi = &m_errors.getByThreadId(thread_id);
  } catch ( JaspItemNotFoundExc ) {
    pEi = 0;
  }
  if (pEi == 0) {
    return JPI_NO_ERROR;
  } else {
    const LastErrorItem_Sp& rEi = *pEi;
    return rEi->getCode();
  }
}
