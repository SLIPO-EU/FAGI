package gr.athena.innovation.fagi.preview;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumStat {

    /**
     * Undefined value of the statistic.
     */
    UNDEFINED("undefined"),

    /**
     * Number of POI entities in each input dataset.
     */
    TOTAL_POIS("totalPois"),
    
    /**
     * Total number of triples in each input dataset.
     */    
    TOTAL_TRIPLES("totalTriples");

	private final String value;
    
	private static final Map<String, EnumStat> intToTypeMap = new HashMap<>();
	static {
		for (EnumStat type : EnumStat.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumStat(String value) {
		this.value = value;
	}

    /**
     * Returns the string value (key) of the statistic.
     * @return The value.
     */
    public String getKey() {
		return this.value;
	}
    
    /**
     * Returns the EnumStat object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The statistic value.
     * @return The statistic.
     */
    public static EnumStat fromString(String value) {
		for (EnumStat item : EnumStat.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumStat.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStat> {
        
        /**
         * Deserializes the EnumStat
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStat
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public EnumStat deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumStat.fromString(parser.getValueAsString());
		}
	}

    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POI entities in each input dataset.";
            case TOTAL_TRIPLES: return "Total number of triples in each input dataset.";
            default: throw new IllegalArgumentException();
        }
    }  
}
