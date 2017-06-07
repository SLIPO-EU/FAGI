# Fagi Command Line 
___
### Building from source
In order to build the command line version from source, you should first clone the cli branch to a preferred location by running:

`git clone -b cli --single-branch https://github.com/SLIPO-EU/FAGI-gis.git Fagi-gis`

Then, go the the root directory of the project (Fagi-gis) and run:
`mvn package`
Tested with git version 1.9.1 and Apache Maven 3.3.3

### Run Fagi-gis from command line
Go to config.properties inside the resources folder and change the configuration as you need.

Then go to the target directory of the project and run:

`java -jar fagi-1.0-SNAPSHOT.jar -config /path/to/config.properties`