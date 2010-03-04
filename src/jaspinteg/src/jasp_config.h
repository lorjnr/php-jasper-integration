
#ifndef HG_JASP_CONFIG
#define HG_JASP_CONFIG

#include <string>
#include "inifile.h"
#include "smartpointer.h"

#include <vector>

class ConfigItem {
private:
  const std::string m_name;
  const std::string m_value;
public:
  ConfigItem(const std::string& name, const std::string& value);
  virtual ~ConfigItem();

  const std::string& getName() const;
  const std::string& getValue() const;
};

typedef CountedSp<ConfigItem> ConfigItem_Sp;
typedef std::vector<ConfigItem_Sp> ConfigItems;

class ConfigGroup {
private:
  ConfigItems m_items;

  bool sameKey(const std::string& key, const ConfigItem_Sp& item) const;
public:
  ConfigGroup();
  virtual ~ConfigGroup();

  void add(ConfigItem* item);

  int hasItems() const;

  ConfigItem_Sp& getByKey(const std::string& key);
  const ConfigItem_Sp& getByKey(const std::string& key) const;
};

typedef CountedSp<ConfigGroup> ConfigGroup_Sp;

class JasperConfig {
private:
  std::string m_root;
  Inifile m_file;

  ConfigGroup m_sec_general;
  ConfigGroup m_sec_database;

  void initialize();
  void procIni(const std::string& section, const std::string& key, const std::string& value, bool& cont);

  std::string construct_file_path(const std::string& path, const std::string& name) const;
public:
  JasperConfig(const std::string& root, const std::string& file);
  virtual ~JasperConfig();

  ConfigGroup* getSection(const std::string& section);

  const std::string& getRoot() const;

  const ConfigGroup& getSecGeneral() const;
  const ConfigGroup& getSecDatabase() const;
};

class GetSectionProcessor {
private:
  Inifile& m_file;
  ConfigGroup* m_group;
  const std::string* m_section;
  bool m_b;

  void procIni(const std::string& section, const std::string& key, const std::string& value, bool& cont);
public:
  GetSectionProcessor(Inifile& file);
  virtual ~GetSectionProcessor();

  ConfigGroup* getSection(const std::string& section);
};

#endif
 
