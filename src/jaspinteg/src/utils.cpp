
#include "utils.h"

void NormalizePath(std::string& path)
{
  if (path[path.length()-1] != '/') {
    path.append("/");
  }
}
