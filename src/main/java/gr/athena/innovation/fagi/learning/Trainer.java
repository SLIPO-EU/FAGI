package gr.athena.innovation.fagi.learning;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Class preparing the training process.
 * 
 * @author nkarag
 */
public class Trainer {

    private final Configuration configuration;

    public Trainer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void train() throws FileNotFoundException {
        parseTrainingSet(configuration.getLocale(), configuration.getPropertyFrequencyA(), configuration.getPropertyFrequencyB());
    }

    private void parseTrainingSet(Locale locale, String frequencyPathA, String frequencyPathB) throws FileNotFoundException {

        ArrayList<String> freqA = ReadFrequent.getFrequencies(frequencyPathA, 100, 3); //input freq
        //ArrayList<String> freqB = ReadFrequent.getFrequencies(a_path + "files/nameValue.freq.txt", 100, 3); //input freqB
        //ArrayList<String> category = ReadFrequent.getFrequencies(a_path + "files/categoryFrequencies.txt", 30, 3); //input cat frequencies

        String cvsSplitBy = "\\^";
        String line;
        BufferedReader br = new BufferedReader(new FileReader(configuration.getTrainingSetCsvPath()));
        ArrayList<Features> featuresList = new ArrayList<>();

        try {
            int index = -2;
            while ((line = br.readLine()) != null) {

                //skip first two lines of csv
                if (index < 0) {
                    index++;
                    continue;
                }

                String[] tokenArray = line.split(cvsSplitBy);
                if (tokenArray.length < 22) {
                    continue;
                }
                
                TrainingSet trainingSet = new TrainingSet(tokenArray);

                Features features = new Features();

                String idA = trainingSet.getIdA();
                //b not used in this version
                //String idB = trainingSet.getIdA();
                String nameA = trainingSet.getNameA();
                String nameB = trainingSet.getNameB();
                String frequenciesA = " " + String.join(" ", freqA) + " ";
                String acceptance = trainingSet.getAcceptance();
                String fusionAction = trainingSet.getNameFusionAction();
                
                //todo: replace freqA with freqB and evaluate if needed
                features.setFeatures(idA, idA, nameA, nameB, frequenciesA, frequenciesA,locale, acceptance, fusionAction);

                features.setphoneFeatures(trainingSet.getPhoneA(), trainingSet.getPhoneB());
                features.setaddrFeature(trainingSet.getStreetNumberA(), trainingSet.getStreetNumberB());
                features.setaddrNameFeature(trainingSet.getStreetA(), trainingSet.getStreetB());
                
                featuresList.add(index, features);

                index++;
            }
        } catch (IOException | RuntimeException ex) {
            throw new ApplicationException(ex.getMessage());
        }

        try {

            exportToFile(featuresList);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void exportToFile(ArrayList<Features> features) throws IOException {
        String outputFilename = configuration.getOutputDir() + SpecificationConstants.Config.FEATURES_CSV;
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(outputFilename, true));
        Field[] fields = Features.class.getDeclaredFields();
        
        for (Field field : fields) {
            writer.append(field.getName() + ", ");
        }
        
        writer.newLine();

        for (Features key : features) {
            String value = key.toString();
            writer.append(value);
            writer.newLine();
        }

        writer.close();
    }
}