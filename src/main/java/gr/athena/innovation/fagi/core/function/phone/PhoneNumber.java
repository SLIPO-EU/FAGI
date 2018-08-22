package gr.athena.innovation.fagi.core.function.phone;

/**
 * Class describing a phone number.
 * 
 * @author nkarag
 */
public class PhoneNumber {
    
    private boolean unknownFormat = false;
    private String numericalValue;

    private boolean hasPlus = false;
    private boolean hasCountryCode = false;
    
    private String countryCode = null;
    private String areaCode = "";
    private String lineNumber = "";
    private String internal = "";
    
    @Override
    public String toString() {
        return "PhoneNumber{" + "unknownFormat=" + unknownFormat + ", hasPlus=" + hasPlus + ", hasCountryCode=" 
                + hasCountryCode + ", countryCode=" + countryCode + ", areaCode=" + areaCode + ", lineNumber=" 
                + lineNumber + ", internal=" + internal + '}';
    }

    /**
     * Contains the + symbol.
     * 
     * @return true if it contains the plus symbol, false otherwise.
     */
    public boolean hasPlus() {
        return hasPlus;
    }

    /**
     * Sets the value of the hasPlus field.
     * 
     * @param hasPlus true for phones that contain the plus symbol.
     */
    public void setHasPlus(boolean hasPlus) {
        this.hasPlus = hasPlus;
    }
    
    /**
     * Return the country code digits.
     * 
     * @return the country code digits.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Set the country code digits.
     * 
     * @param countryCode the country code digits.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Get the area code.
     * 
     * @return the area code.
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Set the area code.
     * 
     * @param areaCode the area code.
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Get the line number.
     * 
     * @return the line number.
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the line number value.
     * 
     * @param lineNumber the line number.
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Get the internal digits.
     * 
     * @return the internal digits.
     */
    public String getInternal() {
        return internal;
    }

    /**
     * Set the internal digits.
     * 
     * @param internal the internal digits.
     */
    public void setInternal(String internal) {
        this.internal = internal;
    }

    /**
     * Check if the format is unknown.
     * 
     * @return true if the format is unknown, false otherwise.
     */
    public boolean isUnknownFormat() {
        return unknownFormat;
    }

    /**
     * Set the value of the unknown format field.
     * 
     * @param unknownFormat the unknown format value.
     */
    public void setUnknownFormat(boolean unknownFormat) {
        this.unknownFormat = unknownFormat;
    }

    /**
     * Chech if the phone contains country code digits.
     * 
     * @return true if the phone contains country code digits, false otherwise.
     */
    public boolean hasCountryCode() {
        return hasCountryCode;
    }

    /**
     * Set the hasCountryCode value.
     * 
     * @param hasCountryCode the country code boolean value.
     */
    public void setHasCountryCode(boolean hasCountryCode) {
        this.hasCountryCode = hasCountryCode;
    }

    /**
     * Get the numerical value of the phone number.
     * 
     * @return the numerical value.
     */
    public String getNumericalValue() {
        return numericalValue;
    }

    /**
     * Set the numerical value of the phone number.
     * 
     * @param numericalValue the numerical value.
     */
    public void setNumericalValue(String numericalValue) {
        this.numericalValue = numericalValue;
    }
}
