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

`java -jar fagi-1.0-SNAPSHOT.jar -spec /path/to/spec.xml -rules /path/to/rules.xml`

### How to fill in the spec.xml file
Inside the resources directory of the project there is a spec.template.xml file and a spec.xml as an example for convenience. The Specification holds general configuration for the fusion process and is filled with text values between an opening and a closing tag. 
The `INPUTFORMAT` refers to the RDF format of the input dataset and the `OUTPUTFORMAT` holds the value of the desired output format. The accepted RDF formats are the following:

* N-Triples (NT)
* Turtle (TTL)
* RDF/XML (RDF)
* RDF/XML (OWL)
* JSON-LD (JSONLD)
* RDF/JSON (RJ)
* TriG (TRIG)
* N-Quads ()
* TriX (TRIX)

In order to fill the `INPUTFORMAT` and `OUTPUTFORMAT` use the values of the corresponding parenthesis.

The `LEFT`, `RIGHT`, `LINKS` and `TARGET` tags refer to the source and target datasets. Each of these XML tags contain additional tags that describe each of the datasets.

Specifically:
`ID`: An ID to identify the dataset.
`FILE`: The filepath of the dataset. For the target (output) dataset, "System.out" is also accepted as console output.
`ENDPOINT`: Optional tag. Instead of using files, add a SPARQL endpoint and leave the `FILE` tag empty.

`MERGE_WITH`: Specify the final fused dataset. The accepted values are `LEFT` or `RIGHT` in order to merge with one of the source datasets, and `NEW` in order to create a new dataset that contains only the interlinked fused entities. 

### How to fill in the rules.xml file

