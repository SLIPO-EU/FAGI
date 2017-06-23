package gr.athena.innovation.fagi.core.specification;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumFuseIntoDataset {
    DEFAULT(0), LEFT(1), RIGHT(2), NEW(3);
    
	private final int value;

	private EnumFuseIntoDataset(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	
    private static final Map<Integer, EnumFuseIntoDataset> intToTypeMap = new HashMap<>();    
	static {
		for (EnumFuseIntoDataset type : EnumFuseIntoDataset.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
    
	public static EnumFuseIntoDataset fromInteger(int value) {
		EnumFuseIntoDataset type = intToTypeMap.get(value);
		if (type == null)
			return EnumFuseIntoDataset.DEFAULT;
		return type;
	}

	public static EnumFuseIntoDataset fromString(String value) {
		for (EnumFuseIntoDataset item : EnumFuseIntoDataset.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumFuseIntoDataset.DEFAULT;
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
