package gr.athena.innovation.fagi.core.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for all validation actions.
 * 
 * @author nkarag
 */
public enum EnumValidationAction {

    /**
     * Undefined value of the validation action.
     */
    UNDEFINED(0), 
    
    /**
     * Accepts the link.
     */
    ACCEPT(1), 
    
    /**
     * Rejects the link.
     * If a new fused dataset is chosen as output, none of the respective models of the entities appear in the final dataset.
     */
    REJECT(2), 
    
    /**
     * Keeps the default action, but marks the value as ambiguous in the model using a statement with the POI's URI.
     */    
    ACCEPT_MARK_AMBIGUOUS(3),
    
    /**
     * Rejects the link, but marks the value as ambiguous in the model using a statement with the POI's URI.
     */       
    REJECT_MARK_AMBIGUOUS(4);
    
	private final int value;

	private EnumValidationAction(int value) {
		this.value = value;
	}
    
    /**
     * Returns the integer value of the action.
     * @return The value.
     */
    public int getValue() {
		return this.value;
	}

	private static final Map<Integer, EnumValidationAction> intToTypeMap = new HashMap<>();
	static {
		for (EnumValidationAction type : EnumValidationAction.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

    /**
     * Returns the EnumValidationAction object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the action.
     * @return the type of the action.
     */
    public static EnumValidationAction fromInteger(int value) {
		EnumValidationAction type = intToTypeMap.get(value);
		if (type == null)
			return EnumValidationAction.UNDEFINED;
		return type;
	}

    /**
     * Returns the EnumValidationAction object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The value
     * @return The string value of the action.
     */
    public static EnumValidationAction fromString(String value) {
		for (EnumValidationAction item : EnumValidationAction.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumValidationAction.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumValidationAction> {
        
        /**
         * Deserializes the EnumDatasetAction
         * 
         * @param parser the Json parser
         * @param context the deserialization context
         * @return the EnumValidationAction
         * @throws java.io.IOException I/O exception.
         * @throws com.fasterxml.jackson.core.JsonProcessingException Json processing exception.
         */
		@Override
		public EnumValidationAction deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumValidationAction.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case ACCEPT: return "accept";
        case REJECT: return "reject";
        case ACCEPT_MARK_AMBIGUOUS: return "accept-mark-ambiguous";
        case REJECT_MARK_AMBIGUOUS: return "reject-mark-ambiguous";
        default: throw new IllegalArgumentException();
      }
    }     
}
