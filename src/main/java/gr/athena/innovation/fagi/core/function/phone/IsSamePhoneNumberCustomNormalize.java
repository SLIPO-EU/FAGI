package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumberCustomNormalize  implements IFunction, IFunctionTwoParameters{
    
    /**
     * Checks if two telephone numbers are the same using a custom normalization method.
     * 
     * @param number1
     * @param number2
     * @return true if the numbers are the same or extremely close and false otherwise.
     */
    @Override
    public boolean evaluate(String number1, String number2){
        
        //recognize exit code digits. Ignore them if any of the two does not contain them.
        
        boolean hasExitCode1 = recognizeExitCodeDigits(number1);
        boolean hasExitCode2 = recognizeExitCodeDigits(number2);
        
        String codeCategoryFormat1;
        String noCodeCategoryFormat1;
        String codeCategoryFormat2;
        String noCodeCategoryFormat2;
        
        int n;
        if(hasExitCode1 && !hasExitCode2){
            codeCategoryFormat1 = number1;
            noCodeCategoryFormat1 = number2;
            n = 0;
        } else if(hasExitCode2 && !hasExitCode1){
            codeCategoryFormat1 = number2;
            noCodeCategoryFormat1 = number1;
            n=1;
        } else if(hasExitCode1 && hasExitCode2){
            codeCategoryFormat1 = number2;
            codeCategoryFormat2 = number1;   
            n=2;
        } else {
            noCodeCategoryFormat1 = number1;
            noCodeCategoryFormat2 = number2;    
            n=3;
        }
        
        
        
        switch(n){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                
        }
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    

    private boolean recognizeExitCodeDigits(String number) {

        if(number.indexOf('+') == 0){
            if(number.indexOf("(") == 1 && number.indexOf(")") == 4){
                return true;
            }
        }
        
        return false;
    }

}
