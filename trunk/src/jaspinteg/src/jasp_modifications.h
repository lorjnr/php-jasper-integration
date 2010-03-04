
#ifndef HG_JASP_MODIFICATIONS
#define HG_JASP_MODIFICATIONS

#include <vector>
#include <string>

#include "smartpointer.h"

class JasperModification {
private:
  std::string m_mod_str;
public:
  JasperModification(const std::string& mod_str);
  virtual ~JasperModification();

  const std::string& getModStr() const;
};

typedef CountedSp<JasperModification> JasperModification_Sp;
typedef std::vector<JasperModification_Sp> JasperModificationList;

class JasperModifications {
private:
  JasperModificationList m_items;
public:
  JasperModifications();
  virtual ~JasperModifications();

  void add(JasperModification* item);

  int count() const;

  JasperModificationList::const_iterator begin() const;
  JasperModificationList::const_iterator end() const;
};

#endif
