
#ifndef HG_BINBUFFER
#define HG_BINBUFFER

#include "smartpointer.h"

class BinaryBuffer {
private:
  char* m_buffer;
  int m_buf_size;

  int m_writeMark;
  mutable int m_readMark;
public:
  BinaryBuffer(int size);
  virtual ~BinaryBuffer();

  int write(const void* buffer, const int size);
  int getWrittenSize() const;

  void resetRead() const;
  int read(void* buffer, const int size) const;
};

#endif
