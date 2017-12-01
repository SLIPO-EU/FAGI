# Fagi Command Line 
___
### Building from source
In order to build the command line version from source, you should first clone the cli branch to a preferred location by running:

`git clone -b master --single-branch https://github.com/SLIPO-EU/FAGI-gis.git Fagi`

Then, go the the root directory of the project (Fagi-gis) and run:
`mvn clean install`
Tested with git version 1.9.1 and Apache Maven 3.3.3

### Run Fagi-gis from command line
Go to resources directory and change the spec.xml and rules.xml as described below.

Then go to the target directory of the project and run:

`java -jar fagi-1.0-SNAPSHOT.jar -spec /path/to/spec.xml -rules /path/to/rules.xml`

### How to fill in the spec.xml file
Inside the resources directory of the project there is a spec.template.xml file and a spec.xml as an example for convenience. The Specification holds general configuration for the fusion process and is filled with text values between an opening and a closing tag. 
The `inputFormat` refers to the RDF format of the input dataset and the `outputFormat` holds the value of the desired output format. The accepted RDF formats are the following:

* N-Triples (NT)
* Turtle (TTL)
* RDF/XML (RDF)
* RDF/XML (OWL)
* JSON-LD (JSONLD)
* RDF/JSON (RJ)
* TriG (TRIG)
* N-Quads (NQ)
* TriX (TRIX)

In order to fill the `inputFormat` and `outputFormat` use the values of the corresponding parenthesis.

The `left`, `right`, `links` and `target` tags refer to the source and target datasets. Each of these XML tags contain additional tags that describe each of the datasets.

Specifically:
`id`: An ID to identify the dataset.
`file`: The filepath of the dataset. For the target (output) dataset, "System.out" is also accepted as console output.
`endpoint`: Optional tag. Instead of using files, add a SPARQL endpoint and leave the `file` tag empty.

`mergeWith`: Specify the final fused dataset. The accepted values are `left` or `right` in order to merge with one of the source datasets, and `new` in order to create a new dataset that contains only the interlinked fused entities. 

### How to fill in the rules.xml file

The rules.xml file starts with the root element `<rules>`.
We set rules as a `<rule>` element inside the root tag. 

Each <rule> element consists of the following main childs:
`<propertyA>`
`<propertyB>`
`<actionRuleSet>`
`<defaultAction>`

`<propertyA>` and `<propertyB>` define the two RDF properties that the rule will apply.
`<actionRuleSet>` is a set of condition-action pairs with priority the order of appearance.
`<defaultAction>` is the default fusion action to apply if no condition from the <actionRuleSet> is met.

`<actionRuleSet>` element:
This element consists of one or more `<actionRule>` child elements. 
Each <actionRule> is a pair of a condition and a fusion action, namely `<condition>`, `<action>`.
If the condition of an <actionRule> is met, then the fusion action of that <actionRule> is going to be applied and all the rest will be ignored, so the fusion action priority is the order of the <actionRule> appearance. 

The `<condition>` along with the `<action>` are the most essential part of the configuration of the fusion process. 
In order to construct a condition, we assemble a group of logical operations that contain functions to apply on the RDF properties defined above.
We can define a logical operations by using the `<expression>` tag as a child of a condition. 
Then, inside the expression we can put together a combination `<and>`, `<or>` and `<not>` operations and as operands we can use `<function>` elements containing a function or a nested <expression> containing more logical operations.

A sample rules.xml file could look like this: 

<rules>

	<rule>
		<propertyA>dateA</propertyA>
		<propertyB>dateB</propertyB>
		<actionRuleSet>
			<actionRule>
				<condition>
					<expression>
						<function>isKnownDate(b)</function>
					</expression>
				</condition>
				<action>keep-right</action>
			</actionRule>			
			<actionRule>
				<condition>
					<expression>
						<not>
							<function>isKnownDate(a)</function>
						</not>
					</expression>
				</condition>
				<action>keep-both</action>
			</actionRule>		
		</actionRuleSet>
		<defaultAction>keep-left</defaultAction>
	</rule>
	<rule>
		<propertyA>phoneA</propertyA>
		<propertyB>phoneB</propertyB>
		<actionRuleSet>
			<actionRule>
				<condition>
					<function>isSamePhoneNumber(a,b)</function>
				</condition>
				<action>keep-left</action>
			</actionRule>		
		</actionRuleSet>
		<defaultAction>keep-left</defaultAction>
	</rule>	
	
</rules>

### Available functions:

* **isDateKnownFormat:** Checks if the given date String is written as a known format. The known formats are defined at the [specification](../blob/master/src/main/java/gr/athena/innovation/fagi/specification/SpecificationConstants.java)
* **isValidDate:** Evaluates the given date against the target format.
* **isGeometryMoreComplex:** Checks if the first geometry has more points than the second.
* **isLiteralAbbreviation:** Checks if the given literal is or contains an abbreviation of some form.
* **isSameNormalized:** Checks if the two given literals are same. It normalizes the two literals and checks again if the first check fails.
* **isPhoneNumberParsable:** Checks if the given phone number is consisted of only numbers or contains special character and/or exit code.
* **isSamePhoneNumber:** Checks if the given phone numbers are the same. Some phone-normalization steps are executed if the first evaluation fails.
* **isSamePhoneNumberUsingExitCode:** Same as above, except the exit code, which is checked separately using the input value.
* **exists:** Checks if the given property exists in the model of the given entity.

| Name        | Parameters     | Category  | Example
| ------------- |:-------------:| :-----:|:-----:|
| isDateKnownFormat      | a or b | Date | isDateKnownFormat(a)
| isValidDate      | a or b and format | Date | isValidDate(a, DD/MM/YYYY)
| isGeometryMoreComplex | a or b |  Geometry | isGeometryMoreComplex(b)
| isLiteralAbbreviation | a or b | Literal | isLiteralAbbreviation(b) 
| isSameNormalized | a and b | Literal | isSameNormalized(a,b) 
| isPhoneNumberParsable | a or b | Phone | isPhoneNumberParsable(a) 
| isSamePhoneNumber | a and b | Phone | isSamePhoneNumber(a,b)  
| isSamePhoneNumberUsingExitCode | a,b and digits | Phone | isSamePhoneNumberUsingExitCode(a,b,0030)  
| exists | model, property | Property | exists(a,http&#58;//www.w3.org/2000/01/rdf-schema#label)  


### Available fusion actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| keep-left | Both | Keeps the value of the left source dataset in the fused model.
| keep-right | Both | Keeps the value of the right source dataset in the fused model.
| keep-both | Both | Keeps both values of the source datasets in the fused model.
| keep-more-points | Geometry | Keeps the geometry that is composed with more points than the other.
| keep-more-points-and-shift | Geometry | Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry.
| shift-left-geometry | Geometry | Shifts the geometry of the left source entity to the centroid of the right.
| shift-right-geometry | Geometry | Shifts the geometry of the right source entity to the centroid of the left.

### Available default dataset actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| keep-left | Both | Keeps the value of the left source entity in the fused model.
| keep-right | Both | Keeps the value of the right source entity in the fused model.
| keep-both | Both | Keeps both values of the source entities in the fused model.

