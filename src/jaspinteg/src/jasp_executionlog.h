
#ifndef HG_JASP_EXECUTIONLOG
#define HG_JASP_EXECUTIONLOG

#include <vector>
#include <string>

#include "smartpointer.h"

class JasperExeclogItem {
private:
  int m_level;
  std::string m_msg;
public:
  JasperExeclogItem(const int level, const std::string& msg);
  virtual ~JasperExeclogItem();

  const int getLevel() const;
  const std::string& getMsg() const;
};

typedef CountedSp<JasperExeclogItem> JasperExeclogItem_Sp;
typedef std::vector<JasperExeclogItem_Sp> JasperExeclogItems;

class JasperExeclogList {
private:
  JasperExeclogItems m_items;
public:
  JasperExeclogList();
  virtual ~JasperExeclogList();

  void add(JasperExeclogItem* item);

  int getCount() const;
  const JasperExeclogItem_Sp& getItem_sp(int index) const;
  const JasperExeclogItem* getItem_ptr(int index) const;

  JasperExeclogItems::const_iterator begin() const;
  JasperExeclogItems::const_iterator end() const;
};

#endif
