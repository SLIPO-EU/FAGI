package gr.athena.innovation.fagi.model;

import java.util.Map;

/**
 *
 * @author nkarag
 */
public class WeightedLiteral {

    private String baseLiteral;
    private double baseWeight;
    private String misMatched;
    private double misMatchedWeight;
    
    private Map<String, Double> specialTermWeight;

    public String getBaseLiteral() {
        return baseLiteral;
    }

    public void setBaseLiteral(String baseLiteral) {
        this.baseLiteral = baseLiteral;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    public void putTerm(String term, double weight) {
        specialTermWeight.put(term, weight);
    }

    public void getTermWeight(String term, double weight) {
        specialTermWeight.get(term);
    }

    public Map<String, Double> getSpecialTermWeight() {
        return specialTermWeight;
    }

    public void setSpecialTermWeight(Map<String, Double> specialTermWeight) {
        this.specialTermWeight = specialTermWeight;
    }


    public String getMisMatched() {
        return misMatched;
    }

    public void setMisMatched(String misMatched) {
        this.misMatched = misMatched;
    }

    public double getMisMatchedWeight() {
        return misMatchedWeight;
    }

    public void setMisMatchedWeight(double misMatchedWeight) {
        this.misMatchedWeight = misMatchedWeight;
    }
    
    public String getTermsLiteral() {

        StringBuilder builder = new StringBuilder();
        specialTermWeight.keySet().stream().forEach((key) -> {
            builder.append(key);
        });

        String specialTerms = builder.toString();

        return specialTerms;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        specialTermWeight.keySet().stream().forEach((key) -> {
            builder.append(key);
        });
        String specialTerms = builder.toString();

        return baseLiteral + " " + specialTerms;
    }

}
