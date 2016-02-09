################################################################################
#
# Makefile for the basic Computer Graphics source code
#
# The following lines are the only files that you as a student should edit.
#
################################################################################

# specify the directory where the source code can be found.
SOURCEDIR = src

# specify the name of the executable jar file.
EXECUTABLENAME = cgproject.jar	

# specify the name of the class containing main method.
ENTRYPOINT = main.Renderer
						
# specify the packages where the code can be found
PACKAGES = camera film gui main math sampling shape

################################################################################
# Only the code above this line has to be edited if more classes are added     #
################################################################################

JAVAC = javac
JFLAGS = -g -d $(SOURCEDIR) -classpath $(SOURCEDIR)
JAR = jar

########################
# default build target #
########################

default: all

######################
# target to make all #
######################

all: clean classes jar

###########################################################################
# target which compiles all the .java files specified in the SOURCES list #
###########################################################################

classes: SOURCES := $(foreach dir,$(PACKAGES),$(wildcard $(SOURCEDIR)/$(dir)/*.java))
classes:
	$(info searching .java files from the following packages:)
	$(foreach package,$(PACKAGES),$(info $  $  - $(package)))
	$(info compiling the following .java classes:)
	$(foreach java,$(SOURCES),$(info $  $  - $(java)))
	@$(JAVAC) $(JFLAGS) $(SOURCES)
	$(info finished compilation)
	
#############################################################################
# Creates an executable JAR file from the classes in the SOURCEDIR with the #
# ENTRYPOINT class as the class containing the main method                  #
#############################################################################

jar:
	$(info building $(EXECUTABLENAME))
	@$(JAR) -cfe $(EXECUTABLENAME) $(ENTRYPOINT) -C $(SOURCEDIR)/ .
	$(info finished building $(EXECUTABLENAME))
	
##########################################
# removes all the generated .class files #
##########################################

cleanclasses: CLASSFILES := $(foreach filename,$(foreach dir,$(PACKAGES),$(wildcard $(SOURCEDIR)/$(dir)/*.class)),$(filename))
cleanclasses:
	$(info removing the following .class files:)
	$(foreach filename, ${CLASSFILES}, $(info $  $  - $(filename)))
	@$(foreach filename,${CLASSFILES},rm -f '$(filename)')
	
################################################
# Removes the BINARYDIR and the executable JAR #
################################################
clean: cleanclasses
	@rm -f $(EXECUTABLENAME)
	
