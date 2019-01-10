package gr.athena.innovation.fagi.specification;

/**
 * Class for holding XSDs as strings.
 * 
 * @author nkarag
 */
public class SchemaDefinition {

    public static final String CONFIG_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
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
"		</xs:element>\n" +
"		<xs:element name=\"locale\" minOccurs=\"0\">\n" +
"		  <xs:simpleType>\n" +
"			<xs:restriction base=\"xs:string\">\n" +
"			  <xs:enumeration value=\"\"/>\n" +
"			  <xs:enumeration value=\"EN\"/>\n" +
"			  <xs:enumeration value=\"en\"/>\n" +
"			  <xs:enumeration value=\"EN-GB\"/>\n" +
"			  <xs:enumeration value=\"EN-US\"/>			  			  \n" +
"			  <xs:enumeration value=\"ENGLISH\"/>\n" +
"			  <xs:enumeration value=\"english\"/>\n" +
"			  <xs:enumeration value=\"DE\"/>\n" +
"			  <xs:enumeration value=\"de\"/>\n" +
"			  <xs:enumeration value=\"GERMAN\"/>\n" +
"			  <xs:enumeration value=\"german\"/>\n" +
"			  <xs:enumeration value=\"de-AT\"/>\n" +
"			  <xs:enumeration value=\"DE-AT\"/>\n" +
"			  <xs:enumeration value=\"EL\"/>\n" +
"			  <xs:enumeration value=\"el\"/>\n" +
"			  <xs:enumeration value=\"greek\"/>\n" +
"			</xs:restriction>\n" +
"		  </xs:simpleType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"similarity\" minOccurs=\"0\">\n" +
"		  <xs:simpleType>\n" +
"			<xs:restriction base=\"xs:string\">\n" +
"			  <xs:enumeration value=\"\"/>\n" +
"			  <xs:enumeration value=\"sortedjarowinkler\"/>\n" +
"			  <xs:enumeration value=\"jarowinkler\"/>\n" +
"			  <xs:enumeration value=\"cosine\"/>\n" +
"			  <xs:enumeration value=\"levenshtein\"/>			  			  \n" +
"			  <xs:enumeration value=\"jaro\"/>\n" +
"			  <xs:enumeration value=\"2Gram\"/>\n" +
"			  <xs:enumeration value=\"longestcommonsubsequence\"/>\n" +
"			</xs:restriction>\n" +
"		  </xs:simpleType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"verbose\" type=\"xs:boolean\" />		\n" +
"		<xs:element name=\"rules\" type=\"xs:anyURI\"/>		\n" +
"		<xs:element name=\"left\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"categories\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"date\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element> \n" +
"		<xs:element name=\"right\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"categories\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"date\" type=\"xs:string\" minOccurs=\"0\" />\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"links\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"endpoint\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"linksFormat\">\n" +
"				<xs:simpleType>\n" +
"				  <xs:restriction base=\"xs:string\">\n" +
"				    <xs:enumeration value=\"csv\"/>\n" +
"				    <xs:enumeration value=\"csv-unique-links\"/>\n" +
"				    <xs:enumeration value=\"nt\"/>				    \n" +
"				  </xs:restriction>\n" +
"				</xs:simpleType>\n" +
"			  </xs:element>			  \n" +
"			  <xs:element name=\"file\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>\n" +
"		<xs:element name=\"target\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
"			  <xs:element name=\"mode\">\n" +
"				<xs:simpleType>\n" +
"				  <xs:restriction base=\"xs:string\">\n" +
"				    <xs:enumeration value=\"aa_mode\"/>\n" +
"				    <xs:enumeration value=\"bb_mode\"/>\n" +
"				    <xs:enumeration value=\"ab_mode\"/>\n" +
"				    <xs:enumeration value=\"ba_mode\"/>\n" +
"				    <xs:enumeration value=\"a_mode\"/>\n" +
"				    <xs:enumeration value=\"b_mode\"/>\n" +
"				    <xs:enumeration value=\"l_mode\"/>				    \n" +
"				  </xs:restriction>\n" +
"				</xs:simpleType>\n" +
"			  </xs:element>			  \n" +
"			  <xs:element name=\"outputDir\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"fused\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"remaining\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"ambiguous\" type=\"xs:anyURI\"/>\n" +
"			  <xs:element name=\"statistics\" type=\"xs:anyURI\"/>\n" +
"                         <xs:element name=\"fusionLog\" type=\"xs:anyURI\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element>				\n" +
"		<xs:element name=\"ML\">\n" +
"		  <xs:complexType>\n" +
"			<xs:sequence>\n" +
"			  <xs:element name=\"name\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"address\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"website\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"phone\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"email\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			  <xs:element name=\"validation\" type=\"xs:anyURI\" minOccurs=\"0\"/>\n" +
"			</xs:sequence>\n" +
"		  </xs:complexType>\n" +
"		</xs:element> \n" +
"    </xs:sequence>\n" +
"  </xs:complexType>\n" +
"  </xs:element>\n" +
"</xs:schema> ";
    
    public static final String RULE_XSD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
"	<xs:element name=\"rules\">\n" +
"		<xs:complexType>\n" +
"			<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"				\n" +
"				<xs:element name=\"defaultDatasetAction\">\n" +
"				  <xs:simpleType>\n" +
"					<xs:restriction base=\"xs:string\">\n" +
"					  <xs:enumeration value=\"keep-left\"/>\n" +
"					  <xs:enumeration value=\"keep-right\"/>\n" +
"					  <xs:enumeration value=\"keep-both\"/>\n" +
"					</xs:restriction>\n" +
"				  </xs:simpleType>\n" +
"				</xs:element>\n" +
"				\n" +
"				<xs:element name=\"validationRule\" minOccurs=\"0\" maxOccurs=\"unbounded\">		\n" +
"					<xs:complexType>\n" +
"						<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"						  <xs:element name=\"externalProperty\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"							<xs:complexType>\n" +
"							  <xs:simpleContent>\n" +
"								<xs:extension base=\"xs:string\">\n" +
"								  <xs:attribute name=\"id\" type=\"xs:string\" use=\"required\"/>\n" +
"								</xs:extension>\n" +
"							  </xs:simpleContent>\n" +
"							</xs:complexType>\n" +
"						  </xs:element> \n" +
"						  <xs:element name=\"actionRuleSet\" minOccurs=\"0\">\n" +
"							<xs:complexType>\n" +
"							  <xs:sequence>\n" +
"								<xs:element name=\"actionRule\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"								  <xs:complexType>\n" +
"									<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"									  <xs:element name=\"condition\">\n" +
"										  <xs:complexType>\n" +
"											<xs:sequence>\n" +
"											  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"											  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"												<xs:complexType>\n" +
"												 <xs:sequence>\n" +
"													<xs:element name=\"not\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"													<xs:element name=\"and\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"													<xs:element name=\"or\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"												 </xs:sequence>\n" +
"												</xs:complexType>\n" +
"											  </xs:element>									  \n" +
"											</xs:sequence>\n" +
"										  </xs:complexType>\n" +
"									  </xs:element> 							  \n" +
"									  <xs:element name=\"action\">\n" +
"										  <xs:simpleType>\n" +
"											<xs:restriction base=\"xs:string\">\n" +
"											  <xs:enumeration value=\"accept\"/>\n" +
"											  <xs:enumeration value=\"reject\"/>\n" +
"											  <xs:enumeration value=\"reject-mark-ambiguous\"/>\n" +
"											  <xs:enumeration value=\"accept-mark-ambiguous\"/>\n" +
"											  <xs:enumeration value=\"ml-validation\"/>\n" +
"											</xs:restriction>\n" +
"										  </xs:simpleType>\n" +
"									  </xs:element>                          \n" +
"									</xs:choice>\n" +
"								  </xs:complexType>\n" +
"								</xs:element>\n" +
"							  </xs:sequence>\n" +
"							</xs:complexType>\n" +
"						  </xs:element>\n" +
"						  <xs:element name=\"defaultAction\">\n" +
"							<xs:simpleType>\n" +
"							  <xs:restriction base=\"xs:string\">\n" +
"								  <xs:enumeration value=\"accept\"/>\n" +
"								  <xs:enumeration value=\"reject\"/>\n" +
"								  <xs:enumeration value=\"reject-mark-ambiguous\"/>\n" +
"								  <xs:enumeration value=\"accept-mark-ambiguous\"/>\n" +
"								  <xs:enumeration value=\"ml-validation\"/>\n" +
"							  </xs:restriction>\n" +
"							</xs:simpleType>\n" +
"						  </xs:element>     \n" +
"						</xs:choice>\n" +
"					</xs:complexType>\n" +
"				</xs:element> 				\n" +
"				\n" +
"				<xs:element name=\"rule\" minOccurs=\"0\" maxOccurs=\"unbounded\">		\n" +
"					<xs:complexType>\n" +
"						<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"						  <xs:element name=\"propertyA\" type=\"xs:string\" />\n" +
"						  <xs:element name=\"propertyB\" type=\"xs:string\" />\n" +
"						  <xs:element name=\"externalProperty\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"							<xs:complexType>\n" +
"							  <xs:simpleContent>\n" +
"								<xs:extension base=\"xs:string\">\n" +
"								  <xs:attribute name=\"id\" type=\"xs:string\" use=\"required\"/>\n" +
"								</xs:extension>\n" +
"							  </xs:simpleContent>\n" +
"							</xs:complexType>\n" +
"						  </xs:element> \n" +
"						  <xs:element name=\"actionRuleSet\" minOccurs=\"0\">\n" +
"							<xs:complexType>\n" +
"							  <xs:sequence>\n" +
"								<xs:element name=\"actionRule\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"								  <xs:complexType>\n" +
"									<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"									  <xs:element name=\"condition\">\n" +
"										  <xs:complexType>\n" +
"											<xs:sequence>\n" +
"											  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"											  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"												<xs:complexType>\n" +
"												 <xs:sequence>\n" +
"													<xs:element name=\"not\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"													<xs:element name=\"and\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"													<xs:element name=\"or\" minOccurs=\"0\">\n" +
"													  <xs:complexType>\n" +
"														<xs:sequence>\n" +
"														  <xs:element name=\"function\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														  <xs:element name=\"expression\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
"														</xs:sequence>\n" +
"													  </xs:complexType>\n" +
"													</xs:element> \n" +
"												 </xs:sequence>\n" +
"												</xs:complexType>\n" +
"											  </xs:element>									  \n" +
"											</xs:sequence>\n" +
"										  </xs:complexType>\n" +
"									  </xs:element> 							  \n" +
"									  <xs:element name=\"action\">\n" +
"										  <xs:simpleType>\n" +
"											<xs:restriction base=\"xs:string\">\n" +
"											  <xs:enumeration value=\"keep-left\"/>\n" +
"                                             <xs:enumeration value=\"keep-left-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"keep-right\"/>\n" +
"                                             <xs:enumeration value=\"keep-right-mark-ambiguous\"/>\n" +             
"											  <xs:enumeration value=\"concatenate\"/>\n" +
"											  <xs:enumeration value=\"concatenate-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"concatenate-geometry\"/>\n" +
"											  <xs:enumeration value=\"concatenate-geometry-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"keep-longest\"/>\n" +
"											  <xs:enumeration value=\"keep-longest-mark-ambiguous\"/>\n" +   
"											  <xs:enumeration value=\"keep-recommended\"/>\n" +
"											  <xs:enumeration value=\"keep-recommended-mark-ambiguous\"/>\n" +
"											  <xs:enumeration value=\"keep-both\"/>\n" +
"											  <xs:enumeration value=\"keep-both-mark-ambiguous\"/>\n" +
"											  <xs:enumeration value=\"keep-most-complete-name\"/>\n" +
"											  <xs:enumeration value=\"keep-most-complete-name-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"keep-more-points\"/>\n" +
"											  <xs:enumeration value=\"keep-more-points-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"keep-more-points-and-shift\"/>\n" +
"											  <xs:enumeration value=\"keep-more-points-and-shift-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"shift-left-geometry\"/>\n" +
"											  <xs:enumeration value=\"shift-left-geometry-mark-ambiguous\"/>\n" +            
"											  <xs:enumeration value=\"shift-right-geometry\"/>\n" +
"											  <xs:enumeration value=\"shift-right-geometry-mark-ambiguous\"/>\n" +            
"											</xs:restriction>\n" +
"										  </xs:simpleType>\n" +
"									  </xs:element>                          \n" +
"									</xs:choice>\n" +
"								  </xs:complexType>\n" +
"								</xs:element>\n" +
"							  </xs:sequence>\n" +
"							</xs:complexType>\n" +
"						  </xs:element>\n" +
"						  <xs:element name=\"defaultAction\">\n" +
"							<xs:simpleType>\n" +
"							  <xs:restriction base=\"xs:string\">\n" +
"								  <xs:enumeration value=\"keep-left\"/>\n" +
"                                 <xs:enumeration value=\"keep-left-mark-ambiguous\"/>\n" +
"								  <xs:enumeration value=\"keep-right\"/>\n" +
"                                 <xs:enumeration value=\"keep-right-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"concatenate\"/>\n" +
"								  <xs:enumeration value=\"concatenate-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"concatenate-geometry\"/>\n" +      
"								  <xs:enumeration value=\"concatenate-geometry-mark-ambiguous\"/>\n" +              
"								  <xs:enumeration value=\"keep-most-complete-name\"/>\n" +
"								  <xs:enumeration value=\"keep-most-complete-name-mark-ambiguous\"/>\n" +     
"								  <xs:enumeration value=\"keep-longest\"/>\n" +
"								  <xs:enumeration value=\"keep-longest-mark-ambiguous\"/>\n" +  
"								  <xs:enumeration value=\"keep-recommended\"/>\n" +
"								  <xs:enumeration value=\"keep-recommended-mark-ambiguous\"/>\n" +
"								  <xs:enumeration value=\"keep-both\"/>\n" +
"								  <xs:enumeration value=\"keep-both-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"keep-more-points\"/>\n" +
"								  <xs:enumeration value=\"keep-more-points-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"keep-more-points-and-shift\"/>\n" +
"								  <xs:enumeration value=\"keep-more-points-and-shift-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"shift-left-geometry\"/>\n" +
"								  <xs:enumeration value=\"shift-left-geometry-mark-ambiguous\"/>\n" +            
"								  <xs:enumeration value=\"shift-right-geometry\"/>\n" +
"								  <xs:enumeration value=\"shift-right-geometry-mark-ambiguous\"/>\n" +            
"							  </xs:restriction>\n" +
"							</xs:simpleType>\n" +
"						  </xs:element>     \n" +
"						</xs:choice>\n" +
"					</xs:complexType>\n" +
"				</xs:element>\n" +
"				\n" +
"			</xs:choice>\n" +
"		</xs:complexType>\n" +
"	</xs:element>\n" +
"</xs:schema>";
}
