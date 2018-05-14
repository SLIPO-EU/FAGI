package gr.athena.innovation.fagi.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for left or right entity (a or b accordingly).
 * 
 * @author nkarag
 */
public enum EnumEntity {

    /**
     * Undefined value of the entity.
     */
    UNDEFINED(0),
    
    /**
     * Represents the LEFT or "A" entity.
     */
    LEFT(1),

    /**
     * Represents the RIGHT or "B" entity.
     */
    RIGHT(2);

	private final int value;
    
	private static final Map<Integer, EnumEntity> intToTypeMap = new HashMap<>();
	static {
		for (EnumEntity type : EnumEntity.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumEntity(int value) {
		this.value = value;
	}

    /**
     * Returns the integer value of the entity.
     * @return The value.
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the EnumEntity object from its integer value or UNDEFINED.
     * 
     * @param value the integer value of the entity.
     * @return the type of the entity.
     */
    public static EnumEntity fromInteger(int value) {
		EnumEntity type = intToTypeMap.get(value);
		return type;
	}

    /**
     * Returns the EnumEntity object from its String value or UNDEFINED.
     * 
     * @param value The value.
     * @return The EnumEntity string value.
     */
    public static EnumEntity fromString(String value) {
		for (EnumEntity item : EnumEntity.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumEntity.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumEntity> {
        
        /**
         * Deserializes the EnumEntity
         * 
         * @param parser the Json parser
         * @param context the deserialization context
         * @return the EnumEntity
         * @throws java.io.IOException I/O exception.
         * @throws com.fasterxml.jackson.core.JsonProcessingException Json processing exception.
         */
		@Override
		public EnumEntity deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumEntity.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case LEFT: return "left";
        case RIGHT: return "right";
        default: throw new IllegalArgumentException();
      }
    }    
}
