# Fagi Command Line 
___
### Building from source
In order to build the command line version from source, you should first clone the cli branch to a preferred location by running:

`git clone -b cli-develop --single-branch https://github.com/SLIPO-EU/FAGI-gis.git Fagi`

Then, go the the root directory of the project (Fagi-gis) and run:
`mvn clean install`
Tested with git version 1.9.1 and Apache Maven 3.3.3

### Run Fagi-gis from command line
Go to config.properties inside the resources folder and change the configuration as you need.

Then go to the target directory of the project and run:

`java -jar fagi-1.0-SNAPSHOT.jar -spec /path/to/spec.xml -rules /path/to/rules.xml`

### How to fill in the spec.xml file
Inside the resources directory of the project there is a spec.template.xml file and a spec.xml as an example for convenience. The Specification holds general configuration for the fusion process and is filled with text values between an opening and a closing tag. 
The `INPUT_FORMAT` refers to the RDF format of the input dataset and the `OUTPUT_FORMAT` holds the value of the desired output format. The accepted RDF formats are the following:

* N-Triples (NT)
* Turtle (TTL)
* RDF/XML (RDF)
* RDF/XML (OWL)
* JSON-LD (JSONLD)
* RDF/JSON (RJ)
* TriG (TRIG)
* N-Quads (NQ)
* TriX (TRIX)

In order to fill the `INPUT_FORMAT` and `OUTPUT_FORMAT` use the values of the corresponding parenthesis.

The `LEFT`, `RIGHT`, `LINKS` and `TARGET` tags refer to the source and target datasets. Each of these XML tags contain additional tags that describe each of the datasets.

Specifically:
`ID`: An ID to identify the dataset.
`FILE`: The filepath of the dataset. For the target (output) dataset, "System.out" is also accepted as console output.
`ENDPOINT`: Optional tag. Instead of using files, add a SPARQL endpoint and leave the `FILE` tag empty.

`MERGE_WITH`: Specify the final fused dataset. The accepted values are `LEFT` or `RIGHT` in order to merge with one of the source datasets, and `NEW` in order to create a new dataset that contains only the interlinked fused entities. 

### How to fill in the rules.xml file

The rules.xml file starts with the root element `<RULES>`.
We set rules as a `<RULE>` element inside the root tag. 

Each <RULE> element consists of the following main childs:
`<PROPERTYA>`
`<PROPERTYB>`
`<ACTION_RULE_SET>`
`<DEFAULT_ACTION>`

`<PROPERTYA>` and `<PROPERTYB>` define the two RDF properties that the rule will apply.
`<ACTION_RULE_SET>` is a set of condition-action pairs with priority the order of appearance.
`<DEFAULT_ACTION>` is the default fusion action to apply if no condition from the <ACTION_RULE_SET> is met.

`<ACTION_RULE_SET>` element:
This element consists of one or more `<ACTION_RULE>` child elements. 
Each <ACTION_RULE> is a pair of a condition and a fusion action, namely `<CONDITION>`, `<ACTION>`.
If the condition of an <ACTION_RULE> is met, then the fusion action of that <ACTION_RULE> is going to be applied and all the rest will be ignored, so the fusion action priority is the order of the <ACTION_RULE> appearance. 

The `<CONDITION>` along with the `<ACTION>` are the most essential part of the configuration of the fusion process. 
In order to construct a condition, we assemble a group of logical operations that contain functions to apply on the RDF properties defined above.
We can define a logical operations by using the `<EXPRESSION>` tag as a child of a condition. 
Then, inside the expression we can put together a combination `<AND>`, `<OR>` and `<NOT>` operations and as operands we can use `<FUNCTION>` elements containing a function or a nested <EXPRESSION> containing more logical operations.

A sample rules.xml file could look like this: 

<RULES>

	<RULE>
		<PROPERTYA>dateA</PROPERTYA>
		<PROPERTYB>dateB</PROPERTYB>
		<ACTION_RULE_SET>
			<ACTION_RULE>
				<CONDITION>
					<EXPRESSION>
						<FUNCTION>isKnownDate(B)</FUNCTION>
					</EXPRESSION>
				</CONDITION>
				<ACTION>Keep Right</ACTION>
			</ACTION_RULE>			
			<ACTION_RULE>
				<CONDITION>
					<EXPRESSION>
						<NOT>
							<FUNCTION>isKnownDate(A)</FUNCTION>
						</NOT>
					</EXPRESSION>
				</CONDITION>
				<ACTION>Keep Both</ACTION>
			</ACTION_RULE>		
		</ACTION_RULE_SET>
		<DEFAULT_ACTION>Keep Left</DEFAULT_ACTION>
	</RULE>
	<RULE>
		<PROPERTYA>phoneA</PROPERTYA>
		<PROPERTYB>phoneB</PROPERTYB>
		<ACTION_RULE_SET>
			<ACTION_RULE>
				<CONDITION>
					<FUNCTION>isSamePhoneNumber(A,B)</FUNCTION>
				</CONDITION>
				<ACTION>Keep Left</ACTION>
			</ACTION_RULE>		
		</ACTION_RULE_SET>
		<DEFAULT_ACTION>Keep Left</DEFAULT_ACTION>
	</RULE>	
	
</RULES>

# Available functions:
| Name        | Parameters     | Category  | Example
| ------------- |:-------------:| :-----:|:-----:|
| isDateKnownFormat      | A or B | Date | isDateKnowFormat(A)
| isValidDate      | A or B and format | Date | isValidDate(A, DD/MM/YYYY)
| isGeometryMoreComplicated | A or B |  Geometry | isGeometryMoreComplicated(B)
| isLiteralAbbreviation | A or B | Literal | isLiteralAbbreviation(B) 
| isPhoneNumberParsable | A or B | Phone | isPhoneNumberParsable(A) 
| isSamePhoneNumber | A and B | Phone | isSamePhoneNumber(A,B)  
| isSamePhoneNumberUsingExitCode | A,B and digits | Phone | isSamePhoneNumberUsingExitCode(A,B,0030)  
| exists | model, property | Property | exists(A,ht&#8203;tp://www.w3.org/2000/01/rdf-schema#label)  


# Available fusion actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| Keep Left | Both | Keeps the value of the left source dataset in the fused model.
| Keep Right | Both | Keeps the value of the right source dataset in the fused model.
| Keep Both | Both | Keeps both values of the source datasets in the fused model.
| Keep More Points | Geometry | Keeps the geometry that is composed with more points than the other.
| Keep More Points And Shift | Geometry | Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry
| Shift Left Geometry | Geometry | Shifts the geometry of the left source entity to the centroid of the right.
| Shift Right Geometry | Geometry | Shifts the geometry of the right source entity to the centroid of the left.

# Available default dataset actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| Keep Left | Both | Keeps the value of the left source entity in the fused model.
| Keep Right | Both | Keeps the value of the right source entity in the fused model.
| Keep Both | Both | Keeps both values of the source entities in the fused model.

