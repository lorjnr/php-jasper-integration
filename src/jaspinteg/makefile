
ifndef "command"
command = debug
endif

LIB_NAME = jaspinteg

VERSION_MAJOR = 1
VERSION_MINOR = 0

LIB = lib$(LIB_NAME).so
LIB_MAJ = $(LIB).$(VERSION_MAJOR)
LIB_MAJ_MIN = $(LIB_MAJ).$(VERSION_MINOR)

A = lib$(LIB_NAME).a


# --- Configuration Defaults

LINKER = g++
LINKER_FLAGS = -shared -Wl,-soname,$(LIB_MAJ)
LINKER_DIRS =
LINKER_LIBS =

COMPILER = g++
COMPILER_FLAGS = -fPIC
COMPILER_DIRS =

AR = ar
AR_FLAGS = -cqv

DIRECTORY_INTERMEDIATE = output
DIRECTORY_OUTPUT = bin
DIRECTORY_SOURCE = src
DIRECTORY_SYMBOLS = symbols

DIRECTORY_OUTPUT_RELEASE = release
DIRECTORY_OUTPUT_DEBUG = debug
DIRECTORY_OUTPUT_LIB = lib
DIRECTORY_OUTPUT_INCLUDE = include

DIRECTORY_INSTALL_INCLUDE = /usr/include
DIRECTORY_INSTALL_LIB = /usr/lib


# --- Configuration

#COMPILER_DIRS += -I/home/bayo/da/c_libs/minizip

#LINKER_DIRS += -L/home/bayo/da/c_libs/minizip -L/home/bayo/da/c_libs/zlib-1.2.3
#LINKER_LIBS += -lminizip -lz

# --- Command Configuration

ifeq "$(command)" "debug"
  DIRECTORY_OUTPUT = $(DIRECTORY_OUTPUT_DEBUG)
  COMPILER_FLAGS += -g
else
  DIRECTORY_OUTPUT = $(DIRECTORY_OUTPUT_RELEASE)
endif

# --- Objects

OBJECTS = $(DIRECTORY_INTERMEDIATE)/jaspinteg.o \
          $(DIRECTORY_INTERMEDIATE)/inifile.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_config.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_report.o \
          $(DIRECTORY_INTERMEDIATE)/last_error.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_executer.o \
          $(DIRECTORY_INTERMEDIATE)/conversion.o \
          $(DIRECTORY_INTERMEDIATE)/binbuffer.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_saver.o \
          $(DIRECTORY_INTERMEDIATE)/mutex_wrapper.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_modifications.o \
          $(DIRECTORY_INTERMEDIATE)/utils.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_manager.o \
          $(DIRECTORY_INTERMEDIATE)/jasp_executionlog.o


# --- Build-Targets

build: $(DIRECTORY_OUTPUT)/$(LIB_MAJ_MIN) $(DIRECTORY_OUTPUT)/$(A)


$(DIRECTORY_INTERMEDIATE)/%.o: $(DIRECTORY_SOURCE)/%.cpp
	$(COMPILER) $(COMPILER_FLAGS) $(COMPILER_DIRS) -c -o "$@" "$<"


$(DIRECTORY_OUTPUT)/$(LIB_MAJ_MIN): $(OBJECTS)
	$(LINKER) $(LINKER_DIRS) $(LINKER_FLAGS) -o "$@" $^ $(LINKER_LIBS) 


$(DIRECTORY_OUTPUT)/$(A): $(OBJECTS)
	$(AR) $(AR_FLAGS) $(DIRECTORY_OUTPUT)/$(A) $^


# --- Install-Targets

install:
	install -m 644 -o root -g root $(DIRECTORY_SOURCE)/jaspinteg.h $(DIRECTORY_INSTALL_INCLUDE)
	install -m 755 -o root -g root $(DIRECTORY_OUTPUT)/$(LIB_MAJ_MIN) $(DIRECTORY_INSTALL_LIB)
	(cd $(DIRECTORY_INSTALL_LIB); ln -sf $(LIB_MAJ_MIN) $(LIB))
	(cd $(DIRECTORY_INSTALL_LIB); ln -sf $(LIB_MAJ_MIN) $(LIB_MAJ))
	ldconfig

install_local:
	cp $(DIRECTORY_OUTPUT)/$(LIB_MAJ_MIN) $(DIRECTORY_OUTPUT_LIB)
	(cd $(DIRECTORY_OUTPUT_LIB); ln -sf $(LIB_MAJ_MIN) $(LIB_MAJ))
	(cd $(DIRECTORY_OUTPUT_LIB); ln -sf $(LIB_MAJ_MIN) $(LIB))
	cp $(DIRECTORY_OUTPUT)/$(A) $(DIRECTORY_OUTPUT_LIB)
	cp $(DIRECTORY_SOURCE)/jaspinteg.h $(DIRECTORY_OUTPUT_INCLUDE)

# --- Setup-/Cleanup-Targets

setup:
	mkdir -p $(DIRECTORY_INTERMEDIATE)
	mkdir -p $(DIRECTORY_OUTPUT_DEBUG)
	mkdir -p $(DIRECTORY_OUTPUT_RELEASE)
	mkdir -p $(DIRECTORY_SYMBOLS)
	mkdir -p $(DIRECTORY_OUTPUT_LIB)
	mkdir -p $(DIRECTORY_OUTPUT_INCLUDE)

clean:
	rm -rf $(DIRECTORY_INTERMEDIATE)
	rm -rf $(DIRECTORY_OUTPUT_DEBUG)
	rm -rf $(DIRECTORY_OUTPUT_RELEASE)
	rm -rf $(DIRECTORY_SYMBOLS)
	rm -rf $(DIRECTORY_OUTPUT_LIB)
	rm -rf $(DIRECTORY_OUTPUT_INCLUDE)
	rm -f $(DIRECTORY_INSTALL_INCLUDE)/jaspinteg.h
	rm -f $(DIRECTORY_INSTALL_LIB)/$(LIB)
	rm -f $(DIRECTORY_INSTALL_LIB)/$(LIB_MAJ)
	rm -f $(DIRECTORY_INSTALL_LIB)/$(LIB_MAJ_MIN)

force: clean setup build install
