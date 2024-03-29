
#include "conversion.h"

#include "jasp_exception.h"

#include <sstream>
#include <cstdlib>

//-----------------------------------------------------------------------------------------------//
//-- Functions ----------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
void IntToStr(int i, std::string& str)
{
  str = "";
  std::stringstream ss;
  ss << i;
  ss >> str;
} 

//-----------------------------------------------------------------------------------------------//
int StrToInt(const std::string& str)
{
  if (str.compare("0") != 0) {
    int res = atoi(str.c_str());
    if ( res == 0 ) throw JaspConversionExc();
    return res;
  } else {
    return 0;
  }
}
