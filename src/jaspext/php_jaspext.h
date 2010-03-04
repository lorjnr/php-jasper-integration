#ifndef PHP_JASPEXT_H
#define PHP_JASPEXT_H

#ifdef ZTS
#include "TSRM.h"
#endif

#include "jaspinteg.h"

ZEND_BEGIN_MODULE_GLOBALS(jaspext)
MNGR_HANDLE JSP_MNGR_HANDLE;
ZEND_END_MODULE_GLOBALS(jaspext)

#ifdef ZTS
#define JASPEXT_G(v) TSRMG(jaspext_globals_id, zend_jaspext_globals *, v)
#else
#define JASPEXT_G(v) (jaspext_globals.v)
#endif

#define PHP_JASPEXT_VERSION "1.0"
#define PHP_JASPEXT_EXTNAME "jaspext"

PHP_FUNCTION(jasper_open_mngr);
PHP_FUNCTION(jasper_version);
PHP_FUNCTION(jasper_open_rep);
PHP_FUNCTION(jasper_close_rep);
PHP_FUNCTION(jasper_execute);
PHP_FUNCTION(jasper_save);
PHP_FUNCTION(jasper_save_random);
PHP_FUNCTION(jasper_buffer_size);
PHP_FUNCTION(jasper_buffer_read);
PHP_FUNCTION(jasper_execlog_count);
PHP_FUNCTION(jasper_execlog_level);
PHP_FUNCTION(jasper_execlog_msg);
PHP_FUNCTION(jasper_register_modification);
PHP_FUNCTION(jasper_encode_param);
PHP_FUNCTION(jasper_last_error);


extern zend_module_entry jaspext_module_entry;
#define phpext_jaspext_ptr &jaspext_module_entry

#ifdef PHP_WIN32
#define PHP_JASPEXT_API __declspec(dllexport)
#else
#define PHP_JASPEXT_API
#endif

PHP_MINIT_FUNCTION(jaspext);
PHP_MSHUTDOWN_FUNCTION(jaspext);
PHP_RINIT_FUNCTION(jaspext);
PHP_RSHUTDOWN_FUNCTION(jaspext);
PHP_MINFO_FUNCTION(jaspext);

#endif
