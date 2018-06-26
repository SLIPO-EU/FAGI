package gr.athena.innovation.fagi.learning;

import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import gr.athena.innovation.fagi.specification.Configuration;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author nkarag
 */
public class Trainer {

    private final Configuration configuration;
    
    public Trainer(Configuration configuration){
        this.configuration = configuration;
    }
    
    public void train() throws FileNotFoundException{
        
        
        //change void to custom result object in order to pass it down to 'extractFeatures'
        parseTrainingSet();
        
        //change void to custom pair-feature object in order to pass it down to 'exportToFile'
        extractFeatures();
        
        exportToFile();
        
    }

    private void parseTrainingSet() throws FileNotFoundException {
        
        //csv path containing the train set entities.
        String path = "";
        String cvsSplitBy = "\\^";
        
        String line;

        BufferedReader br = new BufferedReader(new FileReader(path));

        try {
        
            int l = 0;
            while ((line = br.readLine()) != null) {

                //skip first two lines of csv
                if (l < 2) {
                    l++;
                    continue;
                }

                String[] spl = line.split(cvsSplitBy);

                //StringBuffer sb = new StringBuffer("");
                if (spl.length < 22) {
                    continue;
                }
                String idA = spl[0];
                String idB = spl[1];

                String distanceMeters = spl[2];

                String nameA = spl[3];
                String nameB = spl[4];
                String nameFusionAction = spl[5];

                String streetA = spl[6];
                String streetB = spl[7];
                String streetFusionAction = spl[8];

                String streetNumberA = spl[9];
                String streetNumberB = spl[10];

                String phoneA = spl[11];
                String phoneB = spl[12];
                String phoneFusionAction = spl[13];

                String emailA = spl[14];
                String emailB = spl[15];
                String emailFusionAction = spl[16];

                String websiteA = spl[17];
                String websiteB = spl[18];
                String websiteFusionAction = spl[19];

                String score = spl[20];
                String names1 = spl[21];
                String acceptance = spl[22];
                
                //implement method to deal with the above fields and keep them in a pair object
                createTrainingPair();
                
                l++;
            }
        } catch(IOException | RuntimeException ex){  
            throw new ApplicationException(ex.getMessage());
        }
    }

    private void extractFeatures() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void exportToFile() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void createTrainingPair() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private WeightedPairLiteral getWeightedPair(String a, String b) throws IOException{
        
        //override locale if needed
        Locale locale = configuration.getLocale();
        
        //Load resources
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        
        //get special terms from custom file (this should be changed to use 'getSpecialTerms' when we have extracted the final list) 
        String specialTermsPath = "";
        Set<String> specialTerms = resourceFileLoader.getSpecialTermsFromPath(specialTermsPath);

        TermResolver.setTerms(specialTerms);        
        
        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        NormalizedLiteral normA = normalizer.getNormalizedLiteral(a, b, locale);
        NormalizedLiteral normB = normalizer.getNormalizedLiteral(b, a, locale);
        
        AdvancedGenericNormalizer advancedNormalizer = new AdvancedGenericNormalizer();
        
        WeightedPairLiteral weightedPair = advancedNormalizer.getWeightedPair(normA, normB, locale);

        return weightedPair;
    }
}
