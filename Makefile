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
PACKAGES = camera gui main math sampling shape

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
	@echo -e "\nsearching .java files from the following packages:"
	@echo -e "$(foreach package,$(PACKAGES),\n\t$(package))"
	@echo -e "\ncompiling the following .java classes:"
	@echo -e "$(foreach java,$(SOURCES),\n\t$(java))"
	@$(JAVAC) $(JFLAGS) $(SOURCES)
	@echo -e "\nfinished compilation"
	
#############################################################################
# Creates an executable JAR file from the classes in the SOURCEDIR with the #
# ENTRYPOINT class as the class containing the main method                  #
#############################################################################

jar:
	@echo -e "\nbuilding $(EXECUTABLENAME)"
	@$(JAR) -cfe $(EXECUTABLENAME) $(ENTRYPOINT) -C $(SOURCEDIR)/ .
	@echo -e "finished building $(EXECUTABLENAME)"
	
##########################################
# removes all the generated .class files #
##########################################

cleanclasses: CLASSFILES := $(foreach filename,$(foreach dir,$(PACKAGES),$(wildcard $(SOURCEDIR)/$(dir)/*.class)),$(filename))
cleanclasses:
	@echo -e "\nremoving the following .class files:"
	@echo -e $(foreach filename,${CLASSFILES},'\n\t$(filename)')
	@$(foreach filename,${CLASSFILES},rm -f '$(filename)')
	
################################################
# Removes the BINARYDIR and the executable JAR #
################################################
clean: cleanclasses
	@rm -f $(EXECUTABLENAME)
	
