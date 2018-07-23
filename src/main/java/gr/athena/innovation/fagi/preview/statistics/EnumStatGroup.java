package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of groups of statistics. Statistics of the same group can be viewed together.
 * 
 * @author nkarag
 */
public enum EnumStatGroup {

    /**
     * Undefined value of the group.
     */
    UNDEFINED(0),

    /**
     * Group that refers to a percent of property values.
     */
    PERCENT(1),

    /**
     * Group that refers to a property statistic.
     */
    PROPERTY(3),
    
    /**
     * Triple based group.
     */
    TRIPLE_BASED(4),
    
    /**
     * Poi based group.
     */
    POI_BASED(5);

	private final int value;
    
	private static final Map<Integer, EnumStatGroup> intToTypeMap = new HashMap<>();
	static {
		for (EnumStatGroup type : EnumStatGroup.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumStatGroup(int value) {
		this.value = value;
	}

    /**
     * Returns the integer value of the group.
     * @return The group  int value.
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the EnumStatGroup object from its integer value or UNDEFINED if the group does not exist.
     * 
     * @param value the integer value of the group type.
     * @return the type of the group.
     */
    public static EnumStatGroup fromInteger(int value) {
		EnumStatGroup type = intToTypeMap.get(value);
		if (type == null)
			return EnumStatGroup.UNDEFINED;
		return type;
	}

    /**
     * Returns the EnumStatGroup object from its Integer value or UNDEFINED if the group does not exist.
     * 
     * @param value The group value.
     * @return The group.
     */
    public static EnumStatGroup fromString(String value) {
		for (EnumStatGroup item : EnumStatGroup.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumStatGroup.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStatGroup> {
        
        /**
         * Deserializes the EnumStatGroup
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStatGroup
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public EnumStatGroup deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumStatGroup.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case PERCENT: return "empty-percent";
            case PROPERTY: return "property";
            case TRIPLE_BASED: return "triple-based";
            case POI_BASED: return "poi-based";
            default: throw new IllegalArgumentException();
        }
    }
}
