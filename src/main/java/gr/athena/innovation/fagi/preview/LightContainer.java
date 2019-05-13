package gr.athena.innovation.fagi.preview;

/**
 * Light Statistics container
 * 
 * @author nkarag
 */
public class LightContainer {

    private Double averageGain;
    private Double averageConfidence;
    private Double maxGain;
    private String fusedPOIs;
    private String initialLinks;
    private String uniqueLinks;
    private String rejectedLinks;
    private String pathA;
    private String pathB;
    private String fusedPath;

    public Double getAverageGain() {
        return averageGain;
    }

    public void setAverageGain(Double averageGain) {
        this.averageGain = averageGain;
    }

    public Double getAverageConfidence() {
        return averageConfidence;
    }

    public void setAverageConfidence(Double averageConfidence) {
        this.averageConfidence = averageConfidence;
    }

    public Double getMaxGain() {
        return maxGain;
    }

    public void setMaxGain(Double maxGain) {
        this.maxGain = maxGain;
    }

    public String getFusedPOIs() {
        return fusedPOIs;
    }

    public void setFusedPOIs(String fusedPOIs) {
        this.fusedPOIs = fusedPOIs;
    }

    public String getInitialLinks() {
        return initialLinks;
    }

    public void setInitialLinks(String initialLinks) {
        this.initialLinks = initialLinks;
    }

    public String getUniqueLinks() {
        return uniqueLinks;
    }

    public void setUniqueLinks(String uniqueLinks) {
        this.uniqueLinks = uniqueLinks;
    }

    public String getRejectedLinks() {
        return rejectedLinks;
    }

    public void setRejectedLinks(String rejectedLinks) {
        this.rejectedLinks = rejectedLinks;
    }

    public String getFusedPath() {
        return fusedPath;
    }

    public void setFusedPath(String fusedPath) {
        this.fusedPath = fusedPath;
    }

    public String getPathA() {
        return pathA;
    }

    public void setPathA(String pathA) {
        this.pathA = pathA;
    }

    public String getPathB() {
        return pathB;
    }

    public void setPathB(String pathB) {
        this.pathB = pathB;
    }
}
