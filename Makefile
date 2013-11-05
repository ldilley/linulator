# $Id$
# LinPot Makefile

# Compiler options
CC      = gcc
INC     = -Iinclude
CFLAGS  = -g $(INC) -pedantic -pipe -O2 -Wall

# Linker options
LINK    = gcc
LFLAGS  =
LIBS    =

# Files
HEADERS = include/account.h \
	  include/config.h \
	  include/filesystem.h \
	  include/limits.h \
	  include/linpot.h \
	  include/log.h \
	  include/network.h \
	  include/process.h \
	  include/system.h \
	  include/types.h \
	  include/version.h
SOURCES = src/config.c \
	  src/linpot.c \
	  src/log.c \
	  src/network.c
OBJECTS = src/config.o \
	  src/linpot.o \
	  src/log.o \
	  src/network.o
TARGET  = linpot

# Build rules
$(TARGET): $(OBJECTS)
	$(LINK) $(LFLAGS) -o $(TARGET) $(OBJECTS) $(LIBS)

clean:
	rm -f $(OBJECTS) $(TARGET) $(TARGET).core
