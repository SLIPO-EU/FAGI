/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsSameCustomNormalizeTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCustomNormalizeTest.class);
    
    /**
     * Test of evaluate method, of class IsSameCustomNormalize.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        Configuration.getInstance().setSimilarity("jarowinkler");
        String text1 = "Hotel Café ExampleName ";
        Literal literalA = ResourceFactory.createStringLiteral(text1);
        
        String text2 = "Test ExampleName-another Café";
        Literal literalB = ResourceFactory.createStringLiteral(text2);

        String threshold = "0.9";
        IsSameCustomNormalize instance = new IsSameCustomNormalize();
        boolean expResult = true;
        boolean result = instance.evaluate(literalA, literalB, threshold);
        assertEquals(expResult, result);

    }

    /**
     * Test of getName method, of class IsSameCustomNormalize.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsSameCustomNormalize instance = new IsSameCustomNormalize();
        String expResult = SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
