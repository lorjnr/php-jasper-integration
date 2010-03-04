
#include "inifile.h"

#include "jasp_exception.h"

#include <iostream>
#include <fstream>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


//-----------------------------------------------------------------------------------------------//
//-- Inifile ------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
Inifile::Inifile(const std::string& file)
: m_file(file)
{
}

//-----------------------------------------------------------------------------------------------//
Inifile::~Inifile()
{
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::extractSection(const std::string& line, std::string& section) const
{
  size_t p1 = line.find_first_of('[');
  if ( p1 != std::string::npos ) {
    size_t p2 = line.find_first_of(']');
    if ( p2 != std::string::npos ) {
      section = line.substr((p1+1), (p2-(p1+1)));
      return true;
    }
  }
  return false;
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::compareSection(const std::string& searched, const std::string& candidate) const
{
  return (searched.compare(candidate) == 0);
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::splitNameValue(const std::string& line, std::string& name, std::string& value) const
{
  size_t p = line.find_first_of('=');
  if ( p != std::string::npos ) {
    name = line.substr(0, p);
    value = line.substr((p+1), (line.length()-p));
    return true;
  }
  return false;
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::compareKey(const std::string& searched, const std::string& candidate) const
{
  return (searched.compare(candidate) == 0);
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::internalReadString(const std::string& section, const std::string& key, std::string& value) const
{
  std::fstream fs(m_file.c_str(), std::fstream::in);
  if (fs.fail()) throw JaspOpenFileExc();

  bool res = false;
  bool in_section = false;

  while ( ! fs.eof() ) {
    std::string line;
    std::getline(fs, line);
    if ( in_section ) {
      std::string n;
      std::string v;
      if ( splitNameValue(line, n, v) ) {
        if ( compareKey(key, n) ) {
          value = v;
          res = true;
          break;
        }
      }
    } else {
      std::string curr_section;
      if ( extractSection(line, curr_section) ) {
        if ( compareSection(section, curr_section) ) {
          in_section = true;
        }
      }
    }

  }

  fs.close();

  return res;
}

//-----------------------------------------------------------------------------------------------//
bool Inifile::readString(const std::string& section, const std::string& key, std::string& value) const
{
  return internalReadString(section, key, value);
}

//-----------------------------------------------------------------------------------------------//
void Inifile::processFile(ProcIniFunctorBase& fct) const
{
  std::fstream fs(m_file.c_str(), std::fstream::in);
  if (fs.fail()) throw JaspOpenFileExc();

  std::string s;
  std::string n;
  std::string v;
  bool b = true;
  while ( ! fs.eof() ) {
    std::string line;
    std::getline(fs, line);
    if ( ! extractSection(line, s) ) {
      if ( splitNameValue(line, n, v) ) {
        fct(s, n, v, b);
        if ( ! b ) break;
      }
    }
  }

  fs.close();
}
