
#ifndef HG_MUTEX_WRAPPER
#define HG_MUTEX_WRAPPER

#include <pthread.h>

class MutexWrapper {
private:
  pthread_mutex_t m_mutex;
public:
  MutexWrapper();
  virtual ~MutexWrapper();

  void lock();
  void unlock();
};

class ScopedMutex {
private:
  MutexWrapper& m_mutex;
public:
  ScopedMutex(MutexWrapper& mutex);
  virtual ~ScopedMutex();
};

#endif
