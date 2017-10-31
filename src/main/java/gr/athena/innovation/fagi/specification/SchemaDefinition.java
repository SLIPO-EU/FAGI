package gr.athena.innovation.fagi.specification;

/**
 * Class for holding XSDs as strings.
 * 
 * @author nkarag
 */
public class SchemaDefinition {
    public static final String SPEC_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "<xs:element name=\"SPECIFICATION\">	\n" +
        "  <xs:complexType>\n" +
        "    <xs:sequence>\n" +
        "		<xs:element name=\"INPUT_FORMAT\" type=\"xs:string\"/>\n" +
        "		<xs:element name=\"OUTPUT_FORMAT\" type=\"xs:string\"/>\n" +
        "		<xs:element name=\"LEFT\">\n" +
        "		  <xs:complexType>\n" +
        "			<xs:sequence>\n" +
        "			  <xs:element name=\"ID\" type=\"xs:string\"/>\n" +
        "			  <xs:element name=\"ENDPOINT\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "			  <xs:element name=\"FILE\" type=\"xs:anyURI\"/>\n" +
        "			</xs:sequence>\n" +
        "		  </xs:complexType>\n" +
        "		</xs:element> \n" +
        "		<xs:element name=\"RIGHT\">\n" +
        "		  <xs:complexType>\n" +
        "			<xs:sequence>\n" +
        "			  <xs:element name=\"ID\" type=\"xs:string\"/>\n" +
        "			  <xs:element name=\"ENDPOINT\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "			  <xs:element name=\"FILE\" type=\"xs:anyURI\"/>\n" +
        "			</xs:sequence>\n" +
        "		  </xs:complexType>\n" +
        "		</xs:element>\n" +
        "		<xs:element name=\"LINKS\">\n" +
        "		  <xs:complexType>\n" +
        "			<xs:sequence>\n" +
        "			  <xs:element name=\"ID\" type=\"xs:string\"/>\n" +
        "			  <xs:element name=\"ENDPOINT\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "			  <xs:element name=\"FILE\" type=\"xs:anyURI\"/>\n" +
        "			</xs:sequence>\n" +
        "		  </xs:complexType>\n" +
        "		</xs:element>\n" +
        "		<xs:element name=\"TARGET\">\n" +
        "		  <xs:complexType>\n" +
        "			<xs:sequence>\n" +
        "			  <xs:element name=\"ID\" type=\"xs:string\"/>\n" +
        "			  <xs:element name=\"RESOURCE_URI\" type=\"xs:anyURI\"/>\n" +
        "			  <xs:element name=\"MERGE_WITH\" type=\"xs:string\"/>			  \n" +
        "			  <xs:element name=\"FILE\" type=\"xs:anyURI\"/>\n" +
        "			</xs:sequence>\n" +
        "		  </xs:complexType>\n" +
        "		</xs:element>				\n" +
        "    </xs:sequence>\n" +
        "  </xs:complexType>\n" +
        "  </xs:element>\n" +
        "</xs:schema> ";
    
    public static final String RULE_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "  <xs:element name=\"RULES\">\n" +
        "    <xs:complexType>\n" +
        "      <xs:sequence>\n" +
        "		<xs:element name=\"DEFAULT_DATASET_ACTION\" type=\"xs:string\"/>\n" +
        "        <xs:element name=\"RULE\" maxOccurs=\"unbounded\">\n" +
        "          <xs:complexType>\n" +
        "            <xs:sequence>\n" +
        "              <xs:element name=\"PROPERTYA\" type=\"xs:anyURI\" />\n" +
        "              <xs:element name=\"PROPERTYB\" type=\"xs:anyURI\" />\n" +
        "              <xs:element name=\"ACTION_RULE_SET\" minOccurs=\"0\">\n" +
        "                <xs:complexType>\n" +
        "                  <xs:sequence>\n" +
        "                    <xs:element name=\"ACTION_RULE\">\n" +
        "                      <xs:complexType>\n" +
        "                        <xs:sequence>\n" +
        "                          <xs:element name=\"CONDITION\">\n" +
        "							  <xs:complexType>\n" +
        "								<xs:sequence>\n" +
        "								  <xs:element name=\"FUNCTION\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "								  <xs:element name=\"EXPRESSION\" minOccurs=\"0\">\n" +
        "							        <xs:complexType>\n" +
        "										<xs:sequence>\n" +
        "											<xs:element name=\"NOT\" minOccurs=\"0\">\n" +
        "											  <xs:complexType>\n" +
        "												<xs:sequence>\n" +
        "												  <xs:element name=\"FUNCTION\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "												</xs:sequence>\n" +
        "											  </xs:complexType>\n" +
        "											</xs:element> \n" +
        "											<xs:element name=\"AND\" minOccurs=\"0\">\n" +
        "											  <xs:complexType>\n" +
        "												<xs:sequence>\n" +
        "												  <xs:element name=\"FUNCTION\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "												</xs:sequence>\n" +
        "											  </xs:complexType>\n" +
        "											</xs:element> \n" +
        "											<xs:element name=\"OR\" minOccurs=\"0\">\n" +
        "											  <xs:complexType>\n" +
        "												<xs:sequence>\n" +
        "												  <xs:element name=\"FUNCTION\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "												</xs:sequence>\n" +
        "											  </xs:complexType>\n" +
        "											</xs:element> \n" +
        "										</xs:sequence>\n" +
        "									</xs:complexType>\n" +
        "								  </xs:element>									  \n" +
        "								</xs:sequence>\n" +
        "							  </xs:complexType>\n" +
        "						  </xs:element> 							  \n" +
        "                          <xs:element name=\"ACTION\" type=\"xs:string\"/>\n" +
        "                        </xs:sequence>\n" +
        "                      </xs:complexType>\n" +
        "                    </xs:element>\n" +
        "                  </xs:sequence>\n" +
        "                </xs:complexType>\n" +
        "              </xs:element>\n" +
        "              <xs:element type=\"xs:string\" name=\"DEFAULT_ACTION\"/>\n" +
        "            </xs:sequence>\n" +
        "          </xs:complexType>\n" +
        "        </xs:element>\n" +
        "      </xs:sequence>\n" +
        "    </xs:complexType>\n" +
        "  </xs:element>\n" +
        "</xs:schema>";
}
