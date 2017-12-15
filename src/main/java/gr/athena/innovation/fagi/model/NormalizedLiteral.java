package gr.athena.innovation.fagi.model;

/**
 *
 * @author nkarag
 */
public class NormalizedLiteral {

    private String literal;
    private String normalized;
    private boolean isNormalized;

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public String getNormalized() {
        return normalized;
    }

    public void setNormalized(String normalized) {
        this.normalized = normalized;
    }

    public boolean isIsNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }
}
