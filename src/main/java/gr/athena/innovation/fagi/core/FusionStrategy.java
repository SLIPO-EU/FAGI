package gr.athena.innovation.fagi.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for fusion strategy of POI-ensembles.
 * 
 * @author nkarag
 */
public enum FusionStrategy {

    /**
     * Undefined value of the fusion strategy.
     */
    UNDEFINED(0),

    /**
     * Keeps a unique value based on voting strategy.
     */
    KEEP_UNIQUE_BY_VOTE(1),

    /**
     * Keeps all values.
     */
    KEEP_ALL(2),

    /**
     * Keeps a unique value without a strategy. 
     */
    KEEP_ANY(3);

    private final int value;

    private static final Map<Integer, FusionStrategy> intToTypeMap = new HashMap<>();
    static {
            for (FusionStrategy type : FusionStrategy.values()) {
                    intToTypeMap.put(type.value, type);
            }
    }

    private FusionStrategy(int value) {
            this.value = value;
    }

    /**
     * Returns the integer value of the strategy.
     * @return The value.
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the FusionStrategy object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the strategy.
     * @return the type of the strategy.
     */
    public static FusionStrategy fromInteger(int value) {
		FusionStrategy type = intToTypeMap.get(value);
		if (type == null)
			return FusionStrategy.UNDEFINED;
		return type;
	}

    /**
     * Returns the FusionStrategy object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The fusion strategy value.
     * @return The fusion strategy.
     */
    public static FusionStrategy fromString(String value) {
		for (FusionStrategy item : FusionStrategy.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return FusionStrategy.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<FusionStrategy> {

        /**
         * Deserializes the FusionStrategy.
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the FusionStrategy
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public FusionStrategy deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return FusionStrategy.fromString(parser.getValueAsString());
		}
	}

    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case KEEP_UNIQUE_BY_VOTE: return "keep-unique";
            case KEEP_ALL: return "keep-all";
            case KEEP_ANY: return "keep-any";
            default: throw new IllegalArgumentException();
        }
    }    
}
