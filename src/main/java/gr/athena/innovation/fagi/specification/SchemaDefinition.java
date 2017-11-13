package gr.athena.innovation.fagi.specification;

/**
 * Class for holding XSDs as strings.
 * 
 * @author nkarag
 */
public class SchemaDefinition {
    public static final String SPEC_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
"<xs:element name=\"specification\">	\n" +
"  <xs:complexType>\n" +
"    <xs:sequence>\n" +
"		<xs:element name=\"inputFormat\">\n" +
"		  <xs:simpleType>\n" +
"			<xs:restriction base=\"xs:string\">\n" +
"			  <xs:enumeration value=\"NT\"/>\n" +
"			  <xs:enumeration value=\"TTL\"/>\n" +
"			  <xs:enumeration value=\"RDF\"/>\n" +
"			  <xs:enumeration value=\"OWL\"/>\n" +
"			  <xs:enumeration value=\"JSONLD\"/>			  \n" +
"			  <xs:enumeration value=\"RJ\"/>\n" +
"			  <xs:enumeration value=\"TRIG\"/>\n" +
"			  <xs:enumeration value=\"NQ\"/>\n" +
"			  <xs:enumeration value=\"TRIX\"/>\n" +
"			</xs:restriction>\n" +
"		  </xs:simpleType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"outputFormat\">\n" +
"		  <xs:simpleType>\n" +
"			<xs:restriction base=\"xs:string\">\n" +
"			  <xs:enumeration value=\"NT\"/>\n" +
"			  <xs:enumeration value=\"TTL\"/>\n" +
"			  <xs:enumeration value=\"RDF\"/>\n" +
"			  <xs:enumeration value=\"OWL\"/>\n" +
"			  <xs:enumeration value=\"JSONLD\"/>			  \n" +
"			  <xs:enumeration value=\"RJ\"/>\n" +
"			  <xs:enumeration value=\"TRIG\"/>\n" +
"			  <xs:enumeration value=\"NQ\"/>\n" +
"			  <xs:enumeration value=\"TRIX\"/>\n" +
"			</xs:restriction>\n" +
"		  </xs:simpleType>\n" +
"		</xs:element>		\n" +
"		<xs:element name=\"left\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element> \n" +
"		<xs:element name=\"right\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"links\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"target\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"resourceURI\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"mergeWith\">\n" +
"				<xs:simpleType>\n" +
"				  <xs:restriction base=\"xs:string\">\n" +
"				    <xs:enumeration value=\"left\"/>\n" +
"				    <xs:enumeration value=\"right\"/>\n" +
"				    <xs:enumeration value=\"new\"/>\n" +
"				  </xs:restriction>\n" +
"				</xs:simpleType>\n" +
"			  </xs:element>				  \n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>				\n" +
"    </xs:sequence>\n" +
"  </xs:complexType>\n" +
"  </xs:element>\n" +
"</xs:schema> ";
    
    public static final String RULE_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "  <xs:element name=\"rules\">\n" +
        "    <xs:complexType>\n" +
        "      <xs:sequence>\n" +
        "		<xs:element name=\"defaultDatasetAction\">\n" +
        "		  <xs:simpleType>\n" +
        "			<xs:restriction base=\"xs:string\">\n" +
        "			  <xs:enumeration value=\"keep-left\"/>\n" +
        "			  <xs:enumeration value=\"keep-right\"/>\n" +
        "			  <xs:enumeration value=\"keep-both\"/>\n" +
        "		    </xs:restriction>\n" +
        "		  </xs:simpleType>\n" +
        "		</xs:element>\n" +
        "        <xs:element name=\"rule\" maxOccurs=\"unbounded\">\n" +
        "          <xs:complexType>\n" +
        "            <xs:sequence>\n" +
        "              <xs:element name=\"propertyA\" type=\"xs:anyURI\" />\n" +
        "              <xs:element name=\"propertyB\" type=\"xs:anyURI\" />\n" +
        "              <xs:element name=\"actionRuleSet\" minOccurs=\"0\">\n" +
        "                <xs:complexType>\n" +
        "                  <xs:sequence>\n" +
        "                    <xs:element name=\"actionRule\">\n" +
        "                      <xs:complexType>\n" +
        "                        <xs:sequence>\n" +
        "                          <xs:element name=\"condition\">\n" +
        "							  <xs:complexType>\n" +
        "								<xs:sequence>\n" +
        "								  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "								  <xs:element name=\"expression\" minOccurs=\"0\">\n" +
        "							        <xs:complexType>\n" +
        "									 <xs:sequence>\n" +
        "										<xs:element name=\"not\" minOccurs=\"0\">\n" +
        "										  <xs:complexType>\n" +
        "											<xs:sequence>\n" +
        "											  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "											</xs:sequence>\n" +
        "										  </xs:complexType>\n" +
        "										</xs:element> \n" +
        "										<xs:element name=\"and\" minOccurs=\"0\">\n" +
        "										  <xs:complexType>\n" +
        "											<xs:sequence>\n" +
        "											  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "											</xs:sequence>\n" +
        "										  </xs:complexType>\n" +
        "										</xs:element> \n" +
        "										<xs:element name=\"or\" minOccurs=\"0\">\n" +
        "										  <xs:complexType>\n" +
        "											<xs:sequence>\n" +
        "											  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "											</xs:sequence>\n" +
        "										  </xs:complexType>\n" +
        "										</xs:element> \n" +
        "									 </xs:sequence>\n" +
        "									</xs:complexType>\n" +
        "								  </xs:element>									  \n" +
        "								</xs:sequence>\n" +
        "							  </xs:complexType>\n" +
        "						  </xs:element> 							  \n" +
        "                          <xs:element name=\"action\">\n" +
        "							  <xs:simpleType>\n" +
        "								<xs:restriction base=\"xs:string\">\n" +
        "								  <xs:enumeration value=\"keep-left\"/>\n" +
        "								  <xs:enumeration value=\"keep-right\"/>\n" +
        "								  <xs:enumeration value=\"keep-both\"/>\n" +
        "								  <xs:enumeration value=\"keep-more-points\"/>\n" +
        "								  <xs:enumeration value=\"keep-more-points-and-shift\"/>\n" +
        "								  <xs:enumeration value=\"shift-left-geometry\"/>\n" +
        "								  <xs:enumeration value=\"shift-right-geometry\"/>\n" +
        "								</xs:restriction>\n" +
        "							  </xs:simpleType>\n" +
        "						  </xs:element>                          \n" +
        "                        </xs:sequence>\n" +
        "                      </xs:complexType>\n" +
        "                    </xs:element>\n" +
        "                  </xs:sequence>\n" +
        "                </xs:complexType>\n" +
        "              </xs:element>\n" +
        "              <xs:element name=\"defaultAction\">\n" +
        "				<xs:simpleType>\n" +
        "				  <xs:restriction base=\"xs:string\">\n" +
        "					<xs:enumeration value=\"keep-left\"/>\n" +
        "					<xs:enumeration value=\"keep-right\"/>\n" +
        "					<xs:enumeration value=\"keep-both\"/>\n" +
        "					<xs:enumeration value=\"keep-more-points\"/>\n" +
        "					<xs:enumeration value=\"keep-more-points-and-shift\"/>\n" +
        "					<xs:enumeration value=\"shift-left-geometry\"/>\n" +
        "					<xs:enumeration value=\"shift-right-geometry\"/>\n" +
        "				  </xs:restriction>\n" +
        "				</xs:simpleType>\n" +
        "			  </xs:element>     \n" +
        "            </xs:sequence>\n" +
        "          </xs:complexType>\n" +
        "        </xs:element>\n" +
        "      </xs:sequence>\n" +
        "    </xs:complexType>\n" +
        "  </xs:element>\n" +
        "</xs:schema>";
}
