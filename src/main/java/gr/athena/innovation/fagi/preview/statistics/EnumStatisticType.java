package gr.athena.innovation.fagi.preview.statistics;

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
public enum EnumStatisticType {

    /**
     * Undefined value of the statistic type.
     */
    UNDEFINED(0),
    /**
     * Total POIs.
     */
    TOTAL_POIS(1),

    /**
     * Non-empty type.
     */
    NON_EMPTY_NAMES(2),

    /**
     * Linked-type.
     */
    NON_EMPTY_PHONES(3),

    /**
     * Total-type.
     */
    EMPTY_NAMES(4),  
    /**
     * Total-type.
     */
    EMPTY_PHONES(5), 
    /**
     * Total-type.
     */
    LINKED_NON_EMPTY_NAMES(6),   
    /**
     * Total-type.
     */
    DISTINCT_PROPERTIES(7), 
    /**
     * Total-type.
     */
    NAME_PERCENT(8), 
    /**
     * Total-type.
     */
    PHONE_PERCENT(9);

	private final int value;
    
	private static final Map<Integer, EnumStatisticType> intToTypeMap = new HashMap<>();
	static {
		for (EnumStatisticType type : EnumStatisticType.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumStatisticType(int value) {
		this.value = value;
	}

    /**
     * Returns the integer value of the type.
     * @return The value.
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the EnumStatisticType object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the type.
     * @return the type of the type.
     */
    public static EnumStatisticType fromInteger(int value) {
		EnumStatisticType type = intToTypeMap.get(value);
		if (type == null){
            return EnumStatisticType.UNDEFINED;
        }
			
		return type;
	}

    /**
     * Returns the EnumStatisticType object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The statistic type value.
     * @return The statistic type.
     */
    public static EnumStatisticType fromString(String value) {
		for (EnumStatisticType item : EnumStatisticType.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumStatisticType.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStatisticType> {
        
        /**
         * Deserializes the EnumStatisticType
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStatisticType
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public EnumStatisticType deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumStatisticType.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case TOTAL_POIS: return "total POIs";
        case EMPTY_NAMES: return "empty names";
        case EMPTY_PHONES: return "empty phones";
        case NON_EMPTY_NAMES: return "non empty names";
        case NON_EMPTY_PHONES: return "non empty phones";
        case LINKED_NON_EMPTY_NAMES: return "linked non empty names";
        case NAME_PERCENT: return "names percent";
        case PHONE_PERCENT: return "phones percent";
        case DISTINCT_PROPERTIES: return "distinct properties";

        default: throw new IllegalArgumentException();
      }
    }       
}
