package gr.athena.innovation.fagi.core.rule.model;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
    public final class Function{

        private static final Logger logger = LogManager.getLogger(Function.class);
        private String name;
        private String[] parameters;
        private int parameterCount;

        public Function(String functionString){

            if(StringUtils.isBlank(functionString)){
                logger.fatal("Something went wrong parsing a function from " + SpecificationConstants.RULES_XML);
                throw new RuntimeException();
            }
            
            if(functionString.contains("(") && functionString.contains(")")){

                String[] parts = functionString.split("\\(");
                setName(parts[0].toLowerCase());
                String parenthesis = parts[1];

                String[] commas = parenthesis.split("\\)");

                if(commas[0].contains(",")){
                    String[] params = commas[1].split(",");
                    setParameters(params);
                } else {
                    //single parameter:
                    setParameters(commas);
                }

                logger.trace("Function parameters: " + Arrays.toString(commas));

            } else {
                logger.fatal("Function " + functionString + " is malformed (Parenthesis missing). Check rules.xml file!");
                throw new IllegalArgumentException();                
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getParameters() {
            return parameters;
        }

        public void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        public int getParameterCount() {
            return parameterCount;
        }

        public void setParameterCount(int parameterCount) {
            this.parameterCount = parameterCount;
        }
    }