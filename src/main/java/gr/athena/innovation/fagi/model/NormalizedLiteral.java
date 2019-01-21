package gr.athena.innovation.fagi.model;

/**
 * Class container for a normalized literal.
 * 
 * @author nkarag
 */
public class NormalizedLiteral {

    private String literal;
    private String normalized;
    private boolean isNormalized;

    /**
     * The literal as a string.
     * 
     * @return the literal as a string.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Set the literal as a string.
     * 
     * @param literal the literal as a string.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

    /**
     * Return the normalized value as a string.
     * 
     * @return the normalized value.
     */
    public String getNormalized() {
        return normalized;
    }

    /**
     * Set the normalized value as a string.
     * 
     * @param normalized the normalized value.
     */
    public void setNormalized(String normalized) {
        this.normalized = normalized;
    }

    /**
     * Check if this literal is already normalized.
     * 
     * @return true if the literal is normalized, false otherwise.
     */
    public boolean isIsNormalized() {
        return isNormalized;
    }

    /**
     * Set the current literal as normalized.
     * 
     * @param isNormalized set the literal as normalized.
     */
    public void setIsNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }
}
