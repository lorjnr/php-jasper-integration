
#include "binbuffer.h"

#include <cstdlib>
#include <cstring>


//-----------------------------------------------------------------------------------------------//
//-- BinaryBuffer -------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
BinaryBuffer::BinaryBuffer(int size)
: m_buffer(0), m_writeMark(0), m_readMark(0), m_buf_size(size)
{
  m_buffer = (char*)malloc(m_buf_size);
}

//-----------------------------------------------------------------------------------------------//
BinaryBuffer::~BinaryBuffer()
{
  free(m_buffer);
}

//-----------------------------------------------------------------------------------------------//
int BinaryBuffer::write(const void* buffer, const int size)
{
  int eSize;
  if ( (m_writeMark + size ) > m_buf_size ) {
    eSize = (m_buf_size - m_writeMark);
  } else {
    eSize = size;
  }
  memcpy((m_buffer + m_writeMark), buffer, size);
  m_writeMark = m_writeMark + eSize;
  return eSize;
}

//-----------------------------------------------------------------------------------------------//
int BinaryBuffer::getWrittenSize() const
{
  return m_writeMark;
}

//-----------------------------------------------------------------------------------------------//
void BinaryBuffer::resetRead() const
{
  m_readMark = 0;
}

//-----------------------------------------------------------------------------------------------//
int BinaryBuffer::read(void* buffer, const int size) const
{
  int eSize;
  if ( (m_readMark + size ) > m_buf_size ) {
    eSize = (m_buf_size - m_readMark);
  } else {
    eSize = size;
  }
  memcpy(buffer, (m_buffer + m_readMark), eSize);
  m_readMark = m_readMark + eSize;
  return eSize;
}
