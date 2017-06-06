package gr.athena.innovation.fagi.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Enumeration for all geometry actions.
 * 
 * @author nkarag
 */
public enum EnumGeometricActions {
	UNDEFINED(0), KEEP_LEFT_GEOMETRY(1), KEEP_RIGHT_GEOMETRY(2), KEEP_BOTH_GEOMETRIES(3), 
    KEEP_MORE_POINTS(4), KEEP_MORE_POINTS_AND_SHIFT(5), SHIFT_LEFT_GEOMETRY(6), SHIFT_RIGHT_GEOMETRY(7);

    private static final Logger logger = LogManager.getRootLogger();
    
	private final int value;
    
	private static final Map<Integer, EnumGeometricActions> intToTypeMap = new HashMap<>();
	static {
		for (EnumGeometricActions type : EnumGeometricActions.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
    
	private EnumGeometricActions(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static EnumGeometricActions fromInteger(int value) {
		EnumGeometricActions type = intToTypeMap.get(value);
		if (type == null)
			return EnumGeometricActions.UNDEFINED;
		return type;
	}

	public static EnumGeometricActions fromString(String value) {
		for (EnumGeometricActions item : EnumGeometricActions.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumGeometricActions.UNDEFINED;
	}

	public static class Deserializer extends JsonDeserializer<EnumGeometricActions> {

		@Override
		public EnumGeometricActions deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumGeometricActions.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case KEEP_LEFT_GEOMETRY: return "Keep Left Geometry";
        case KEEP_RIGHT_GEOMETRY: return "Keep Right Geometry";
        case KEEP_BOTH_GEOMETRIES: return "Keep Both Geometries";
        case KEEP_MORE_POINTS: return "Keep More Points";
        case KEEP_MORE_POINTS_AND_SHIFT: return "Keep More Points and Shift";    
        case SHIFT_LEFT_GEOMETRY: return "Shift Left Geometry";
        case SHIFT_RIGHT_GEOMETRY: return "Shift Right Geometry";
        default: throw new IllegalArgumentException();
      }
    }
}