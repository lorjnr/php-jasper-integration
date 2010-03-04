
#include "jasp_saver.h"

#include <iostream>
#include <fstream>
#include <malloc.h>

//-----------------------------------------------------------------------------------------------//
//-- JasperSaver --------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperSaver::JasperSaver(JasperReport& rep)
: m_rep(rep)
{
}

//-----------------------------------------------------------------------------------------------//
JasperSaver::~JasperSaver()
{
}
 
//-----------------------------------------------------------------------------------------------//
RETURN_CODE JasperSaver::save(const std::string& file)
{
  if ( ! m_rep.hasPdfBuffer() ) return JPI_SAVE_NOPDF_E;
  BinaryBuffer& bb = m_rep.getPdfBuffer_ref();
  bb.resetRead();

  int bb_size = bb.getWrittenSize();
  void* r_buf = malloc(512);
  int r_size = 0;

  std::fstream pdf_stream(file.c_str(), std::fstream::out);
  if (pdf_stream.fail()) return JPI_SAVE_OPENFILE_E;
  while (bb_size > 0) {
    r_size = bb.read(r_buf, 512);
    pdf_stream.write((char*)r_buf, r_size);
    bb_size = bb_size - r_size;
  }
  free(r_buf);
  pdf_stream.close();

  return 0;
}
