
#ifndef HG_JASP_SAVER
#define HG_JASP_SAVER

#include "jasp_report.h"

#include <string>

class JasperSaver {
private:
  JasperReport& m_rep;

public:
  JasperSaver(JasperReport& rep);
  virtual ~JasperSaver();

  RETURN_CODE save(const std::string& file);
};

#endif 
 
