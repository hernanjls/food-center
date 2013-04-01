Current Version is using:
- App Engine SDK               - 1.7.6
- Google Web Toolkit (GWT) SDK - 2.5.1
- Android SDK                  - 21

===============================================================================
Updating to newer App Engine SDK
===============================================================================

Assuming your new App Engine SDK is installed in {@SDK}.

Remove the following files from food-center-appengine/war/WEB-INF/lib and 
replace them with the new files (SEE Notes 1st): 
- appengine-api-1.0-sdk-X.X.X.jar with  {@SDK}/lib/user/appengine-api-1.0-sdk-X.X.X.jar.
- appengine-jsr107cache-X.X.X.jar with  {@SDK}/lib/user/appengine-jsr107cache-X.X.X.jar.
- jsr107cache-X.X.jar with              {@SDK}/lib/user/jsr107cache-X.X.jar.
- appengine-api-labs.jar with           {@SDK}/lib/opt/user/appengine-api-labs/vX/appengine-api-labs.jar.
- appengine-endpoints.jar with          {@SDK}/lib/opt/user/appengine-endpoints/vX/appengine-endpoints.jar.
- asm-X.X.jar with                      {@SDK}/lib/opt/tools/datanucleus/vX/asm-X.X.jar.
- datanucleus-api-jdo-X.X.X.jar with    {@SDK}/lib/opt/tools/datanucleus/vX/datanucleus-api-jdo-X.X.X.jar.
- datanucleus-api-jpa-X.X.X.jar with    {@SDK}/lib/opt/tools/datanucleus/vX/datanucleus-api-jpa-X.X.X.jar.
- datanucleus-core-X.X.X.jar with       {@SDK}/lib/opt/tools/datanucleus/vX/datanucleus-core-X.X.X.jar.
- geronimo-jpa_2.0_spec-X.X.jar with    {@SDK}/lib/opt/tools/datanucleus/vX/geronimo-jpa_2.0_spec-X.X.jar.
- jdo-api-X.X.X.jar with                {@SDK}/lib/opt/tools/datanucleus/vX/jdo-api-X.X.X.jar.
- jta-X.X.jar with                      {@SDK}/lib/opt/tools/datanucleus/vX/jta-X.X.jar.

Notes:
- There is no need to replace files which has a version in their name, and has
  the same version as the new files.
- X represents the newest version in the {@SDK}

Update project build path with the new files.


===============================================================================
Updating to newer Google Web Toolkit (GWT) SDK
===============================================================================

Assuming your new GWT SDK is installed in {@GWT}.

Remove the following files from food-center-appengine/war/WEB-INF/lib and
replace them with the new files (SEE Notes 1st):
- TODO.

Notes:
- There is no need to replace files which has a version in their name, and has
  the same version as the new files.
- X represents the newest version in the {@GWT}

Update project build path with the new files.


===============================================================================
Updating to newer Android SDK
===============================================================================

Assuming your new Android SDK is installed in {@ANDROID}.

Update Eclipse Android SDK tools.

Remove the following files from food-center-android/libs and replace them with the new files:
- None.

Update project build path with the new files.
