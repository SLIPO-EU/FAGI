package gr.athena.innovation.fagi.core.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for all fusion actions.
 * 
 * @author nkarag
 */
public enum EnumFusionAction {
    UNDEFINED(0), KEEP_LEFT(1), KEEP_RIGHT(2), KEEP_BOTH(3), 
    KEEP_MORE_POINTS(4), KEEP_MORE_POINTS_AND_SHIFT(5), SHIFT_LEFT_GEOMETRY(6), SHIFT_RIGHT_GEOMETRY(7);
    
	private final int value;

	private EnumFusionAction(int value) {
		this.value = value;
	}
    
	public int getValue() {
		return this.value;
	}

	private static final Map<Integer, EnumFusionAction> intToTypeMap = new HashMap<>();
	static {
		for (EnumFusionAction type : EnumFusionAction.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

	public static EnumFusionAction fromInteger(int value) {
		EnumFusionAction type = intToTypeMap.get(value);
		if (type == null)
			return EnumFusionAction.UNDEFINED;
		return type;
	}

	public static EnumFusionAction fromString(String value) {
		for (EnumFusionAction item : EnumFusionAction.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumFusionAction.UNDEFINED;
	}

	public static class Deserializer extends JsonDeserializer<EnumFusionAction> {

		@Override
		public EnumFusionAction deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumFusionAction.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case KEEP_LEFT: return "Keep Left";
        case KEEP_RIGHT: return "Keep Right";
        case KEEP_BOTH: return "Keep Both";
        case KEEP_MORE_POINTS: return "Keep More Points";
        case KEEP_MORE_POINTS_AND_SHIFT: return "Keep More Points And Shift";
        case SHIFT_LEFT_GEOMETRY: return "Shift Left Geometry";
        case SHIFT_RIGHT_GEOMETRY: return "Shift Right Geometry";
        default: throw new IllegalArgumentException();
      }
    }     
}
