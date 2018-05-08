# Fagi Command Line 
___
### Building from source
The following instructions were tested with git version 1.9.1 and Apache Maven 3.3.3. In order to build the command line version from source, you should first clone the master branch to a preferred location by running:

`git clone -b master --single-branch https://github.com/SLIPO-EU/FAGI-gis.git Fagi`

Then, go the the root directory of the project (Fagi-gis) and run:
`mvn clean install`

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

The `locale` is optional in case a dataset contains entities from regions with different locales, but it is strongly recommended to choose one when possible because it is used on several steps of the normalization process. The available locales are: 
* EN 
* EN-GB
* EN-US
* DE
* DE-DE
* DE-AT
* EL

The `similarity` is also optional and it is used as a part of the custom matching process (default is JaroWinkler). The available values (case-insensitive) are the following:
* sortedjarowinkler 
* jarowinkler
* cosine
* jaro
* levenshtein
* 2Gram
* longestcommonsubsequence

The `left`, `right`, `links` and `target` tags refer to the source and target datasets. Each of these XML tags contain additional tags that describe each of the datasets.

Specifically:

`id`: An ID to identify the dataset.

`file`: The filepath of the dataset. For the target (output) dataset.

`endpoint`: Optional tag. Instead of using files, add a SPARQL endpoint and leave the `file` tag empty.

`categories`: This is again optional. It is used to extract statistics about the categories of the entities. If you want to use this feature you should provide a file in N-Triples format that contains the categorization.

`mode`: Specify the fused dataset mode. The supported modes are shown in the table below.

`outputDir`: This is the directory path under which all produced files will be written. The results should be one or two files with the fused datasets (based on selected fusion mode described below), and one file containing statistics about the datasets and the fusion process.

`fused`: Optional tag. Specifies the output filepath of the fused dataset (based on fusion mode). If no value is specified the default name will be "fused.nt" under the output directory defined above.

`remaining`: Optional tag. Specifies the output filepath of the non-fused dataset (based on fusion mode). If no value is specified the default name will be "remaining.nt" under the output directory defined above.

`ambiguous`: Optional tag. Specifies the output filepath of the dataset containing ambiguous linked entities. If no value is specified the default name will be "ambiguous.nt" under the output directory defined above.

`statistics`: Optional tag. Specifies the path of the statistics file. By default a file with name "statistics.txt" will be written under the output directory defined above.

| Mode        | Description     
| ------------- |:-------------:|
| aa_mode | Only linked triples are handled: Fused triples replace the respective ones of dataset A (the fusion output is exclusively written on A). 
| bb_mode | Only linked triples are handled: Fused triples replace the respective ones of dataset B (the fusion output is exclusively written on B). 
| ab_mode | All triples are handled: Fused triples replace the respective ones of dataset A; Un-linked triples of dataset B are copied as-is into dataset A 
| ba_mode | All triples are handled: Fused triples replace the respective ones of dataset B; Un-linked triples of dataset A are copied as-is into dataset B 
| a_mode | All triples are handled: Fused triples replace the respective ones of dataset A; Fused triples are removed from dataset B, which only maintains the remaining, unlinked triples 
| b_mode | All triples are handled: Fused triples replace the respective ones of dataset B; Fused triples are removed from dataset A, which only maintains the remaining, unlinked triples 
| l_mode | Only linked triples are handled: Only fused triples are written in a third dataset. 

### How to fill in the rules.xml file

The rules.xml file starts with the root element `<rules>`.
We set rules as a `<rule>` element inside the root tag. 

Each <rule> element consists of the following main childs:

`<propertyA>`

`<propertyB>`

`<externalProperty>`

`<actionRuleSet>`

`<defaultAction>`.


* `<propertyA>` and `<propertyB>` define the two RDF properties that the rule will apply.
* `<externalProperty>` is optional and is used to combine different properties inside a condition. The fusion action does not affect the value of this property. The external property requires an id attribute as a parameter in the XML and the id must start with the letter a or be that refers to the corresponding value (left or right) and followed by an incrementing integer for each different property used in the same rule.
* `<actionRuleSet>` is a set of condition-action pairs with priority the order of appearance.
* `<defaultAction>` is the default fusion action to apply if no condition from the <actionRuleSet> is met.

* `<actionRuleSet>` element:
This element consists of one or more `<actionRule>` child elements. 
Each <actionRule> is a pair of a condition and a fusion action, namely `<condition>`, `<action>`.
If the condition of an <actionRule> is met, then the fusion action of that <actionRule> is going to be applied and all the rest will be ignored, so the fusion action priority is the order of the <actionRule> appearance. 

* The `<condition>` along with the `<action>` are the most essential part of the configuration of the fusion process. 
In order to construct a condition, we assemble a group of logical operations that contain functions to apply on the RDF properties defined above.
We can define a logical operation by using the `<expression>` tag as a child of a condition. 
Then, inside the expression we can put together a combination of `<and>`, `<or>` and `<not>` operations. Αs operands we can use `<function>` elements containing a function or a nested <expression> containing more logical operations. The depth of the nested expressions supported currently is 2 levels of same logical operations. 

Except fusion rules which are defined with the `<rule>` tag, there is an option to add validation rules using the `<validationRule>` tag. With a validation rule we can accept/reject and/or mark a link as ambiguous in the model. The validation rules follow the exact same logic described above with the only difference being that the fusion actions are replaced with the validation actions, both described at the tables below.

A sample rules.xml file could look like this: 

<rules>

	<validationRule>
		<externalProperty id="a1">phoneA contactValueA</externalProperty>
		<externalProperty id="b1">phoneB contactValueB</externalProperty>
		<actionRuleSet>
			<actionRule>
				<condition>
					<expression>
						<or>
							<expression>
								<and>
									<function>isSamePhoneNumberCustomNormalize(a1,b1)</function>
									<function>isSameCustomNormalize(a,b,0.6)</function>
								</and>
							</expression>
							<expression>
								<not>
									<function>isSameCustomNormalize(a,b,0.5)</function>
								</not>
							</expression>
						</or>
					</expression>			
				</condition>
				<action>reject-mark-ambiguous</action>
			</actionRule>
		</actionRuleSet>
		<defaultAction>accept</defaultAction>
	</validationRule>	
	<rule>
		<propertyA>dateA lastModifiedA</propertyA>
		<propertyB>dateB lastModifiedB</propertyB>
		<externalProperty id="a1">label</externalProperty>
		<externalProperty id="b1">label</externalProperty>		
		<actionRuleSet>
			<actionRule>
				<condition>
					<expression>
						<function>isLiteralAbbreviation(b1)</function>
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
		<propertyA>phoneA contactValueA</propertyA>
		<propertyB>phoneB contactValueB</propertyB>
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

* **isDateKnownFormat:** Checks if the given date String is written as a known format. The known formats are defined at the [specification](../master/src/main/java/gr/athena/innovation/fagi/specification/SpecificationConstants.java)
* **isValidDate:** Evaluates the given date against the target format.
* **isGeometryMoreComplex:** Checks if the first geometry has more points than the second.
* **isLiteralAbbreviation:** Checks if the given literal is or contains an abbreviation of some form.
* **isSameSimpleNormalize:** Checks if the two given literals are same. It normalizes the two literals with some basic steps and uses the provided similarity (default JaroWinkler) and returns true if the result is above the provided threshold. Threshold should be between (0,1) using dot as decimal point.
* **isSameCustomNormalize:** Checks if the two given literals are same. It normalizes the two literals with some extra steps in addition to the simple normalization. Then, it uses the provided similarity (default JaroWinkler) and returns true if the result is above the provided threshold. Threshold should be between (0,1) using dot as decimal point.
* **isPhoneNumberParsable:** Checks if the given phone number is consisted of only numbers or contains special character and/or exit code.
* **isSamePhoneNumber:** Checks if the given phone numbers are the same. Some phone-normalization steps are executed if the first evaluation fails.
* **isSamePhoneNumberCustomNormalize:** Checks if the given phone numbers are the same. Some phone-normalization. If the equality fails, some custom steps for normalization are executed and the function rechecks for equality (e.g two numbers are considered same if one of them does not contain a country code but the line number is the same etc).
* **isSamePhoneNumberUsingExitCode:** Same as above, except the exit code, which is checked separately using the input value.
* **exists:** Checks if the given property exists in the model of the entity.
* **notExists:** The reverse function of exists. Returns true if the selected property is not found in the model.

| Name        | Parameters     | Category  | Example
| ------------- |:-------------:| :-----:|:-----:|
| isDateKnownFormat      | a or b | Date | isDateKnownFormat(a)
| isDatePrimaryFormat      | a or b | Date | isDatePrimaryFormat(a)
| isValidDate      | a or b and format | Date | isValidDate(a, DD/MM/YYYY)
| isGeometryMoreComplex | a or b |  Geometry | isGeometryMoreComplex(b)
| isLiteralAbbreviation | a or b | Literal | isLiteralAbbreviation(b) 
| isSameSimpleNormalize | a, b and threshold| Literal | isSameSimpleNormalize(a,b, 0.7) 
| isSameCustomNormalize | a, b and threshold| Literal | isSameCustomNormalize(a,b, 0.6) 
| isPhoneNumberParsable | a or b | Phone | isPhoneNumberParsable(a) 
| isSamePhoneNumber | a and b | Phone | isSamePhoneNumber(a,b)  
| isSamePhoneNumberCustomNormalize | a and b | Phone | isSamePhoneNumberCustomNormalize(a,b)  
| isSamePhoneNumberUsingExitCode | a,b and digits | Phone | isSamePhoneNumberUsingExitCode(a,b,0030)  
| exists | a or b | Property | exists(a)  
| notExists | a or b | Property | notExists(b)  


### Available fusion actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| keep-left | Both | Keeps the value of the left source dataset in the fused model.
| keep-right | Both | Keeps the value of the right source dataset in the fused model.
| concatenate | Literal | Keeps both values of the source datasets as a concatenated literal in the same property of the fused model.
| keep-longest | Literal | Keeps the value of the longest literal in the fused model using the NFC normalization before comparing the literals.
| keep-both | Both | Keeps both values of the source datasets in the fused model.
| keep-more-points | Geometry | Keeps the geometry that is composed with more points than the other.
| keep-more-points-and-shift | Geometry | Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry.
| shift-left-geometry | Geometry | Shifts the geometry of the left source entity to the centroid of the right.
| shift-right-geometry | Geometry | Shifts the geometry of the right source entity to the centroid of the left.

### Available validation actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| accept | Link | Accepts a link based on the rule property.
| reject| Link | Rejects the whole link based on the rule property.
| accept-mark-ambiguous | Link | Keeps the default fusion action data, but marks the property as ambiguous by adding a statement to the model.
| reject-mark-ambiguous | Link | Rejects the link, but marks the property as ambiguous by adding a statement to the model.

### Available default dataset actions:
| Name        | Type | Description
| ------------- |:-------------|:------|
| keep-left | Both | Keeps the value of the left source entity in the fused model.
| keep-right | Both | Keeps the value of the right source entity in the fused model.
| keep-both | Both | Keeps both values of the source entities in the fused model.

