#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "php.h"
#include "php_ini.h"
//#include "jaspinteg.h"
#include "php_jaspext.h"

ZEND_DECLARE_MODULE_GLOBALS(jaspext)

function_entry jaspext_functions[] = {
    PHP_FE(jasper_open_mngr, NULL)
    PHP_FE(jasper_open_rep, NULL)
    PHP_FE(jasper_close_rep, NULL)
    PHP_FE(jasper_execute, NULL)
    PHP_FE(jasper_save, NULL)
    PHP_FE(jasper_save_random, NULL)
    PHP_FE(jasper_buffer_size, NULL)
    PHP_FE(jasper_buffer_read, NULL)
    PHP_FE(jasper_execlog_count, NULL)
    PHP_FE(jasper_execlog_level, NULL)
    PHP_FE(jasper_execlog_msg, NULL)
    PHP_FE(jasper_register_modification, NULL)
    PHP_FE(jasper_encode_param, NULL)
    PHP_FE(jasper_last_error, NULL)
    PHP_FE(jasper_version, NULL)
    {NULL, NULL, NULL}
};


zend_module_entry jaspext_module_entry = {
#if ZEND_MODULE_API_NO >= 20010901
    STANDARD_MODULE_HEADER,
#endif
    PHP_JASPEXT_EXTNAME,
    jaspext_functions,
    PHP_MINIT(jaspext),
    PHP_MSHUTDOWN(jaspext),
    PHP_RINIT(jaspext),
    PHP_RSHUTDOWN(jaspext),
    PHP_MINFO(jaspext),
#if ZEND_MODULE_API_NO >= 20010901
    PHP_JASPEXT_VERSION,
#endif
    STANDARD_MODULE_PROPERTIES
};


#ifdef COMPILE_DL_JASPEXT
ZEND_GET_MODULE(jaspext)
#endif


static void php_jaspext_init_globals(zend_jaspext_globals* jaspext_globals) {
}

PHP_RINIT_FUNCTION(jaspext)
{
  JASPEXT_G(JSP_MNGR_HANDLE) = INVALID_MNGR_HANDLE;
  return SUCCESS;
}

PHP_RSHUTDOWN_FUNCTION(jaspext)
{
  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    jpi_close_mngr(JASPEXT_G(JSP_MNGR_HANDLE));
  }
  return SUCCESS;
}

PHP_MINIT_FUNCTION(jaspext)
{
  ZEND_INIT_MODULE_GLOBALS(jaspext, php_jaspext_init_globals, NULL);
  jpi_init();
  return SUCCESS;
}

PHP_MSHUTDOWN_FUNCTION(jaspext)
{
  jpi_cleanup();
  return SUCCESS;
}


PHP_MINFO_FUNCTION(jaspext)
{
    php_info_print_table_start();
    php_info_print_table_header(2, "jaspext support", "enabled");
    php_info_print_table_end();
}




PHP_FUNCTION(jasper_open_mngr)
{
  char* conf_file = NULL;
  int conf_file_len;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "s", &conf_file, &conf_file_len) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) == INVALID_MNGR_HANDLE) {
    JASPEXT_G(JSP_MNGR_HANDLE) = jpi_open_mngr(conf_file);
    if (JASPEXT_G(JSP_MNGR_HANDLE) == INVALID_MNGR_HANDLE) {
      php_error_docref(NULL TSRMLS_CC, E_WARNING, "Unable to initialize Manager!");
    }
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager available!");
  }
}

PHP_FUNCTION(jasper_open_rep)
{
  char* rep_id = NULL;
  int rep_id_len;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "s", &rep_id, &rep_id_len) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    REP_HANDLE r = jpi_open_rep(JASPEXT_G(JSP_MNGR_HANDLE), rep_id);
    RETURN_LONG((long)r);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)INVALID_REP_HANDLE);
  }
}

PHP_FUNCTION(jasper_close_rep)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    jpi_close_rep(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
  }
}

PHP_FUNCTION(jasper_execute)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    RETURN_CODE r = jpi_execute(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle);
    RETURN_LONG((long)r);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  }
}

PHP_FUNCTION(jasper_save)
{
  char* file = NULL;
  int file_len;
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l|s", &handle, &file, &file_len) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    RETURN_CODE r = jpi_save(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, file);
    RETURN_LONG((long)r);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}

PHP_FUNCTION(jasper_save_random)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  char pdf_out[500];

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    int s = jpi_save_random(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, pdf_out, 500);
    if ( (s <= 0) || (s >= 500) ) {
      pdf_out[0] = 0;
    }
    RETURN_STRING(pdf_out, 1);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}

PHP_FUNCTION(jasper_register_modification)
{
  long handle = 0;
  char* mod_str = NULL;
  int mod_str_len;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l|s", &handle, &mod_str, &mod_str_len) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    RETURN_CODE r = jpi_register_modification(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, mod_str);
    RETURN_LONG((long)r);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}

PHP_FUNCTION(jasper_encode_param)
{
  char* param_str = NULL;
  int param_str_len;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "s", &param_str, &param_str_len) == FAILURE) { 
    return;
  }

  int param_n_size = (param_str_len * 2);
  char param_n[param_n_size];

  int s = jpi_encode_param(param_str, param_n, param_n_size);
  if ( (s <= 0) || (s >= param_n_size) ) {
    param_n[0] = 0;
  }
  RETURN_STRING(param_n, 1);
}

PHP_FUNCTION(jasper_last_error)
{
  RETURN_CODE r = getLastError();
  RETURN_LONG((long)r);
}

PHP_FUNCTION(jasper_version)
{
  char* v = jpi_version();
  RETURN_STRING(v, 1);
}

PHP_FUNCTION(jasper_buffer_size)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    int s = jpi_buffer_length(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle);
    if (s > 0) {
      RETURN_LONG((long)s);
    } else {
      RETURN_LONG((long)0);
    }
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)0);
  } 
}

PHP_FUNCTION(jasper_buffer_read)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    RETURN_CODE r = jpi_buffer_resetread(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle);
    if (r == JPI_NO_ERROR) {
      void* buf;
      int read_size = 0;
      php_stream* stream = 0;

      buf = emalloc(512);
      stream = php_stream_memory_create(0);

      do {
        read_size = jpi_buffer_read(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, buf, 512);
        php_stream_write(stream, buf, read_size);
      }
      while (read_size > 0);

      php_stream_seek(stream, 0, SEEK_SET);
      php_stream_passthru(stream);

      php_stream_close(stream);
      efree(buf);
    }
    RETURN_LONG((long)r);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}

PHP_FUNCTION(jasper_execlog_count)
{
  long handle = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l", &handle) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    int c = jpi_execlog_count(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle);
    RETURN_LONG((long)c);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)0);
  } 
}

PHP_FUNCTION(jasper_execlog_level)
{
  long handle = 0;
  long idx = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l|l", &handle, &idx) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    int level;
    jpi_execlog_entry(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, (int)idx, &level, 0, 0);
    RETURN_LONG((long)level);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}

PHP_FUNCTION(jasper_execlog_msg)
{
  long handle = 0;
  long idx = 0;

  if (zend_parse_parameters(ZEND_NUM_ARGS() TSRMLS_CC, "l|l", &handle, &idx) == FAILURE) { 
    return;
  }

  if (JASPEXT_G(JSP_MNGR_HANDLE) != INVALID_MNGR_HANDLE) {
    char msg[500];
    int s = jpi_execlog_entry(JASPEXT_G(JSP_MNGR_HANDLE), (int)handle, (int)idx, 0, msg, 500);
    if ( (s <= 0) || (s >= 500) ) {
      msg[0] = 0;
    }
    RETURN_STRING(msg, 1);
  } else {
    php_error_docref(NULL TSRMLS_CC, E_WARNING, "Manager not available!");
    RETURN_LONG((long)JPI_UNDEFINED_ERROR);
  } 
}
