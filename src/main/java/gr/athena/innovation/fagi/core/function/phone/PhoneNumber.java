package gr.athena.innovation.fagi.core.function.phone;

/**
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
    public boolean hasPlus() {
        return hasPlus;
    }

    public void setHasPlus(boolean hasPlus) {
        this.hasPlus = hasPlus;
    }
    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public boolean isUnknownFormat() {
        return unknownFormat;
    }

    public void setUnknownFormat(boolean unknownFormat) {
        this.unknownFormat = unknownFormat;
    }

    public boolean hasCountryCode() {
        return hasCountryCode;
    }

    public void setHasCountryCode(boolean hasCountryCode) {
        this.hasCountryCode = hasCountryCode;
    }

    public String getNumericalValue() {
        return numericalValue;
    }

    public void setNumericalValue(String numericalValue) {
        this.numericalValue = numericalValue;
    }
}
