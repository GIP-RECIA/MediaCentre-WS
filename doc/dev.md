# to add NOTICE
'mvn notice:check' Checks that a NOTICE file exists and that its content match what would be generated.
'mvn notice:generate' Generates a new NOTICE file, replacing any existing NOTICE file.

# to add licence headers
'mvn license:check' verify if some files miss license header
'mvn license:format' add the license header when missing. If a header is existing, it is updated to the new one.
'mvn license:remove' remove existing license header

# in production mainly set in system properties the property for log directory else it will log in $catalina_base
-Dlogback.logfileDirectory=/PATH/

# to run test:
# locally without any REST API (activated profiles are test,WITHOUT_GAR,WITHOUT_STRUCT_REST,USER_MAPPING - expect on test class declaration)
./mvnw clean test -Dlogback.logfileDirectory=LOGS -Dlogback.loglevel=INFO

# with config context:
export MAVEN_OPTS="-Xms1G -Xmx2G" # could be needed in case of using a context - requesting on real datas
export PORTAL_HOME=.....
./mvnw clean test -Dlogback.logfileDirectory=LOGS -Dspring.profiles.active=USER_MAPPING,test,WITHOUT_STRUCT_REST -Dspring.config.additional-location=${PORTAL_HOME}/mediacentre-ws.yml

# pour la récupération de la liste des ressources diffusable
`curl -X GET 'xxxxxxxx/mediacentre-ws/api/ressourcesDiffusables/' -H 'Accept: application/json' -H 'Content-Type: application/json' --compressed -H 'Pragma: no-cache' -H 'Cache-Control: no-cache' -k -v --tlsv1.2`
ou en xml
`curl -X GET 'xxxxxxxx/mediacentre-ws/api/ressourcesDiffusables/' -H 'Accept: application/xml' -H 'Content-Type: application/json' --compressed -H 'Pragma: no-cache' -H 'Cache-Control: no-cache' -k -v --tlsv1.2`

