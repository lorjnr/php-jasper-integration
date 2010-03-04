
#ifndef HG_JASP_EXCEPTION
#define HG_JASP_EXCEPTION

class JaspBaseException { };

class JaspWrongStateExc : public JaspBaseException { };

class JaspNullRefExc : public JaspBaseException { };

class JaspOpenFileExc : public JaspBaseException { };

class JaspGetConfigExc : public JaspBaseException { };

class JaspItemNotFoundExc : public JaspBaseException { };

class JaspInitFailedExc : public JaspBaseException { };

class JaspConversionExc : public JaspBaseException { };

#endif
