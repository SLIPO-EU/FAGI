package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for default dataset actions.
 * 
 * @author nkarag
 */
public enum EnumStatViewType {

    /**
     * Undefined value of the stat type.
     */
    UNDEFINED(0),

    /**
     * This statistic is intended for barchart.
     */
    BAR(1),

    /**
     * This statistic can be viewed for bar-chart.
     */
    LINE(2);

	private final int value;
    
	private static final Map<Integer, EnumStatViewType> intToTypeMap = new HashMap<>();
	static {
		for (EnumStatViewType type : EnumStatViewType.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumStatViewType(int value) {
		this.value = value;
	}

    /**
     * Returns the integer value of the statistic type.
     * @return The value.
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the EnumStatViewType object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the statistic type.
     * @return the type of the statistic.
     */
    public static EnumStatViewType fromInteger(int value) {
		EnumStatViewType type = intToTypeMap.get(value);
		if (type == null)
			return EnumStatViewType.UNDEFINED;
		return type;
	}

    /**
     * Returns the EnumStatViewType object from its Integer value or UNDEFINED if the type does not exist.
     * 
     * @param value The statistic type value.
     * @return The statistic type.
     */
    public static EnumStatViewType fromString(String value) {
		for (EnumStatViewType item : EnumStatViewType.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumStatViewType.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStatViewType> {
        
        /**
         * Deserializes the EnumStatViewType
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStatViewType
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public EnumStatViewType deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumStatViewType.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case BAR: return "bar-chart";
            case LINE: return "line-chart";
            default: throw new IllegalArgumentException();
        }
    }    
}
