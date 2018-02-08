package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.utils.Namespace;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class RDFStatisticsCollector implements StatisticsCollector{

    private static final Logger logger = LogManager.getLogger(RDFStatisticsCollector.class);

    @Override
    public StatisticsContainer collect(){

        StatisticsContainer container = new StatisticsContainer();

        StatisticResultPair distinctProperties = countDistinctProperties();
        container.setDistinctProperties(distinctProperties);
        
        StatisticResultPair nonEmptyDates = countNonEmptyDates();
        container.setNonEmptyDates(nonEmptyDates);
        
        StatisticResultPair percentageOfKnownFormats = calculatePercentageOfPrimaryDateFormats();
        container.setPercentageOfDateKnownFormats(percentageOfKnownFormats);
        
        return container;
    }

    private StatisticResultPair countDistinctProperties(){
        
        int distinctPropertiesA = SparqlRepository.countDistinctProperties(LeftModel.getLeftModel().getModel());
        int distinctPropertiesB = SparqlRepository.countDistinctProperties(RightModel.getRightModel().getModel());
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA, distinctPropertiesB);
        pair.setName("Distinct Properties");

        return pair;
    }

    private StatisticResultPair countNonEmptyDates(){

        int datesA = SparqlRepository.countProperty(LeftModel.getLeftModel().getModel(), Namespace.DATE);
        int datesB = SparqlRepository.countProperty(RightModel.getRightModel().getModel(), Namespace.DATE);
        StatisticResultPair pair = new StatisticResultPair(datesA, datesB);
        pair.setName("Non empty Dates");
        
        return pair;
    }

    private StatisticResultPair calculatePercentageOfPrimaryDateFormats(){

        Model leftModel = LeftModel.getLeftModel().getModel();
        Model rightModel = RightModel.getRightModel().getModel();
        String date = Namespace.DATE;

        int totalDatesA = SparqlRepository.countProperty(leftModel, date);

        IsDatePrimaryFormat isDatePrimaryFormat = new IsDatePrimaryFormat();
        
        int knownFormatCounter = 0;
        NodeIterator objectsA = SparqlRepository.getObjectsOfProperty(date, leftModel);
        while(objectsA.hasNext()){
            RDFNode node = objectsA.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    knownFormatCounter++;
                }       
            }
        }
        
        Double percentA = knownFormatCounter / (double) totalDatesA;
        
        knownFormatCounter = 0;
        
        int totalDatesB = SparqlRepository.countProperty(rightModel, date);

        NodeIterator objectsB = SparqlRepository.getObjectsOfProperty(date, rightModel);
        while(objectsB.hasNext()){
            RDFNode node = objectsB.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    knownFormatCounter++;
                }       
            }
        }
        
        Double percentB = knownFormatCounter / (double) totalDatesB;

        StatisticResultPair pair = new StatisticResultPair(percentA.intValue(), percentB.intValue());
        
        pair.setName("Percentage of primary date formats");
        return pair;
    }
    
    private StatisticResultPair calculatePercentageOfNonEmptyProperties(){

        //count percentage of non empty properties of each poi

        return null;
    }
}
