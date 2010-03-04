PHP_ARG_WITH(jaspext, whether to enable jaspext support,
[  --with-jaspext          Include jaspext support])

if test "$PHP_JASPEXT" != "no"; then

  SEARCH_PATH="/usr/local /usr"
  SEARCH_FOR="include/jaspinteg.h"
  LIBNAME=jaspinteg
  LIBSYMBOL=jpi_version

  if test -r $PHP_JASPEXT/; then
    JASPINTEG_DIR=$PHP_JASPEXT
  else
    AC_MSG_CHECKING([for jaspinteg in default path])
    for i in $SEARCH_PATH ; do
      if test -r $i/$SEARCH_FOR; then
        JASPINTEG_DIR=$i
        AC_MSG_RESULT([found in $i])
      fi
    done
  fi

  if test -z "$JASPINTEG_DIR"; then
    AC_MSG_RESULT([not found])
    AC_MSG_ERROR([Please reinstall the jaspinteg distribution])
  fi

  PHP_ADD_INCLUDE($JASPINTEG_DIR/include)

  PHP_CHECK_LIBRARY($LIBNAME, $LIBSYMBOL,
  [
    PHP_ADD_LIBRARY_WITH_PATH($LIBNAME, $JASPINTEG_DIR/lib, JASPEXT_SHARED_LIBADD)
    dnl AC_DEFINE(HAVE_JASPINTEGLIB, 1, [])
  ],[
    AC_MSG_ERROR([Problem with libjaspinteg.(a|so). Please check config.log for more information.])
  ],[
    -L$JASPINTEG_DIR/lib -lstdc++
  ])

  PHP_SUBST(JASPEXT_SHARED_LIBADD)


  dnl AC_DEFINE(HAVE_HELLO, 1, [Whether you have Hello World])
  PHP_NEW_EXTENSION(jaspext, jaspext.c, $ext_shared)

fi



	