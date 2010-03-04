
#ifndef HG_INIFILE
#define HG_INIFILE

#include <string>

class ProcIniFunctorBase {
public:
  virtual void operator()(const std::string& section, const std::string& key, const std::string& value, bool& cont) = 0;
};

template <class T> class ProcIniFunctor : public ProcIniFunctorBase {
public:
  T* m_obj;
  void (T::*m_fct)(const std::string& section, const std::string& key, const std::string& value, bool& cont);
public:
  ProcIniFunctor( T* obj, void(T::*fct)(const std::string& section, const std::string& key, const std::string& value, bool& cont) ) {
    m_obj = obj;
    m_fct = fct;
  }
  virtual void operator()(const std::string& section, const std::string& key, const std::string& value, bool& cont) {
    (*m_obj.*m_fct)(section, key, value, cont);
  };
};


class Inifile {
private:
  const std::string m_file;

  bool extractSection(const std::string& line, std::string& section) const;
  bool compareSection(const std::string& searched, const std::string& candidate) const;

  bool splitNameValue(const std::string& line, std::string& name, std::string& value) const;
  bool compareKey(const std::string& searched, const std::string& candidate) const;

  bool internalReadString(const std::string& section, const std::string& key, std::string& value) const;
public:
  Inifile(const std::string& file);
  virtual ~Inifile();

  bool readString(const std::string& section, const std::string& key, std::string& value) const;

  void processFile(ProcIniFunctorBase& fct) const;
};

#endif
 
