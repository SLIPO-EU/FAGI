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
     *
     */
    UNDEFINED(0),
    /**
     * Keeps the model of the entity from the left source dataset.
     */
    KEEP_LEFT(1),
    /**
     * Keeps the model of the entity from the left source dataset and marks the property being fused as ambiguous.
     */
    KEEP_LEFT_MARK(2),    
    /**
     * Keeps the model of the entity from the right source dataset.
     */
    KEEP_RIGHT(3),
    /**
     * Keeps the model of the entity from the right source dataset and marks the property being fused as ambiguous.
     */
    KEEP_RIGHT_MARK(4),      
    /**
     * Keeps both models of the entity from left and right source datasets.
     */
    KEEP_BOTH(5),
    /**
     * Keeps both models of the entity from left and right source datasets and marks the property being fused as ambiguous.
     */
    KEEP_BOTH_MARK(6),    
    /**
     * Keeps the model of the entity with the longest value.
     */
    KEEP_LONGEST(7),
    /**
     * Keeps the model of the entity with the longest value and marks it ambiguous.
     */
    KEEP_LONGEST_MARK(8),    
    /**
     * Concatenates the two literals and keeps them in the same property.
     */
    CONCATENATE(9),
    /**
     * Concatenates the two literals into a single value of the same property and marks the property as ambiguous. 
     */
    CONCATENATE_MARK(10),    
    /**
     * Keeps the most recent model, based on the dataset dates provided in the specification.
     */
    KEEP_MOST_RECENT(11),
    /**
     * Keeps the most recent model, based on the dataset dates provided in the specification. Marks property as ambiguous.
     */
    KEEP_MOST_RECENT_MARK(12),    
    /**
     * Keeps the geometry containing more points than the other.
     */
    KEEP_MORE_POINTS(13),
    /**
     * Keeps the geometry containing more points than the other. Marks the property as ambiguous.
     */
    KEEP_MORE_POINTS_MARK(14),    
    /**
     * Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry.
     */
    KEEP_MORE_POINTS_AND_SHIFT(15),
    /**
     * Keeps the geometry with more points and shifts its centroid to the centroid of the other geometry. Marks the property as ambiguous.
     */
    KEEP_MORE_POINTS_AND_SHIFT_MARK(16),    
    /**
     * Shifts the geometry of the left source entity to the centroid of the right.
     */
    SHIFT_LEFT_GEOMETRY(17),
    /**
     * Shifts the geometry of the left source entity to the centroid of the right. Marks the property as ambiguous.
     */
    SHIFT_LEFT_GEOMETRY_MARK(18),    
    /**
     * Shifts the geometry of the right source entity to the centroid of the left.
     */
    SHIFT_RIGHT_GEOMETRY(19),
    /**
     * Shifts the geometry of the right source entity to the centroid of the left. Marks the property as ambiguous.
     */
    SHIFT_RIGHT_GEOMETRY_MARK(20),    
    /**
     * Produces a geometry collection from the given geometries.
     */
    CONCATENATE_GEOMETRY(21),
    /**
     * Produces a geometry collection from the given geometries. Marks the property as ambiguous.
     */
    CONCATENATE_GEOMETRY_MARK(22);    

    private final int value;

    private EnumFusionAction(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value of the action.
     *
     * @return The value
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
        if (type == null) {
            return EnumFusionAction.UNDEFINED;
        }
        return type;
    }

    /**
     * Returns the EnumFusionAction object from its String value or UNDEFINED if the type does not exist.
     *
     * @param value The string value.
     * @return The fusion action.
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
         * Deserializes the EnumFusionAction
         *
         * @param parser the Json parser
         * @param context the deserialization context
         * @return the EnumFusionAction.
         * @throws java.io.IOException I/O exception.
         * @throws com.fasterxml.jackson.core.JsonProcessingException Json processing exception.
         */
        @Override
        public EnumFusionAction deserialize(JsonParser parser, DeserializationContext context) throws IOException,
                JsonProcessingException {
            return EnumFusionAction.fromString(parser.getValueAsString());
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case UNDEFINED:
                return "undefined";
            case KEEP_LEFT:
                return "keep-left";
            case KEEP_LEFT_MARK:
                return "keep-left-mark-ambiguous";             
            case KEEP_RIGHT:
                return "keep-right";
            case KEEP_RIGHT_MARK:
                return "keep-right-mark-ambiguous";
            case KEEP_BOTH:
                return "keep-both";
            case KEEP_BOTH_MARK:
                return "keep-both-mark-ambiguous";                
            case KEEP_LONGEST:
                return "keep-longest";
            case KEEP_LONGEST_MARK:
                return "keep-longest-mark-ambiguous";                
            case KEEP_MOST_RECENT:
                return "keep-most-recent";
            case KEEP_MOST_RECENT_MARK:
                return "keep-most-recent-mark-ambiguous";                
            case CONCATENATE:
                return "concatenate";
            case CONCATENATE_MARK:
                return "concatenate-mark-ambiguous";                
            case CONCATENATE_GEOMETRY:
                return "concatenate-geometry";
            case CONCATENATE_GEOMETRY_MARK:
                return "concatenate-geometry-mark-ambiguous";                
            case KEEP_MORE_POINTS:
                return "keep-more-points";
            case KEEP_MORE_POINTS_MARK:
                return "keep-more-points-mark-ambiguous";                
            case KEEP_MORE_POINTS_AND_SHIFT:
                return "keep-more-points-and-shift";
            case KEEP_MORE_POINTS_AND_SHIFT_MARK:
                return "keep-more-points-and-shift-mark-ambiguous";                
            case SHIFT_LEFT_GEOMETRY:
                return "shift-left-geometry";
            case SHIFT_LEFT_GEOMETRY_MARK:
                return "shift-left-geometry-mark-ambiguous";                
            case SHIFT_RIGHT_GEOMETRY:
                return "shift-right-geometry";
            case SHIFT_RIGHT_GEOMETRY_MARK:
                return "shift-right-geometry-mark-ambiguous";                
            default:
                throw new IllegalArgumentException();
        }
    }
}
