
#include "jasp_config.h"
#include "utils.h"


//-----------------------------------------------------------------------------------------------//
//-- JasperConfig -------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
JasperConfig::JasperConfig(const std::string& root, const std::string& file)
: m_root(root), m_file(construct_file_path(root, file)), m_sec_general(), m_sec_database()
{
  initialize();
}

//-----------------------------------------------------------------------------------------------//
JasperConfig::~JasperConfig()
{
}

//-----------------------------------------------------------------------------------------------//
std::string JasperConfig::construct_file_path(const std::string& path, const std::string& name) const
{
  std::string res = path;
  NormalizePath(res);
  res = res + name;
  return res;
}

//-----------------------------------------------------------------------------------------------//
void JasperConfig::initialize()
{
  ProcIniFunctor<JasperConfig> fct(this, &JasperConfig::procIni);
  m_file.processFile(fct);
}

//-----------------------------------------------------------------------------------------------//
void JasperConfig::procIni(const std::string& section, const std::string& key, const std::string& value, bool& cont)
{
  if ( section.compare("database") == 0 ) {
    m_sec_database.add(new ConfigItem(key, value));
    return;
  }
  if ( section.compare("general") == 0 ) {
    m_sec_general.add(new ConfigItem(key, value));
    return;
  }
  if ( m_sec_database.hasItems() && m_sec_general.hasItems() ) cont = false;
}

//-----------------------------------------------------------------------------------------------//
ConfigGroup* JasperConfig::getSection(const std::string& section)
{
  GetSectionProcessor p(m_file);
  return p.getSection(section);
}

//-----------------------------------------------------------------------------------------------//
const std::string& JasperConfig::getRoot() const
{
  return m_root;
}

//-----------------------------------------------------------------------------------------------//
const ConfigGroup& JasperConfig::getSecGeneral() const
{
  return m_sec_general;
}

//-----------------------------------------------------------------------------------------------//
const ConfigGroup& JasperConfig::getSecDatabase() const
{
  return m_sec_database;
}


//-----------------------------------------------------------------------------------------------//
//-- ConfigGroup --------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
ConfigGroup::ConfigGroup()
: m_items()
{
}

//-----------------------------------------------------------------------------------------------//
ConfigGroup::~ConfigGroup()
{
}

//-----------------------------------------------------------------------------------------------//
void ConfigGroup::add(ConfigItem* item)
{
  m_items.push_back(ConfigItem_Sp(item));
}

//-----------------------------------------------------------------------------------------------//
int ConfigGroup::hasItems() const
{
  return ( m_items.size() > 0 );
}

//-----------------------------------------------------------------------------------------------//
bool ConfigGroup::sameKey(const std::string& key, const ConfigItem_Sp& item) const
{
  return (key.compare(item->getName()) == 0);
}

//-----------------------------------------------------------------------------------------------//
ConfigItem_Sp& ConfigGroup::getByKey(const std::string& key)
{
  ConfigItems::iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    ConfigItem_Sp& item = *it;
    if ( sameKey(key, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}

//-----------------------------------------------------------------------------------------------//
const ConfigItem_Sp& ConfigGroup::getByKey(const std::string& key) const
{
  ConfigItems::const_iterator it;
  for (it=m_items.begin(); it<m_items.end(); it++) {
    const ConfigItem_Sp& item = *it;
    if ( sameKey(key, item) ) {
      return item;
    }
  }
  throw JaspItemNotFoundExc();
}


//-----------------------------------------------------------------------------------------------//
//-- ConfigItem ---------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
ConfigItem::ConfigItem(const std::string& name, const std::string& value)
: m_name(name), m_value(value)
{
}

//-----------------------------------------------------------------------------------------------//
ConfigItem::~ConfigItem()
{
}

//-----------------------------------------------------------------------------------------------//
const std::string& ConfigItem::getName() const
{
  return m_name;
}

//-----------------------------------------------------------------------------------------------//
const std::string& ConfigItem::getValue() const
{
  return m_value;
}


//-----------------------------------------------------------------------------------------------//
//-- GetSectionProcessor ------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
GetSectionProcessor::GetSectionProcessor(Inifile& file)
: m_file(file), m_group(0), m_section(0), m_b(false)
{
}

//-----------------------------------------------------------------------------------------------//
GetSectionProcessor::~GetSectionProcessor()
{
}

//-----------------------------------------------------------------------------------------------//
void GetSectionProcessor::procIni(const std::string& section, const std::string& key, const std::string& value, bool& cont)
{
  if ( section.compare(*m_section) == 0 ) {
    m_b = true;
    m_group->add(new ConfigItem(key, value));
  } else {
    if ( m_b ) {
      cont = false;
    }
  }
}

//-----------------------------------------------------------------------------------------------//
ConfigGroup* GetSectionProcessor::getSection(const std::string& section)
{
  ProcIniFunctor<GetSectionProcessor> fct(this, &GetSectionProcessor::procIni);

  m_section = &section;
  m_group = new ConfigGroup();
  m_b = false;

  m_file.processFile(fct);
  ConfigGroup* res = 0;
  if ( m_group->hasItems() ) {
    res = m_group;
  } else {
    delete(m_group);
  }

  m_group = 0;
  m_section = 0;
  m_b = false;

  return res;
}
