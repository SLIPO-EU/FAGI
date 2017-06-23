package gr.athena.innovation.fagi.core.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for all metadata actions.
 * 
 * @author nkarag
 */
public enum EnumMetadataActions {
    
    UNDEFINED(0), KEEP_BOTH_METADATA(1), KEEP_LEFT_METADATA(2), KEEP_RIGHT_METADATA(3), 
    FLATTEN_LEFT_METADATA(4), FLATTEN_RIGHT_METADATA(5);
    
	private final int value;

	private EnumMetadataActions(int value) {
		this.value = value;
	}
    
	public int getValue() {
		return this.value;
	}

	private static final Map<Integer, EnumMetadataActions> intToTypeMap = new HashMap<>();
	static {
		for (EnumMetadataActions type : EnumMetadataActions.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

	public static EnumMetadataActions fromInteger(int value) {
		EnumMetadataActions type = intToTypeMap.get(value);
		if (type == null)
			return EnumMetadataActions.UNDEFINED;
		return type;
	}

	public static EnumMetadataActions fromString(String value) {
		for (EnumMetadataActions item : EnumMetadataActions.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumMetadataActions.UNDEFINED;
	}

	public static class Deserializer extends JsonDeserializer<EnumMetadataActions> {

		@Override
		public EnumMetadataActions deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumMetadataActions.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case KEEP_BOTH_METADATA: return "Keep Both Metadata";
        case KEEP_LEFT_METADATA: return "Keep Left Metadata";
        case KEEP_RIGHT_METADATA: return "Keep Right Metadata";
        case FLATTEN_LEFT_METADATA: return "Flatten Left Metadata";
        case FLATTEN_RIGHT_METADATA: return "Flatten Right Metadata";
        default: throw new IllegalArgumentException();
      }
    }    
}
