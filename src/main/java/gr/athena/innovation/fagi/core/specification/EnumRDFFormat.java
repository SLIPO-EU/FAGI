package gr.athena.innovation.fagi.core.specification;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumRDFFormat {
    DEFAULT(0), NT(1), TTL(2), RDF(3), OWL(4), JSONLD(5), RJ(6), TRIG(7), TRIX(8), NQ(9);
    
	private final int value;

	private EnumRDFFormat(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	
    private static final Map<Integer, EnumRDFFormat> intToTypeMap = new HashMap<>();    
	static {
		for (EnumRDFFormat type : EnumRDFFormat.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
    
	public static EnumRDFFormat fromInteger(int value) {
		EnumRDFFormat type = intToTypeMap.get(value);
		if (type == null)
			return EnumRDFFormat.DEFAULT;
		return type;
	}

	public static EnumRDFFormat fromString(String value) {
		for (EnumRDFFormat item : EnumRDFFormat.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumRDFFormat.DEFAULT;
	}
    
    @Override
    public String toString() {
        switch(this) {
            case DEFAULT: return "NT";
            case NT: return "NT";
            case TTL: return "TTL";
            case RDF: return "RDF";
            case OWL: return "OWL";
            case JSONLD: return "JSONLD";
            case RJ: return "RJ";
            case TRIG: return "TRIG";        
            case TRIX: return "TRIX";
            case NQ: return "NQ";          
            default: return "NT";
        }
    }      
}
