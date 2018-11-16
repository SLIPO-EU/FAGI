package gr.athena.innovation.fagi.model;

import java.io.Serializable;

/**
 * Represents a link between two RDF nodes.
 * 
 * @author nkarag
 */
public class Link implements Serializable {
    private final String nodeA;
    private final String nodeB;
    private final String localNameA;
    private final String localNameB;
    private Float score;

    @Override
    public String toString() {
        return "Link{" + "nodeA=" + nodeA + ", nodeB=" + nodeB + ", score=" + score + '}';
    }
    
    /**
     * Constructs a new link between two nodes.
     * @param nodeA the first node
     * @param localNameA the URI of the first node
     * @param nodeB the second node
     * @param localNameB the URI of the second
     */
    public Link(final String nodeA, final String localNameA, final String nodeB, final String localNameB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.localNameA = localNameA;
        this.localNameB = localNameB;
    }

    /**
     * Constructs a new link between two nodes.
     * @param nodeA the first node
     * @param localNameA the URI of the first node
     * @param nodeB the second node
     * @param localNameB the URI of the second
     * @param score the link's confidence score
     */
    public Link(final String nodeA, final String localNameA, final String nodeB, final String localNameB, final Float score) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.localNameA = localNameA;
        this.localNameB = localNameB;
        this.score = score;
    }
    
    /**
     *
     * @return the first node
     */
    public String getNodeA() {
        return nodeA;
    }
    
    /**
     *
     * @return the second node
     */
    public String getNodeB() {
        return nodeB;
    }

    /**
     *
     * @return the first URI
     */
    public String getLocalNameA() {
        return localNameA;
    }
    
    /**
     *
     * @return the second URI
     */
    public String getLocalNameB() {
        return localNameB;
    }
    
    /**
     * 
     * @return a key identifying this link
     */
    public String getKey() {
        return nodeA + " <--> " + nodeB;
    } 

    /**
     * 
     * @return the score of the link
     */
    public Float getScore() {
        return score;
    }
}
