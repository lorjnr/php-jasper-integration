
#ifndef HG_LAST_ERROR
#define HG_LAST_ERROR

#include "jaspinteg.h"
#include "smartpointer.h"

#include <pthread.h>
#include <vector>

void setLastError_I(const RETURN_CODE& error);
RETURN_CODE getLastError_I();

class LastErrorItem {
private:
  RETURN_CODE m_code;
  const pthread_t m_thread_id;
public:
  LastErrorItem(const RETURN_CODE& code, const pthread_t& thread_id);
  virtual ~LastErrorItem();

  RETURN_CODE getCode() const;
  pthread_t getThreadId() const;

  void setCode(const RETURN_CODE& code);
};

typedef CountedSp<LastErrorItem> LastErrorItem_Sp;
typedef std::vector<LastErrorItem_Sp> LastErrorItemList;

class LastErrorItems {
private:
  LastErrorItemList m_items;

  bool sameThreadId(const int& threadId, const LastErrorItem_Sp& item) const;
public:
  LastErrorItems();
  virtual ~LastErrorItems();

  void add(LastErrorItem* item);

  const LastErrorItem_Sp& getByThreadId(const pthread_t& thread_id) const;
  LastErrorItem_Sp& getByThreadId(const pthread_t& thread_id);
};

class LastErrorManager {
private:
  LastErrorItems m_errors;
public:
  LastErrorManager();
  virtual ~LastErrorManager();

  void setLastError(const RETURN_CODE& error, const pthread_t& thread_id);
  RETURN_CODE getLastError(const pthread_t& thread_id) const;
}; 

#endif
