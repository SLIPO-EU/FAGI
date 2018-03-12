package gr.athena.innovation.fagi.specification;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumDataset {
    DEFAULT(0), LEFT(1), RIGHT(2), NEW(3);
    
	private final int value;

	private EnumDataset(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	
    private static final Map<Integer, EnumDataset> intToTypeMap = new HashMap<>();    
	static {
		for (EnumDataset type : EnumDataset.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
    
	public static EnumDataset fromInteger(int value) {
		EnumDataset type = intToTypeMap.get(value);
		if (type == null)
			return EnumDataset.DEFAULT;
		return type;
	}

	public static EnumDataset fromString(String value) {
		for (EnumDataset item : EnumDataset.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumDataset.DEFAULT;
	}
    
    @Override
    public String toString() {
        switch(this) {
            case DEFAULT: return "default";
            case LEFT: return "left";
            case RIGHT: return "right";
            case NEW: return "new";
            default: return "new";
        }
    }    
}
