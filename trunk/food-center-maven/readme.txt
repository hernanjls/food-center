Food Order Center
=====================

Pre Req:
---------------------

jdk1.6 
	(or jdk1.7, but basically compilation will be generated using jdk1.6 because gap doesn't support jdk1.7)
	http://www.oracle.com/technetwork/java/javase/downloads/index.html
	set env variables (assuming it is installed to "somepath/jdk1.7.0_09":
		JAVA_HOME = "somepath/jdk1.7.0_09"
		JRE_HOME = "somepath/jdk1.7.0_09/jre"
	add to path JAVA_HOME/bin

maven 3:
	(it is usfull when m2e fails to download dependencies)
	download http://maven.apache.org/ and unpack
	set env variables:
		M2
		M2_HOME
	add to path M2_HOME/bin
		
eclipse juno ee: 
	www.eclipse.org

m2e 1.2 
	eclipse plugin, install from eclipse update site:
	http://download.eclipse.org/technology/m2e/releases

m2e-wtp
	eclipse plugin, install from update site (Maven Integration for WTP)
	http://download.jboss.org/jbosstools/updates/m2eclipse-wtp

subclipse
	eclipse plugin, for getting the code from svn, install from update site
	http://subclipse.tigris.org/update_1.6.x
	
gae 1.7.3
	it is needed only for eclipse integration...
	eclipse plugin, install from update site:
	https://dl.google.com/eclipse/plugin/4.2

gwt sdk 2.5.0 (Only if eclipse makes problem):
	download full sdk, and unpack it to: eclipse/plugins/com.google.gwt.eclipse.sdkbundle_2.5.0.v201211121240-rel-r42/gwt-2.5.0
	https://developers.google.com/web-toolkit/download

Opening our docs:
--------------------------
UMLs: 
	visual paradigm open docs/food-center.vpp

System diagram:
	load docs/food-center-system-dia.xml in http://www.diagram.ly/

Gantt:
	load docs/food-center-gantt.gxml in https://app.gantter.com/
	
Getting the source code:
--------------------------
	-	use subclipse in eclipse and import the project parent folder from::
		http://food-center.googlecode.com/svn/trunk/food-center
	-	import the sub-modules into eclipse using add existing project into workspace.
	-	right click on foodcenter-server, maven, update project.
	-	right click on foodcenter-server, properties, Google->Web Application
			make sure "This project has a WAR directory" is checked.
			make sure WAR dir is src/main/webapp
			make sure "Launch and deploy from this dir..."  is NOT checked
	-	right click on the project and run as, maven install (it will download all the dependencies)
	
Known Issues:
-------------------------
	-	if m2e fails to download the dependencies, run from cmdon the parent folder: mvn clean install -U

	
