package gr.athena.innovation.fagi.core.specification;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumTargetDataset {
    DEFAULT(0), LEFT(1), RIGHT(2), NEW(3);
    
	private final int value;

	private EnumTargetDataset(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	
    private static final Map<Integer, EnumTargetDataset> intToTypeMap = new HashMap<>();    
	static {
		for (EnumTargetDataset type : EnumTargetDataset.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
    
	public static EnumTargetDataset fromInteger(int value) {
		EnumTargetDataset type = intToTypeMap.get(value);
		if (type == null)
			return EnumTargetDataset.DEFAULT;
		return type;
	}

	public static EnumTargetDataset fromString(String value) {
		for (EnumTargetDataset item : EnumTargetDataset.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumTargetDataset.DEFAULT;
	}
    
    @Override
    public String toString() {
      switch(this) {
        case DEFAULT: return "Default";
        case LEFT: return "Left";
        case RIGHT: return "Right";
        case NEW: return "New";
        default: return "New";
      }
    }    
}
