package gr.athena.innovation.fagi.specification;

import gr.athena.innovation.fagi.exception.WrongInputException;
import java.util.HashMap;
import java.util.Map;

/**
 * Dataset enumeration class.
 * 
 * @author nkarag
 */
public enum EnumDataset {
    LEFT(0), RIGHT(1);
    
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

		return type;
	}

	public static EnumDataset fromString(String value) throws WrongInputException {
		for (EnumDataset item : EnumDataset.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		throw new WrongInputException("Wrong input for dataset. " + value);
	}
    
    @Override
    public String toString() {
        switch(this) {
            case LEFT: return "left";
            case RIGHT: return "right";
            default: return "default";
        }
    }    
}
