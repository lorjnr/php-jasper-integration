
#include "jaspinteg.h"
#include "jasp_manager.h"
#include "jasp_modifications.h"
#include "last_error.h"

#include "smartpointer.h"
#include "mutex_wrapper.h"

#include "definitions.h"

#include <string.h>


//-----------------------------------------------------------------------------------------------//
//-- Globals ------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

CountedSp<JasperManagerTable> JPI_MNGR_TBL(new JasperManagerTable());

CountedSp<MutexWrapper> JPI_MNGR_MUTEX(new MutexWrapper());


//-----------------------------------------------------------------------------------------------//
//-- Functions ----------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------//
void jpi_init()
{
}

//-----------------------------------------------------------------------------------------------//
void jpi_cleanup()
{
}

//-----------------------------------------------------------------------------------------------//
MNGR_HANDLE jpi_open_mngr(const char* root)
{
  ScopedMutex sm(*JPI_MNGR_MUTEX.getPointer());

  std::string root_str(root);
  JasperManager* mngr = 0;
  MNGR_HANDLE h = INVALID_MNGR_HANDLE;

  try {
    try {
      mngr = new JasperManager(root_str, C_CONF_FILENAME);
    } catch ( JaspOpenFileExc ) {
      setLastError_I(JPI_OMNGR_INVALIDCONFIG_E);
      return INVALID_MNGR_HANDLE;
    }
  } catch ( ... ) {
     setLastError_I(JPI_EXC_ERROR);
     return INVALID_MNGR_HANDLE;
  }

  try {
    h = JPI_MNGR_TBL->getFreeHandle();
  } catch ( ... ) {
    delete(mngr);
    setLastError_I(JPI_OMNGR_GETHANDLE_E);
    return INVALID_MNGR_HANDLE;
  }
  try {
    JasperManagerItem* item = new JasperManagerItem(h, mngr);
    JPI_MNGR_TBL->add(item);
  } catch ( ... ) {
    delete(mngr);
    setLastError_I(JPI_OMNGR_ADDTOTABLE_E);
    return INVALID_MNGR_HANDLE;
  }

  return h;
}

//-----------------------------------------------------------------------------------------------//
void jpi_close_mngr(const MNGR_HANDLE mHandle)
{
  ScopedMutex sm(*JPI_MNGR_MUTEX.getPointer());

  try {
    JPI_MNGR_TBL->removeByHandle(mHandle);
  } catch ( ... ) {
    //Ignore
  }
}

//-----------------------------------------------------------------------------------------------//
REP_HANDLE jpi_open_rep(const MNGR_HANDLE mHandle, const char* id)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    try {

      std::string id_str(id);
      try {
        return mngr->getManager()->openReport(id_str);
      } catch ( JaspGetConfigExc ) {
        setLastError_I(JPI_OPREP_INVALIDID_E);
        return INVALID_REP_HANDLE;
      }

    } catch ( ... ) {
      setLastError_I(JPI_EXC_ERROR);
      return INVALID_REP_HANDLE;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return INVALID_REP_HANDLE;
  }
}

//-----------------------------------------------------------------------------------------------//
void jpi_close_rep(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);

    mngr->getManager()->closeReport(rHandle);

  } catch ( ... ) {
    //Ignore
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE jpi_execute(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        return rep->execute();

      } catch ( ... ) {
        return JPI_EXC_ERROR;
      }
    } else{
      return JPI_NOREP_ERROR;
    }
  } catch ( ... ) {
    return JPI_NOMNGR_ERROR;
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE jpi_save(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const char* file)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        std::string file_name(file);
        return rep->save(file_name);

      } catch ( ... ) {
        return JPI_EXC_ERROR;
      }
    } else{
      return JPI_NOREP_ERROR;
    }
  } catch ( ... ) {
    return JPI_NOMNGR_ERROR;
  }
}

//-----------------------------------------------------------------------------------------------//
int jpi_save_random(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, char* file, const int size)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        std::string file_name = "";
        RETURN_CODE ret = rep->save_random(file_name);
        if (ret == JPI_NO_ERROR) {
          int s = file_name.length();
          if (s < size) {
            memcpy(file, file_name.c_str(), s);
            file[s] = 0;
            return s;
          } else {
            memcpy(file, file_name.c_str(), size);
            file[size-1] = 0;
            setLastError_I(JPI_SRD_TOOSMALLBUFFEE_E);
            return size;
          }
        } else {
          setLastError_I(ret);
          return 0;
        }

        return ret;

      } catch ( ... ) {
        setLastError_I(JPI_EXC_ERROR);
        return 0;
      }
    } else{
      setLastError_I(JPI_NOREP_ERROR);
      return 0;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE jpi_buffer_length(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        const BinaryBuffer* bb = rep->getPdfBuffer_ptr();
        if ( bb != 0 ) {
           return bb->getWrittenSize();
        } else {
          setLastError_I(JPI_BUFF_NOPDF_E);
          return 0;
        }

      } catch ( ... ) {
        setLastError_I(JPI_EXC_ERROR);
        return 0;
      }
    } else{
      setLastError_I(JPI_NOREP_ERROR);
      return 0;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE jpi_buffer_resetread(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        const BinaryBuffer* bb = rep->getPdfBuffer_ptr();
        if ( bb != 0 ) {
          bb->resetRead();
          return JPI_NO_ERROR;
        } else {
          return JPI_BUFF_NOPDF_E;
        }

      } catch ( ... ) {
        return JPI_EXC_ERROR;
      }
    } else{
      return JPI_NOREP_ERROR;
    }
  } catch ( ... ) {
    return JPI_NOMNGR_ERROR;
  }
}

//-----------------------------------------------------------------------------------------------//
int jpi_buffer_read(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, void* buffer, const int size)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        const BinaryBuffer* bb = rep->getPdfBuffer_ptr();
        if ( bb != 0 ) {
          int r_size = 0;
          r_size = bb->read(buffer, size);
          return r_size;
        } else {
          setLastError_I(JPI_BUFF_NOPDF_E);
          return 0;
        }

      } catch ( ... ) {
        setLastError_I(JPI_EXC_ERROR);
        return 0;
      }
    } else{
      setLastError_I(JPI_NOREP_ERROR);
      return 0;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
int jpi_execlog_count(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        const JasperExeclogList* execLog = rep->getExeclog_ptr();
        if ( execLog != 0 ) {
           return execLog->getCount();
        } else {
          setLastError_I(JPI_EXLOG_NOLOG_E);
          return 0;
        }

      } catch ( ... ) {
        setLastError_I(JPI_EXC_ERROR);
        return 0;
      }
    } else{
      setLastError_I(JPI_NOREP_ERROR);
      return 0;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
int jpi_execlog_entry(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const int index, int* level, char* msg, const int size)
{
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        const JasperExeclogList* execLog = rep->getExeclog_ptr();
        if ( execLog != 0 ) {

          const JasperExeclogItem* item;
          item = execLog->getItem_ptr(index);
          if ( item != 0 ) {

            const std::string& msg_str = item->getMsg();

            if (level != 0) *level = item->getLevel();
            int s = msg_str.length();
            if (s < size) {
              memcpy(msg, msg_str.c_str(), s);
              msg[s] = 0;
              return s;
            } else {
              if (size > 0) {
                memcpy(msg, msg_str.c_str(), size);
                msg[size-1] = 0;
              }
              setLastError_I(JPI_EXLOG_TOOSMALLBUFFEE_E);
              return size;
            }

          } else {
            setLastError_I(JPI_EXLOG_WRONGINDEX_E);
            return 0;
          }

        } else {
          setLastError_I(JPI_EXLOG_NOLOG_E);
          return 0;
        }

      } catch ( ... ) {
        setLastError_I(JPI_EXC_ERROR);
        return 0;
      }
    } else{
      setLastError_I(JPI_NOREP_ERROR);
      return 0;
    }
  } catch ( ... ) {
    setLastError_I(JPI_NOMNGR_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE jpi_register_modification(const MNGR_HANDLE mHandle, const REP_HANDLE rHandle, const char* mod){
  try {
    JasperManagerItem_Sp& mngr = JPI_MNGR_TBL->getByHandle(mHandle);
    JasperReport* rep = mngr->getManager()->getReport(rHandle);
    if (rep != 0) {
      try {

        std::string mod_str(mod);
        rep->getModifications().add(new JasperModification(mod_str));
        return JPI_NO_ERROR;

      } catch ( ... ) {
        return JPI_EXC_ERROR;
      }
    } else {
      return JPI_NOREP_ERROR;
    }
  } catch ( ... ) {
    return JPI_NOMNGR_ERROR;
  }
}

//-----------------------------------------------------------------------------------------------//
int jpi_encode_param(const char* param, char* newParam, const int size)
{
  try {

    std::string param_str(param);

    int offset;
    size_t p;

    // :
    offset = 0;
    while (true) {
      p = param_str.find_first_of(':', offset);
      if (p != std::string::npos) {
        param_str.insert(p, ":");
        offset = (p + 2);
      } else {
        break;
      }
    }

  //Return
    int s = param_str.length();
    if (s < size) {
      memcpy(newParam, param_str.c_str(), s);
      newParam[s] = 0;
      return s;
    } else {
      memcpy(newParam, param_str.c_str(), size);
      newParam[size-1] = 0;
      setLastError_I(JPI_ENP_TOOSMALLBUFFEE_E);
      return size;
    }

    return JPI_NO_ERROR;

  } catch ( ... ) {
    setLastError_I(JPI_EXC_ERROR);
    return 0;
  }
}

//-----------------------------------------------------------------------------------------------//
RETURN_CODE getLastError()
{
  return getLastError_I();
}

//-----------------------------------------------------------------------------------------------//
char* jpi_version()
{
  static char v[] = "1.0";
  return v;
}
