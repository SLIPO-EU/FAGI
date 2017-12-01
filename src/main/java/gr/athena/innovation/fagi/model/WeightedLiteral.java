package gr.athena.innovation.fagi.model;

import java.util.Map;

/**
 *
 * @author nkarag
 */
public class WeightedLiteral {

    private String baseLiteral;
    private double baseWeight;
    
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
    
    public void putTerm(String term, double weight){ 
		specialTermWeight.put(term, weight);
	}

    public void getTermWeight(String term, double weight){ 
		specialTermWeight.get(term);
	}
    
    public Map<String, Double> getSpecialTermWeight() {
        return specialTermWeight;
    }

    public void setSpecialTermWeight(Map<String, Double> specialTermWeight) {
        this.specialTermWeight = specialTermWeight;
    }



}
