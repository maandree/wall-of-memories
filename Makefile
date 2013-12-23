# Copying and distribution of this file, with or without modification,
# are permitted in any medium without royalty provided the copyright
# notice and this notice are preserved.  This file is offered as-is,
# without any warranty.


## You should set JAVA (defaults /usr/bin/java) to the absolute path of your `java` command:
##    make JAVA="$(which java)"

## If the compiler cannot find jni.h, try adding JDK_PATH:
##    make JDK_PATH=/usr/lib/jvm/java-7-openjdk



C_OPTIMISE    = -g -Og
JAVA_OPTIMISE = -g
JAR_COMPRESS  = 

JAR           = jar
JAVA          = /usr/bin/java
JAVAC         = javac
JAVAH         = javah

C_EXTRA       = 
C_STD         = gnu99
C_WARN        = -Wall -Wextra -pedantic
C_FLAGS       = $(C_OPTIMISE) -std=$(C_STD) $(C_WARN) $(C_EXTRA) -fPIC

CPP_EXTRA     = 
CPP_FLAGS     = $(CPP_EXTRA) $(FH_EXPORT) -Iobj
ifneq ($(JDK_PATH),)
CPP_FLAGS    += -I"$(JDK_PATH)/include" -I"$(JDK_PATH)/include/linux"
endif

LD_EXTRA      = 
LD_OPTIONS    = -shared -dynamic
LD_FLAGS      = $(LD_OPTIONS) $(LD_EXTRA)

JAVA_EXTRA    = 
JAVA_VERSION  = 
JAVA_WARN     = -Xlint:all
JAVA_VERFLAGS = $(foreach V, $(JAVA_VERSION), -source $(V) -target $(V))
JAVA_FLAGS    = $(JAVA_EXTRA) $(JAVA_WARN) $(JAVA_VERFLAGS) $(JAVA_OPTIMISE) -cp obj:src -s src -d obj

JAVAH_EXTRA   = 
JAVAH_FLAGS   = $(JAVAH_EXTRA) -d obj -cp obj

JAR_FLAGS     = cfm$(JAR_COMPRESS)
MANIFEST      = META-INF/MANIFEST.MF

LIB_SUFFIX    = .so
LIB_PREFIX    = lib

OBJ_JAVA      = $(shell find src/ | grep '\.java$$' | sed -e 's:\.java$$:\.class:' -e 's:^src:obj:')
OBJ_C         = $(shell find src/ | grep '\.c$$'    | sed -e 's:\.c$$:\.o:'        -e 's:^src:obj:')



.PHONY: all
all: app # doc

.PHONY: lastvt
app: bin/wall-of-memories # bin/$(LIB_PREFIX)wall-of-memories$(LIB_SUFFIX)

.PHONY: doc
doc: info pdf dvi ps

.PHONY: info
info: wall-of-memories.info

.PHONY: pdf
pdf: wall-of-memories.pdf

.PHONY: dvi
dvi: wall-of-memories.dvi

.PHONY: ps
ps: wall-of-memories.ps



%.info: info/%.texinfo info/*.texinfo
	$(MAKEINFO) "$<"

%.pdf: info/%.texinfo info/*.texinfo
	texi2pdf "$<"

%.dvi: info/%.texinfo info/*.texinfo
	$(TEXI2DVI) "$<"

%.ps: info/%.texinfo info/*.texinfo
	texi2pdf --ps "$<"



bin/wall-of-memories: bin/wall-of-memories.jar
	echo '#!$(JAVA) -jar' > "$@"
	cat "$<" >> "$@"
	chmod a+x "$@"

bin/wall-of-memories.jar: $(MANIFEST) $(OBJ_JAVA)
	@mkdir -p bin
	$(JAR) $(JAR_FLAGS) "$@" $(MANIFEST) $(foreach C, $(OBJ_JAVA), -C obj $(shell echo "$(C)" | sed -e 's:^obj/::'))

bin/$(LIB_PREFIX)wall-of-memories$(LIB_SUFFIX): $(OBJ_C)
	@mkdir -p bin
	$(CC) $(C_FLAGS) $(LD_FLAGS) $^ -o "$@"

obj/%.class: src/%.java
	@mkdir -p obj
	$(JAVAC) $(JAVA_FLAGS) "$<"

obj/%.h: obj/%.class
	$(JAVAH) $(JAVAH_FLAGS) $(shell echo "$<" | sed -e 's:^obj/::' -e 's:\.class$$::')

obj/%.o: src/%.c obj/%.h
	$(CC) $(C_FLAGS) $(CPP_FLAGS) -c "$<" -o "$@"



.PHONY: clean
clean:
	-rm -r bin obj
	-rm *.info *.pdf *.ps *.dvi
	-rm *.aux *.log *.toc *.cp *.cps *.fn *.fns *.ky *.kys *.pg *.pgs *.tp *.tps *.vr *.vrs
	-rm *.install */*.install

