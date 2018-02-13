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

    /**
     * Undefined value of the fusion action.
     *//**
     * Undefined value of the fusion action.
     */
    UNDEFINED(0), 

    /**
     * Keeps the model of the entity from the left source dataset.
     */
    KEEP_LEFT(1), 

    /**
     * Keeps the model of the entity from the right source dataset.
     */
    KEEP_RIGHT(2), 

    /**
     * Keeps both models of the entity from left and right source datasets.
     */
    KEEP_BOTH(3), 

    /**
     * Keeps the geometry containing more points than the other.
     */
    KEEP_MORE_POINTS(4),

    /**
     * Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry.
     */
    KEEP_MORE_POINTS_AND_SHIFT(5),

    /**
     * Shifts the geometry of the left source entity to the centroid of the right.
     */
    SHIFT_LEFT_GEOMETRY(6),

    /**
     * Shifts the geometry of the right source entity to the centroid of the left.
     */
    SHIFT_RIGHT_GEOMETRY(7),
    
    /**
     * Rejects the link, that is the fusion action. 
     * If a new fused dataset is chosen as output, none of the respective models of the entities appear in the final dataset.
     */      
    REJECT_LINK(8),
    
    /**
     * Concatenates the two literals and keeps them in the same property.
     */      
    CONCATENATE(9),
    
    /**
     * Keeps the model of the entity with the longest value. 
     */      
    KEEP_LONGEST(10),
    
    /**
     * Keeps the default action, but marks the value as ambiguous in the model using a statement with the POI's URI.
     */    
    ACCEPT_MARK_AMBIGUOUS(11),
    
    /**
     * Rejects the link, but marks the value as ambiguous in the model using a statement with the POI's URI.
     */       
    REJECT_MARK_AMBIGUOUS(12);
    
	private final int value;

	private EnumFusionAction(int value) {
		this.value = value;
	}
    
    /**
     * Returns the integer value of the action.
     * @return
     */
    public int getValue() {
		return this.value;
	}

	private static final Map<Integer, EnumFusionAction> intToTypeMap = new HashMap<>();
	static {
		for (EnumFusionAction type : EnumFusionAction.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

    /**
     * Returns the EnumFusionAction object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the action.
     * @return the type of the action.
     */
    public static EnumFusionAction fromInteger(int value) {
		EnumFusionAction type = intToTypeMap.get(value);
		if (type == null)
			return EnumFusionAction.UNDEFINED;
		return type;
	}

    /**
     * Returns the EnumFusionAction object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value
     * @return
     */
    public static EnumFusionAction fromString(String value) {
		for (EnumFusionAction item : EnumFusionAction.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumFusionAction.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumFusionAction> {
        
        /**
         * Deserializes the EnumDatasetAction
         * 
         * @param parser the Json parser
         * @param context the deserialization context
         * @return the EnumDatasetAction
         * @throws java.io.IOException
         * @throws com.fasterxml.jackson.core.JsonProcessingException
         */
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
        case KEEP_LEFT: return "keep-left";
        case KEEP_RIGHT: return "keep-right";
        case CONCATENATE: return "concatenate";
        case KEEP_LONGEST: return "keep-longest";        
        case KEEP_BOTH: return "keep-both";
        case KEEP_MORE_POINTS: return "keep-more-points";
        case KEEP_MORE_POINTS_AND_SHIFT: return "keep-more-points-and-shift";
        case SHIFT_LEFT_GEOMETRY: return "shift-left-geometry";
        case SHIFT_RIGHT_GEOMETRY: return "shift-right-geometry";
        case REJECT_LINK: return "reject-link";
        case ACCEPT_MARK_AMBIGUOUS: return "accept-mark-ambiguous";
        case REJECT_MARK_AMBIGUOUS: return "reject-mark-ambiguous";
        default: throw new IllegalArgumentException();
      }
    }     
}
